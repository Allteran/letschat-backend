package io.allteran.letschatbackend.controller.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.allteran.letschatbackend.config.SecurityTestConfig;
import io.allteran.letschatbackend.config.ws.WebSocketConfig;
import io.allteran.letschatbackend.dto.payload.ChatMessage;
import io.allteran.letschatbackend.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(SecurityTestConfig.class)
public class ChatControllerIT {
	@Mock
	private JwtUtil jwtUtil;

	@Value(value="${local.server.port}")
	private int port;
	private static final String MESSAGE_MAPPING_PATH = "/topic/chat-channel/";
	private String SEND_TO_PATH = "/app/chat.join/{id}";
	private SockJsClient sockJsClient;
	private WebSocketStompClient stompClient;
	private final WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

	@BeforeEach
	public void setup() {
		List<Transport> transports = new ArrayList<>();
		transports.add(new WebSocketTransport(new StandardWebSocketClient()));
		this.sockJsClient = new SockJsClient(transports);

		this.stompClient = new WebSocketStompClient(sockJsClient);
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		MappingJackson2MessageConverter mappingConverter = new MappingJackson2MessageConverter();
		mappingConverter.setObjectMapper(mapper);
		this.stompClient.setMessageConverter(mappingConverter);

		jwtUtil = Mockito.mock(JwtUtil.class);
	}

	@Test
	//TODO: fix ChatController, cuz even with declared annotations User in controller is null, so SecurityContextHolder FROM CONTROLLER not working and IDK WHYYYY
	@WithMockUser(username = "testUser", authorities = {"USER", "ADMIN"})
	public void joinChannel_shouldJoinChannel() throws Exception {
		//given
		String destId = "destId";
		ChatMessage body = ChatMessage.builder()
				.id("messageId")
				.content("Message content")
				.creationDate(LocalDateTime.now())
				.sender("sender")
				.status("SENT_BY_CLIENT")
				.receiver(destId)
				.type(ChatMessage.Type.JOIN)
				.build();

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				System.out.println("SUBSCRIBING to: " + MESSAGE_MAPPING_PATH + destId);
				session.subscribe(MESSAGE_MAPPING_PATH + destId, new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						System.out.println("PAYLOAD MessageDto.class");
						return ChatMessage.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						System.out.println("handleFrame, payload = " + payload.toString());
						ChatMessage response = (ChatMessage) payload;
						try {
							assertEquals(body.getId(), response.getId());
						} catch (Throwable t) {
							System.out.println("assertEquals failed, error = " + t.getMessage());
							failure.set(t);
						} finally {
							System.out.println("DISCONNECT");
							session.disconnect();
							latch.countDown();
						}
					}
				});
				try {
					SEND_TO_PATH = SEND_TO_PATH.replace("{id}", destId);
					System.out.println("SENDING TO " + SEND_TO_PATH);
					session.send(SEND_TO_PATH, body);
				} catch (Throwable t) {
					t.printStackTrace();
					failure.set(t);
					latch.countDown();
				}
			}
		};
		this.stompClient.connect("ws://localhost:{port}" + WebSocketConfig.WS_ENDPOINT, this.headers, handler, this.port);

		//Wait for 3 seconds
		if (latch.await(5, TimeUnit.SECONDS)) {
			System.out.println("OK, MOVING ON");
			if (failure.get() != null) {
				System.out.println("failure.get != null");
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("ChatMessage not received");
		}

	}

	private class TestSessionHandler extends StompSessionHandlerAdapter {

		private final AtomicReference<Throwable> failure;

		public TestSessionHandler(AtomicReference<Throwable> failure) {
			this.failure = failure;
		}

		@Override
		public void handleFrame(StompHeaders headers, Object payload) {
			this.failure.set(new Exception(headers.toString()));
		}

		@Override
		public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
			this.failure.set(ex);
		}

		@Override
		public void handleTransportError(StompSession session, Throwable ex) {
			this.failure.set(ex);
		}
	}
}
