package fr.epitech.project.manager.dto.mapper;

import fr.epitech.project.manager.dto.UserDto;
import fr.epitech.project.manager.entity.UserEntity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Named(value = "convertUserEntityToDto")
    UserDto convert(UserEntity userEntity);

    @IterableMapping(qualifiedByName = "convertUserEntityToDto", nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<UserDto> convertListUserEntity(List<UserEntity> userEntityList);

    @Mapping(target = "id", ignore = true)
    void updateUserEntity(UserDto userDto, @MappingTarget UserEntity userEntity);
}