package io.allteran.letschatbackend.dto.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteRegistrationRequest {
    private String login;
    private String name;
    private String languageId;
    private List<String> interests;
}
