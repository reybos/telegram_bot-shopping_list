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
            return userDtoMapper.map(userO.get());
        }
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

}
