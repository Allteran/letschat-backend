package io.allteran.letschatbackend.util;

import io.allteran.letschatbackend.domain.*;
import io.allteran.letschatbackend.dto.*;
import org.springframework.beans.BeanUtils;

public class EntityMapper {
    public static UserDto convertToDto(User e, String imageBaseUrl) {
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(e, dto, "userImage");
        if(e.getUserImage() != null && imageBaseUrl != null) {
            dto.setUserImage(imageBaseUrl + e.getUserImage());
        }
        return dto;
    }

    public static User convertToEntity(UserDto dto, String imageBaseUrl) {
        User e = new User();
        BeanUtils.copyProperties(dto, e, "userImage");
        if(dto.getUserImage() != null && imageBaseUrl != null) {
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

    public static InterestDto convertToDto(Interest e) {
        InterestDto dto = new InterestDto();
        BeanUtils.copyProperties(e, dto);
        return dto;
    }

}
