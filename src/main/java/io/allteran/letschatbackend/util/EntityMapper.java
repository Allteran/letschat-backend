package io.allteran.letschatbackend.util;

import io.allteran.letschatbackend.domain.ChatCategory;
import io.allteran.letschatbackend.domain.ChatChannel;
import io.allteran.letschatbackend.domain.ChatLanguage;
import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.ChatCategoryDto;
import io.allteran.letschatbackend.dto.ChatChannelDto;
import io.allteran.letschatbackend.dto.ChatLanguageDto;
import io.allteran.letschatbackend.dto.UserDto;
import org.springframework.beans.BeanUtils;

public class EntityMapper {

    public static User convertToEntity(UserDto dto, String imageBaseUrl) {
        User e = new User();
        BeanUtils.copyProperties(dto, e, "userImage");
        if (dto.getUserImage() != null && imageBaseUrl != null) {
            String imageId = dto.getUserImage().replace(imageBaseUrl, "");
            e.setUserImage(imageId);
        }
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

    public static ChatChannelDto convertToDto(ChatChannel e) {
        ChatChannelDto dto = new ChatChannelDto();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }

    public static ChatChannel convertToEntity(ChatChannelDto dto) {
        ChatChannel e = new ChatChannel();
        BeanUtils.copyProperties(dto, e);
        return e;
    }

}
