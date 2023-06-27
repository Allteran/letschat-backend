package io.allteran.letschatbackend.controller.ws;

import io.allteran.letschatbackend.domain.Message;
import io.allteran.letschatbackend.dto.MessageDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {
    @Value("${chat.message.status.sent-to-client}")
    private String STATUS_SENT_TO_CLIENT;
    @Value("${chat.message.status.error}")
    private String STATUS_ERROR;


    @MessageMapping("/join/{id}")
    @SendTo("/topic/{id}")
    public MessageDto joinChannel(@DestinationVariable("id")String destId, MessageDto body) {
        if(!Message.MessageType.JOIN.name().equals(body.getType())) {
            body.setStatus(STATUS_ERROR);
            body.setContent("Error: wrong message type: " + body.getType() + ". You should use type JOIN in join action");
            return body;
        }
        //additional validation and reseting fields
        body.setReceiver(destId);
        body.setContent("User [ID: " + body.getSender() + "] has joined channel [ID: " + body.getReceiver() + "]");
        body.setCreationDate(LocalDateTime.now());
        body.setStatus(STATUS_SENT_TO_CLIENT);
        return body;
    }

}
