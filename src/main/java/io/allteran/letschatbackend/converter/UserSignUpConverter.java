package io.allteran.letschatbackend.converter;

import io.allteran.letschatbackend.domain.User;
import io.allteran.letschatbackend.dto.UserSignUpDto;
import org.springframework.stereotype.Component;

@Component
public class UserSignUpConverter implements Converter<UserSignUpDto, User> {
    @Override
    public User convertToEntity(UserSignUpDto userSignUpDto) {
        return User.builder()
                .id(userSignUpDto.getId())
                .email(userSignUpDto.getEmail())
                .name(userSignUpDto.getName())
                .creationDate(userSignUpDto.getCreationDate())
                .roles(userSignUpDto.getRoles())
                .build();
    }

    @Override
    public UserSignUpDto convertToDTO(User user) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
