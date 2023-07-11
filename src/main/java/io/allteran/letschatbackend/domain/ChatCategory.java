package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Schema(name = "ChatCategory", description = "Backend entity. Stands for different chat categories that can be expanded by admin user")
@Document(value = "chat_category")
@Data
public class ChatCategory {
    @Id
    private String id;
    @NotBlank
    @Schema(description = "Field name should be unique as well")
    private String name;
}
