package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "chat_language")
@Schema(name = "ChatLanguage", description = "Backend entity. Stands for describing ChatLanguage")
public class ChatLanguage {
    @Id
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    @Schema(description = "Code for language in ISO 639-2 format")
    @Size(min = 3, max = 3)
    private String code;
    private String emoji;
}
