package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.domain.ChatChannel;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.ChatChannelDto;
import io.allteran.letschatbackend.dto.payload.GeneralResponse;
import io.allteran.letschatbackend.exception.AccessException;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.exception.NotFoundException;
import io.allteran.letschatbackend.service.ChatChannelService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-channel")
@RequiredArgsConstructor
public class ChatChannelController {
    private final ChatChannelService channelService;

    @Operation(summary = "Find all chats")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Gets all ChatChannels from DB. It may be empty if there are no chats. Response wrapped with GeneralResponse<ChatChannelDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatChannelDto.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @GetMapping(value = {"public", "public/"})
    public ResponseEntity<GeneralResponse<ChatChannelDto>> findAll() {
        List<ChatChannelDto> data = channelService.findAll().stream().map(EntityMapper::convertToDto).toList();
        String message = (data.isEmpty()) ? "There is no ChatChannel i DB" : "OK";

        return ResponseEntity.ok(new GeneralResponse<>(message, data));
    }

    @Operation(summary = "Create new public chat")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. Public chat were created successfully. Response wrapped with GeneralResponse<ChatChannel>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatChannelDto.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Failed. User, ChatCategory or ChatLanguage not found.",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Failed. Some field of ChatChannelDto don't fit requirements.",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @PostMapping("/public/new")
    public ResponseEntity<GeneralResponse<ChatChannelDto>> createPublic(@RequestBody ChatChannelDto body) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            ChatChannelDto createdChannel = EntityMapper.convertToDto(
                    channelService.create(
                            EntityMapper.convertToEntity(body), currentUser, ChatChannel.Type.PUBLIC
                    )
            );
            return ResponseEntity.ok(new GeneralResponse<>("OK", Collections.singletonList(createdChannel)));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(404).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        } catch (EntityFieldException ex) {
            return ResponseEntity.status(401).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }

    @Operation(summary = "Update existing chat")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "OK. Channel DTO updated successfully. Response wrapped with GeneralResponse<ChatChannelDto>",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ChatChannelDto.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. Something was not found: existing ChatChannel, ChatCategory or ChatLanguage",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Fail. Some field if ChatChannelDto don't fit requirements",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @PutMapping("/public/edit/{id}")
    public ResponseEntity<GeneralResponse<ChatChannelDto>> update(@PathVariable("id") String channelId,
                                                                  @RequestBody ChatChannelDto body) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            ChatChannelDto updatedChannel = EntityMapper.convertToDto(
                    channelService.update(channelId, EntityMapper.convertToEntity(body), currentUser));
            return ResponseEntity.ok(new GeneralResponse<>("OK", Collections.singletonList(updatedChannel)));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(404).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        } catch (EntityFieldException ex) {
            return ResponseEntity.status(400).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        }
    }

    @Operation(summary = "Delete public chat")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "ChatChannel deleted successfully",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Fail. ChatChannel not found with given ID",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Fail. Current user not allowed to delete mentioned ChatChannel",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GeneralResponse.class))},
                    headers = {@Header(name = "Authorization", required = true, description = "Required authorization with Bearer token (JWT)")}
            )
    })
    @DeleteMapping("/public/{id}")
    public ResponseEntity<GeneralResponse<?>> delete(@PathVariable("id") String id) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        try {
            channelService.delete(id, currentUser);
            return ResponseEntity.ok(new GeneralResponse<>("OK", Collections.emptyList()));
        } catch (AccessException ex) {
            return ResponseEntity.status(403).body(new GeneralResponse<>(ex.getMessage(), Collections.emptyList()));
        } catch (NotFoundException ex) {
            return ResponseEntity.status(404).body((new GeneralResponse<>(ex.getMessage(), Collections.emptyList())));
        }
    }

}
