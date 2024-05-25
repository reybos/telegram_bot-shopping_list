package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User getOrCreateUser(UserDto userDto);

    User createUser(UserDto userDto);

    Optional<User> findActiveUserByLogin(String login);

    Optional<User> findActiveUserById(long userId);

    User findByIdOrThrow(long userId);

    User findByTelegramOrThrow(long telegramId);

    User updateUserLanguage(long userId, LanguageCode code);

    List<User> findActiveUsersByIds(List<Long> ids);

    void blockUser(long userId);

    User switchJoinRequestSetting(long userId);

}
