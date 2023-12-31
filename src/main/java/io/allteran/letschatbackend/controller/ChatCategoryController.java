package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.ChatCategoryDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.ChatCategoryService;
import io.allteran.letschatbackend.util.EntityMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-category")
@RequiredArgsConstructor
public class ChatCategoryController {
    private final ChatCategoryService categoryService;
    @SneakyThrows
    @Operation(summary = "Get all ChatCategory")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Get all Chat Categories. Response wrapped with GeneralResponse<ChatCategoryDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatCategoryDto.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @GetMapping(path = {"", "/"})
    public ResponseEntity<GeneralResponse<ChatCategoryDto>> getAll() {
        List<ChatCategoryDto> data = categoryService.findAll().stream().map(EntityMapper::convertToDto).toList();
        String message = (data.isEmpty()) ? "There is no ChatCategory in DB" : "OK";
        return ResponseEntity.ok(new GeneralResponse<>(message, data));
    }

    @Operation(summary = "FOR ADMIN ONLY. Create new ChatCategory", description = "Chat category can be created only by admins. Name should be unique")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category were created successfully. Response wrapped with GeneralResponse<ChatCategoryDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatCategoryDto.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail. User error in Category creation. ",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @PostMapping("/protected/new")
    public ResponseEntity<GeneralResponse<ChatCategoryDto>> create(@RequestBody ChatCategoryDto body) {
        try {
            ChatCategoryDto createdDto = EntityMapper.convertToDto(
                    categoryService.create(EntityMapper.convertToEntity(body))
            );
            return ResponseEntity.ok(new GeneralResponse<>("SUCCESS", Collections.singletonList(createdDto)));
        } catch (EntityFieldException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }

    @Operation(summary = "FOR ADMIN ONLY. Delete ChatCategory")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Category was deleted successfullyResponse wrapped with GeneralResponse<ChatCategoryDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatCategoryDto.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error. ChatCategory with given ID does not exist",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @DeleteMapping("/protected/delete/{id}")
    public ResponseEntity<GeneralResponse<ChatCategoryDto>> delete(@PathVariable("id") String id) {
        try {
            categoryService.delete(id);
            return ResponseEntity.ok(new GeneralResponse<>("SUCCESS", Collections.emptyList()));
        } catch (NotFoundException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(400).body(new GeneralResponse<>("ERROR", Collections.emptyList()));
        }
    }
}
