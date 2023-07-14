package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.ChatLanguageDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.service.ChatLanguageService;
import io.allteran.letschatbackend.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-language")
@RequiredArgsConstructor
public class ChatLanguageController {
    private final ChatLanguageService languageService;

    @Operation(summary = "Get all languages from DB")
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List all languages. It may be empty in case when there is no any of language in DB. Response wrapped with GeneralResponse<ChatLanguageDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatLanguageDto.class))}
            )
    })
    @GetMapping(path = {"/", ""})
    public ResponseEntity<GeneralResponse<ChatLanguageDto>> findAll() {
        List<ChatLanguageDto> languages = languageService.findAll().stream().map(EntityMapper::convertToDto).toList();
        String message = (languages.isEmpty()) ? "There is no ChatLanguage in DB" : "OK";
        return ResponseEntity.ok(new GeneralResponse<>(message, languages));
    }

    @Operation(summary = "FOR ADMIN ONLY. Create new ChatLanguage", description = "Every field should be unique")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ChatLanguage created successfully. Response will be wrapped with GeneralResponse<ChatLanguageDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatLanguageDto.class))}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error. ChatLanguage wasn't created, check error message",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))}
            )
    })
    @PostMapping("/protected/new")
    public ResponseEntity<GeneralResponse<ChatLanguageDto>> create(@RequestBody ChatLanguageDto body) {
        try {
            ChatLanguageDto createdDto = EntityMapper.convertToDto(
                    languageService.create(
                            EntityMapper.convertToEntity(body)
                    ));
            return ResponseEntity.ok(new GeneralResponse<>("OK", Collections.singletonList(createdDto)));
        } catch (EntityFieldException ex) {
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }

}
