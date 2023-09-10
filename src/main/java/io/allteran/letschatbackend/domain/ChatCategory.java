package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Schema(name = "ChatCategory", description = "Backend entity. Stands for different chat categories that can be expanded by admin user")
@Document(value = "chat_category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatCategory {
    @Id
    private String id;
    @NotBlank
    @Schema(description = "Field name should be unique as well")
    private String name;

    public ChatCategory(String name) {
        this.name = name;
    }
}
