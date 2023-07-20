package io.allteran.letschatbackend.controller.ws;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.payload.ChatMessage;
import io.allteran.letschatbackend.exception.AccessException;
import io.allteran.letschatbackend.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    //To join some channel - client should send simple MessageDto but with MessageType.JOIN
    //the MessageDto.sender is that who is logged in rn (we getting it from SecurityContextHolder
    @MessageMapping("/chat.join/{id}")
    @SendTo("/topic/chat-channel/{id}")
    public ChatMessage joinChannel(@DestinationVariable("id")String destId,
                                   @Payload ChatMessage body,
                                   SimpMessageHeaderAccessor headerAccessor) {
        ChatMessage modifiedMessage = chatService.joinChannel(body, destId);

        //we put channelId and userId to sessionAttributes for manipulate with that using HandlerInterceptor
        headerAccessor.getSessionAttributes().put("userId", modifiedMessage.getSender());
        headerAccessor.getSessionAttributes().put("channelId", modifiedMessage.getReceiver());
        return body;
    }

    //Here we will just check message and do some minor fixes
    @MessageMapping("/chat.sendMessage/{id}")
    @SendTo("/topic/chat-channel/{id}")
    public ChatMessage sendMessage(@DestinationVariable("id") String destId,
                                   @Payload ChatMessage body,
                                   SimpMessageHeaderAccessor headerAccessor) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if(userId == null) {
            throw new AccessException("User is not logged in");
        }
        return chatService.sendMessage(body, user.getId(), destId);
    }

    @SubscribeMapping("/topic/chat-channel/{id}")
    public void onJoinChannel(@DestinationVariable("id") String destId) {
        //so here we have to save counter of members of the channel

    }
}
