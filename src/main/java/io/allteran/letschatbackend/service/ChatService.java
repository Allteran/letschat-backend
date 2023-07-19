package io.allteran.letschatbackend.service;

import io.allteran.letschatbackend.dto.payload.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {
    @Value("${chat.message.status.sent-to-client}")
    private String STATUS_SENT_TO_CLIENT;
    @Value("${chat.message.status.error}")
    private String STATUS_ERROR;
    public ChatMessage joinChannel(ChatMessage message, String senderId, String receiverId) {
        message.setSender(senderId);
        message.setType(ChatMessage.Type.JOIN);
        message.setReceiver(receiverId);
        message.setCreationDate(LocalDateTime.now());
        message.setContent("User [ID: " + message.getSender() + "] has joined channel [ID: " + message.getReceiver() + "]");
        message.setStatus(STATUS_SENT_TO_CLIENT);
        return message;
    }

    public ChatMessage sendMessage(ChatMessage message, String senderId, String receiverId) {
        message.setReceiver(receiverId);
        message.setSender(senderId);
        if(message.getCreationDate() == null) {
            message.setCreationDate(LocalDateTime.now());
        }

        message.setType(ChatMessage.Type.PUBLIC);
        message.setStatus(STATUS_SENT_TO_CLIENT);
        return message;
    }
}
