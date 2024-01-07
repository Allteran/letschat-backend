package io.allteran.letschatbackend.converter;

import io.allteran.letschatbackend.domain.ChatLanguage;
import io.allteran.letschatbackend.dto.ChatLanguageDto;
import org.springframework.stereotype.Component;

@Component
public class LanguageConverter implements Converter<ChatLanguageDto, ChatLanguage> {
    @Override
    public ChatLanguage convertToEntity(ChatLanguageDto dto) {
        return ChatLanguage.builder()
                .id(dto.getId())
                .name(dto.getName())
                .code(dto.getCode())
                .emoji(dto.getEmoji())
                .build();
    }

    @Override
    public ChatLanguageDto convertToDTO(ChatLanguage entity) {
        return ChatLanguageDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .emoji(entity.getEmoji())
                .build();
    }
}
