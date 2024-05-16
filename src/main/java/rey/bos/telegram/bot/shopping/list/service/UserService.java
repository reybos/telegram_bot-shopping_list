package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Optional;

public interface UserService {

    UserDto getOrCreateUser(UserDto userDto);

    UserDto createUser(UserDto userDto);

    Optional<UserDto> findUserByUserName(String userName);

}
