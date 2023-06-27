package io.allteran.letschatbackend.domain;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(value = "chat_message")
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
