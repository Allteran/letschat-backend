package io.allteran.letschatbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ChatLanguageDto", description = "DTO. Describes language of chat in frontend-backend relations")
public class ChatLanguageDto {
    private String id;
    private String name;
    private String code;
    private String emoji;
}
