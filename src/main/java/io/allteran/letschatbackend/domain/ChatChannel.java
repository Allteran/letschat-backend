package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Schema(name = "ChatChannel", description = "Backend entity. Represents channel of chat or chat room")
@Document("chat_channel")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatChannel {
    @Id
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    @Schema(description = "Look up to ChatCategory.id in DB")
    private String categoryId;
    @NotBlank
    @Schema(description = "Look up to ChatLanguage.id in DB")
    private String languageId;
    private Type type;
    @NotBlank
    private String authorId;
    @Schema(description = "May be null")
    private LocalDateTime creationDate;

    public enum Type {
        PUBLIC, PRIVATE;
    }

}
