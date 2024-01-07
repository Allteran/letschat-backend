package io.allteran.letschatbackend.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document("interest")
@Schema(name = "Interest", description = "Backend entity. Display user's interest in general")
public class Interest {
    @Id
    private String id;
    private String name;
}
