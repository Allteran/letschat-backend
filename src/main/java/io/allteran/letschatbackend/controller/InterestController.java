package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.ChatLanguageDto;
import io.allteran.letschatbackend.dto.InterestDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.service.InterestService;
import io.allteran.letschatbackend.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/interest")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;

    @Operation(summary = "Get all interests from DB")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List all interests. It may be empty in case when there is no any of interest in DB. Response wrapped with GeneralResponse<InterestDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatLanguageDto.class))}
            )
    })
    @GetMapping(value = {"", "/"})
    public ResponseEntity<GeneralResponse<InterestDto>> findAll() {
        List<InterestDto> dtos = interestService.findAll().stream().map(EntityMapper::convertToDto).toList();
        String message = (dtos.isEmpty()) ? "There are no interests in DB" : "OK";
        return ResponseEntity.ok(new GeneralResponse<>(message, dtos));
    }
}
