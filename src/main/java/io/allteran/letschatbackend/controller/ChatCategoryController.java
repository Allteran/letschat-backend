package io.allteran.letschatbackend.controller;

import io.allteran.letschatbackend.dto.ChatCategoryDto;
import io.allteran.letschatbackend.dto.GeneralResponse;
import io.allteran.letschatbackend.exception.EntityFieldException;
import io.allteran.letschatbackend.service.ChatCategoryService;
import io.allteran.letschatbackend.util.EntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat-category")
@RequiredArgsConstructor
public class ChatCategoryController {
    private final ChatCategoryService categoryService;

    @GetMapping(path = {"", "/"})
    public ResponseEntity<List<ChatCategoryDto>> getAll() {
        return ResponseEntity.ok(categoryService.findAll().stream().map(EntityMapper::convertToDto).toList());
    }

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
}
