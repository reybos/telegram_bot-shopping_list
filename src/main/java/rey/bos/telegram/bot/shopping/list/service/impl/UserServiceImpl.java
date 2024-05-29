package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.ShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.UserRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.UserShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.shared.mapper.UserDtoMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ShoppingListRepository shoppingListRepository;
    private final UserShoppingListRepository userShoppingListRepository;
    private final UserDtoMapper userDtoMapper;
    private final TransactionTemplate transactionTemplate;

    @Override
    public User getOrCreateUser(UserDto userDto) {
        Optional<User> userO = userRepository.findByTelegramId(userDto.getTelegramId());
        if (userO.isPresent()) {
            User user = userO.get();
            if (
                (userDto.getUserName() != null && !user.getUserName().equals(userDto.getUserName()))
                    || !user.getFirstName().equals(userDto.getFirstName())
                    || user.isBlocked()
            ) {
                user.setUserName(userDto.getUserName());
                user.setFirstName(userDto.getFirstName());
                user.setBlocked(false);
                user = userRepository.save(user);
            }
            return user;
        }
        return createUser(userDto);
    }

    @Override
    public User createUser(UserDto userDto) {
        User storedUser = transactionTemplate.execute(status -> {
            User user = userDtoMapper.map(userDto);
            if (user.getUserName() == null) {
                user.setUserName(String.valueOf(user.getTelegramId()));
            }
            user.setBlocked(false);
            user = userRepository.save(user);
            ShoppingList shoppingList = shoppingListRepository.save(new ShoppingList());
            UserShoppingList userShoppingList = UserShoppingList.builder()
                .userId(user.getId())
                .shoppingListId(shoppingList.getId())
                .owner(true)
                .active(true)
                .build();
            userShoppingListRepository.save(userShoppingList);
            return user;
        });
        return storedUser;
    }

    @Override
    public Optional<User> findActiveUserByLogin(String login) {
        String userName = login.replaceFirst("@", "");
        return userRepository.findByUserNameAndBlocked(userName, false);
    }

    @Override
    public Optional<User> findActiveUserById(long userId) {
        return userRepository.findByIdAndBlocked(userId, false);
    }

    @Override
    public User findByIdOrThrow(long userId) {
        return findByUserIdOrThrow(userId);
    }

    @Override
    public User findByTelegramOrThrow(long telegramId) {
        Optional<User> userO = userRepository.findByTelegramId(telegramId);
        if (userO.isEmpty()) {
            throw new NoSuchElementException("The user with the telegramId=" + telegramId + " was not found");
        }
        return userO.get();
    }

    private User findByUserIdOrThrow(long userId) {
        Optional<User> userO = userRepository.findById(userId);
        if (userO.isEmpty()) {
            throw new NoSuchElementException("The user with the id=" + userId + " was not found");
        }
        return userO.get();
    }

    @Override
    public User updateUserLanguage(long userId, LanguageCode code) {
        User user = findByUserIdOrThrow(userId);
        user.setLanguageCode(code);
        return userRepository.save(user);
    }

    @Override
    public List<User> findActiveUsersByIds(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return userRepository.findByIds(ids);
    }

    @Override
    public void blockUser(long userId) {
        User user = findByUserIdOrThrow(userId);
        user.setBlocked(true);
        userRepository.save(user);
    }

    @Override
    public User switchJoinRequestSetting(long userId) {
        User user = findByUserIdOrThrow(userId);
        user.setJoinRequestDisabled(!user.isJoinRequestDisabled());
        return userRepository.save(user);
    }

}
