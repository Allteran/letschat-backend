package io.allteran.letschatbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DTO. Display user interest")
public class InterestDto {
    private String id;
    private String name;
}
