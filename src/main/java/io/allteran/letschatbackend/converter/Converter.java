package io.allteran.letschatbackend.converter;

public interface Converter<DTO, ENTITY> {
    ENTITY convertToEntity(DTO dto);

    DTO convertToDTO(ENTITY entity);
}
