package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

public interface UserService {

    UserDto getOrCreateUser(UserDto userDto);

    UserDto createUser(UserDto userDto);

}
