package io.allteran.letschatbackend.converter;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserConverter implements Converter<UserDto, User> {
    @Override
    public User convertToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .active(userDto.isActive())
                .creationDate(userDto.getCreationDate())
                .roles(userDto.getRoles())
                .userImage(userDto.getUserImage())
//                .interests()
//                .password()
//                .language()
                .build();
    }

    @Override
    public UserDto convertToDTO(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .active(user.isActive())
                .creationDate(user.getCreationDate())
                .roles(user.getRoles())
                .userImage(user.getUserImage())
//                .interests()
//                .password()
//                .language()
                .build();
    }
}
