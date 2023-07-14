package io.allteran.letschatbackend.dto;

import io.allteran.letschatbackend.domain.ChatChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatChannelDto {
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    @Schema(description = "Look up to ChatCategory.id in DB")
    private String categoryId;
    @NotBlank
    @Schema(description = "Look up to ChatLanguage.id in DB")
    private String languageId;
    private ChatChannel.Type type;
    @Schema(nullable = true)
    private String authorId;
    private LocalDateTime creationDate;
}
