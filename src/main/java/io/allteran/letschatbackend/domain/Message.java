package io.allteran.letschatbackend.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
    private String id;
    private String sender;
    private String receiver;
    private String content;
    private String status;
    private LocalDateTime creationDate;
    private MessageType type;

    public enum MessageType {
        JOIN, LEAVE, PUBLIC, PRIVATE;
    }
}
