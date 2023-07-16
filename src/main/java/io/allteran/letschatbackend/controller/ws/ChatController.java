package io.allteran.letschatbackend.controller.ws;

import io.allteran.letschatbackend.domain.Message;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.MessageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
@Controller
public class ChatController {
    @Value("${chat.message.status.sent-to-client}")
    private String STATUS_SENT_TO_CLIENT;
    @Value("${chat.message.status.error}")
    private String STATUS_ERROR;


    //To join some channel - client should send simple MessageDto but with MessageType.JOIN
    //the MessageDto.sender is that who is logged in rn (we getting it from SecurityContextHolder
    @MessageMapping("/join/{id}")
    @SendTo("/channel/{id}")
    public MessageDto joinChannel(@DestinationVariable("id")String destId, MessageDto body) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        body.setSender(user.getId());
        body.setType(Message.MessageType.JOIN.name());
        body.setReceiver(destId);
        body.setCreationDate(LocalDateTime.now());
        body.setContent("User [ID: " + body.getSender() + "] has joined channel [ID: " + body.getReceiver() + "]");
        body.setStatus(STATUS_SENT_TO_CLIENT);
        return body;
    }

}
