package io.allteran.letschatbackend.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder()
public class ChatMessage {
    private String id;
    private String sender;
    private String receiver;
    private String content;
    private String status;
    private LocalDateTime creationDate;
    private Type type;

    public enum Type {
        JOIN, LEAVE, PUBLIC, PRIVATE;
    }
}
