package io.allteran.letschatbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "DTO. Describes ChatCategory entity to interact between Frontend and Backend")
@Data
public class ChatCategoryDto {
    private String id;
    @NotBlank
    @Schema(description = "Field name should be unique as well")
    private String name;
}
