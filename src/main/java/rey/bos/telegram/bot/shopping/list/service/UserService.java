package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    UserDto getOrCreateUser(UserDto userDto);

    UserDto createUser(UserDto userDto);

    Optional<UserDto> findActiveUserByLogin(String login);

    Optional<UserDto> findActiveUserById(long userId);

    UserDto findByIdOrThrow(long userId);

    UserDto findByTelegramOrThrow(long telegramId);

    UserDto updateUser(UserDto user);

    List<UserDto> findActiveUsersByIds(List<Long> ids);

    void blockUser(UserDto user);

}
