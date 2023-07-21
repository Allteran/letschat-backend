package io.allteran.letschatbackend.dto.payload;

import com.fasterxml.jackson.annotation.JsonView;
import io.allteran.letschatbackend.view.Views;
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
    @JsonView(value = {Views.Public.class, Views.Internal.class, Views.Profile.class})
    private List<T> data;
}
