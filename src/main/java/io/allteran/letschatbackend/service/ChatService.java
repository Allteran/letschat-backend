package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.dto.payload.ChatMessage;
import io.allteran.letschatbackend.repo.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class ChatService {
    @Value("${chat.message.status.sent-to-client}")
    private String STATUS_SENT_TO_CLIENT;
    @Value("${chat.message.status.error}")
    private String STATUS_ERROR;
    private final MessageRepo messageRepo;
    private final ChatChannelService channelService;
    private final UserService userService;
    @Qualifier("saveMongoExecutor")
    private final ExecutorService saveMongoExecutor;

    public ChatMessage joinChannel(ChatMessage message, String receiverId, String userId) {
        message.setSender(userId);
        message.setType(ChatMessage.Type.JOIN);
        message.setReceiver(receiverId);
        message.setCreationDate(LocalDateTime.now());
        message.setContent("User [ID: " + message.getSender() + "] has joined channel [ID: " + message.getReceiver() + "]");
        message.setStatus(STATUS_SENT_TO_CLIENT);
        saveMongoExecutor.execute(() -> {
            userService.addJoinedChannel(userId, receiverId);
            channelService.addJoinedUser(userId, receiverId);
        });
        return message;
    }

    public ChatMessage sendMessage(ChatMessage message, String senderId, String receiverId) {
        message.setReceiver(receiverId);
        message.setSender(senderId);
        if (message.getCreationDate() == null) {
            message.setCreationDate(LocalDateTime.now());
        }

        message.setType(ChatMessage.Type.PUBLIC);
        message.setStatus(STATUS_SENT_TO_CLIENT);
        saveMongoExecutor.execute(() -> messageRepo.save(message));
        return message;
    }
}
