package io.allteran.letschatbackend.util;

import io.allteran.letschatbackend.domain.ChatCategory;
import io.allteran.letschatbackend.domain.ChatLanguage;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.ChatCategoryDto;
import io.allteran.letschatbackend.dto.ChatLanguageDto;
import io.allteran.letschatbackend.dto.UserDto;
import org.springframework.beans.BeanUtils;

public class EntityMapper {
    public static UserDto convertToDto(User e) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }

    public static User convertToEntity(UserDto dto) {
        User e = new User();
        BeanUtils.copyProperties(dto, e);
        return e;
    }

    public static ChatCategory convertToEntity(ChatCategoryDto dto) {
        ChatCategory e = new ChatCategory();
        BeanUtils.copyProperties(dto, e);
        return e;
    }

    public static ChatCategoryDto convertToDto(ChatCategory e) {
        ChatCategoryDto dto = new ChatCategoryDto();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }

    public static ChatLanguage convertToEntity(ChatLanguageDto dto) {
        ChatLanguage e = new ChatLanguage();
        BeanUtils.copyProperties(dto, e);
        return e;
    }

    public static ChatLanguageDto convertToDto(ChatLanguage e) {
        ChatLanguageDto dto = new ChatLanguageDto();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }
}
