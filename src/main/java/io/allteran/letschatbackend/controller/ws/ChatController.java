package io.allteran.letschatbackend.controller.ws;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.payload.ChatMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
@Controller
public class ChatController {
    @Value("${chat.message.status.sent-to-client}")
    private String STATUS_SENT_TO_CLIENT;
    @Value("${chat.message.status.error}")
    private String STATUS_ERROR;


    //To join some channel - client should send simple MessageDto but with MessageType.JOIN
    //the MessageDto.sender is that who is logged in rn (we getting it from SecurityContextHolder
    @MessageMapping("/chat.join")
    @SendTo("/topic/chat-channel/{id}")
    public ChatMessage joinChannel(@DestinationVariable("id")String destId,
                                  @Payload ChatMessage body,
                                  SimpMessageHeaderAccessor headerAccessor) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        body.setSender(user.getId());
        body.setType(ChatMessage.Type.JOIN);
        body.setReceiver(destId);
        body.setCreationDate(LocalDateTime.now());
        body.setContent("User [ID: " + body.getSender() + "] has joined channel [ID: " + body.getReceiver() + "]");
        body.setStatus(STATUS_SENT_TO_CLIENT);
        headerAccessor.getSessionAttributes().put("userId", body.getSender());
        headerAccessor.getSessionAttributes().put("channelId", body.getReceiver());
        return body;
    }

    //Here we will just check message, maybe do some minor fixes
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chat-channel/{id}")
    public ChatMessage sendMessage(@DestinationVariable("id") String destId,
                                   @Payload ChatMessage body,
                                   SimpMessageHeaderAccessor headerAccessor) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if(userId == null) {
            userId = "";
        }
        if(!destId.equals(body.getReceiver())) {
            //TODO: mismatch destination and receiver. Those two should be equals
            System.out.println("we have a problem");
        }
        if(!user.getId().equals(userId)) {
            //TODO: mismatch logged in user and sender
            System.out.println("Problem #2");
        }
        //only if we are getting this field as null from frontend - we should set it with backend tools
        if(body.getCreationDate() == null) {
            body.setCreationDate(LocalDateTime.now());
        }
        body.setType(ChatMessage.Type.PUBLIC);
        body.setStatus(STATUS_SENT_TO_CLIENT);
        return body;
    }
}
