package io.allteran.letschatbackend.controller.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.allteran.letschatbackend.config.WebSocketConfig;
import io.allteran.letschatbackend.dto.MessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
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
public class ChatControllerIT {

	@Value(value="${local.server.port}")
	private int port;
	private static final String SUBSCRIBE_PATH = "/channel/";
	private static final String SENDING_PATH = "/app/join/";
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
	}

	@Test
	public void joinChannel_shouldJoinChannel() throws Exception {
		//given
		String destId = "destId";
		MessageDto body = MessageDto.builder()
				.id("messageId")
				.content("Message content")
				.creationDate(LocalDateTime.now())
				.sender("sender")
				.status("SENT_BY_CLIENT")
				.receiver(destId)
				.type("JOIN")
				.build();

		final CountDownLatch latch = new CountDownLatch(1);
		final AtomicReference<Throwable> failure = new AtomicReference<>();

		StompSessionHandler handler = new TestSessionHandler(failure) {

			@Override
			public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
				System.out.println("SUBSCRIBING to: " + SUBSCRIBE_PATH + destId);
				session.subscribe(SUBSCRIBE_PATH + destId, new StompFrameHandler() {
					@Override
					public Type getPayloadType(StompHeaders headers) {
						System.out.println("PAYLOAD MessageDto.class");
						return MessageDto.class;
					}

					@Override
					public void handleFrame(StompHeaders headers, Object payload) {
						System.out.println("handleFrame, payload = " + payload.toString());
						MessageDto response = (MessageDto) payload;
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
					System.out.println("SENDING TO " + SENDING_PATH);
					session.send(SENDING_PATH + destId, body);
				} catch (Throwable t) {
					System.out.println("sending error: " + t.getMessage());
					failure.set(t);
					latch.countDown();
				}
			}
		};

		System.out.println("CONNECTING TO: " + "ws://localhost:{port}" + WebSocketConfig.WS_ENDPOINT);
		this.stompClient.connect("ws://localhost:{port}" + WebSocketConfig.WS_ENDPOINT, this.headers, handler, this.port);

		System.out.println("WAITING FOR 3 SECONDS");
		if (latch.await(3, TimeUnit.SECONDS)) {
			System.out.println("OK, MOVING ON");
			if (failure.get() != null) {
				System.out.println("failure.get != null");
				throw new AssertionError("", failure.get());
			}
		}
		else {
			fail("MessageDto not received");
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
