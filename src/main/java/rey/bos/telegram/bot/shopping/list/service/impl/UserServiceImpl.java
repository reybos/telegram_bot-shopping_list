package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.ShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.UserRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.UserShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;
import rey.bos.telegram.bot.shopping.list.shared.mapper.UserDtoMapper;

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
    public UserDto getOrCreateUser(UserDto userDto) {
        Optional<User> userO = userRepository.findByTelegramId(userDto.getTelegramId());
        if (userO.isPresent()) {
            User user = userO.get();
            if (
                !user.getUserName().equals(userDto.getUserName()) || !user.getFirstName().equals(userDto.getFirstName())
            ) {
                user.setUserName(userDto.getUserName());
                user.setFirstName(userDto.getFirstName());
                user = userRepository.save(user);
            }
            return userDtoMapper.map(user);
        }
        return createUser(userDto);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User storedUser = transactionTemplate.execute(status -> {
            User user = userRepository.save(userDtoMapper.map(userDto));
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
        return userDtoMapper.map(storedUser);
    }

    @Override
    public Optional<UserDto> findUserByLogin(String login) {
        String userName = login.replaceFirst("@", "");
        Optional<User> userO = userRepository.findByUserName(userName);
        return userO.map(userDtoMapper::map);
    }

    @Override
    public UserDto findByIdOrThrow(long userId) {
        User user = findByUserIdOrThrow(userId);
        return userDtoMapper.map(user);
    }

    @Override
    public UserDto findByTelegramOrThrow(long telegramId) {
        Optional<User> userO = userRepository.findByTelegramId(telegramId);
        if (userO.isEmpty()) {
            throw new NoSuchElementException("The user with the telegramId=" + telegramId + " was not found");
        }
        return userDtoMapper.map(userO.get());
    }

    private User findByUserIdOrThrow(long userId) {
        Optional<User> userO = userRepository.findById(userId);
        if (userO.isEmpty()) {
            throw new NoSuchElementException("The user with the id=" + userId + " was not found");
        }
        return userO.get();
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = findByUserIdOrThrow(userDto.getId());
        user.setUserName(user.getUserName());
        user.setFirstName(userDto.getFirstName());
        user.setLanguageCode(userDto.getLanguageCode());
        user = userRepository.save(user);
        return userDtoMapper.map(user);
    }

}
