package io.allteran.letschatbackend.converter;

import io.allteran.letschatbackend.domain.Interest;
import io.allteran.letschatbackend.dto.InterestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class InterestConverter implements Converter<InterestDto, Interest> {
    @Override
    public Interest convertToEntity(InterestDto dto) {
        return Interest.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }

    @Override
    public InterestDto convertToDTO(Interest interest) {
        return InterestDto.builder()
                .id(interest.getId())
                .name(interest.getName())
                .build();
    }
}
