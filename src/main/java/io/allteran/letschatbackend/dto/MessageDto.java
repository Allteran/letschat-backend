package io.allteran.letschatbackend.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString(of = {"id", "sender", "receiver", "content", "status", "creationDate", "type"})
public class MessageDto implements Serializable {
    private String id;
    private String sender;
    private String receiver;
    private String content;
    private String status;
    private LocalDateTime creationDate;
    private String type;
}