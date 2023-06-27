package io.allteran.letschatbackend.util;

import io.allteran.letschatbackend.domain.User;
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
}
