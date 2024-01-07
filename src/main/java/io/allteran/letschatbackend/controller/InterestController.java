package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.converter.Converter;
import io.allteran.letschatbackend.domain.Interest;
import io.allteran.letschatbackend.dto.InterestDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.service.InterestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/v1/interest")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;
    private final Converter<InterestDto, Interest> interestConverter;

    @Operation(summary = "Get all interests from DB")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List all interests. It may be empty in case when there is no any of interest in DB. Response wrapped with GeneralResponse<InterestDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InterestDto.class))}
            )
    })
    @GetMapping(value = {"", "/"})
    public ResponseEntity<GeneralResponse<InterestDto>> findAll() {
        List<InterestDto> dtos = interestService.findAll().stream().map(interestConverter::convertToDTO).toList();
        String message = (dtos.isEmpty()) ? "There are no interests in DB" : "OK";
        return ResponseEntity.ok(new GeneralResponse<>(message, dtos));
    }

    @Operation(summary = "Create new interest in DB. Available only for admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Create single interest in DB",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InterestDto.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Interest were not created due to incorrect or duplicate fields"
            )
    })
    @PostMapping("/protected/create")
    public ResponseEntity<?> create(@RequestBody InterestDto dto) {
        try {
            Interest created = interestService.create(interestConverter.convertToEntity(dto));
            return ResponseEntity.ok(new GeneralResponse<>("SUCCESS", Collections.singletonList(created)));
        } catch (EntityFieldException ex) {
            log.error("Can't create Interest", ex);
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }
}
