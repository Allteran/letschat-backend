package io.allteran.letschatbackend.dto.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "DTO. Current DTO represent general response for some kind of entities")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeneralResponse<T> {
    private String message;
    private List<T> data;
}
