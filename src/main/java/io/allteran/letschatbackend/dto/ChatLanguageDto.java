package io.allteran.letschatbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ChatLanguageDto", description = "DTO. Describes language of chat in frontend-backend relations")
public class ChatLanguageDto {
    private String id;
    private String name;
    private String code;
    private String emoji;
}
