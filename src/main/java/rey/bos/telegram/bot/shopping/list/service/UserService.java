package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto getOrCreateUser(UserDto userDto);

    UserDto createUser(UserDto userDto);

    Optional<UserDto> findUserByLogin(String login);

    UserDto findByIdOrThrow(long userId);

    UserDto findByTelegramOrThrow(long telegramId);

    UserDto updateUser(UserDto user);

    List<UserDto> findUsersByIds(List<Long> ids);

}
