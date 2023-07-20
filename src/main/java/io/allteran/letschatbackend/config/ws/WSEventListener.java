package io.allteran.letschatbackend.config.ws;

import io.allteran.letschatbackend.dto.payload.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WSEventListener {
    private final SimpMessageSendingOperations messageTemplate;

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        String channelId = (String) headerAccessor.getSessionAttributes().get("channelId");
        if(userId != null) {
            log.info("USER DISCONNECTED [ID = {}]", userId);
            var chatMessage = ChatMessage.builder()
                    .type(ChatMessage.Type.LEAVE)
                    .sender(userId)
                    .receiver(channelId)
                    .content("User [ID: " + userId + "] has leave the channel [ID: " + channelId + "]")
                    .build();

            messageTemplate.convertAndSend("/topic/chat-channel/" + channelId, chatMessage);
        }
    }
}
