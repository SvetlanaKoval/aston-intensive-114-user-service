package ru.aston.intensive.mapper;

import org.mapstruct.Mapper;
import ru.aston.intensive.dto.UserDto;
import ru.aston.intensive.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto mapUserToUserDto(User user);

    User mapUserDtoToUser(UserDto userDto);

}
