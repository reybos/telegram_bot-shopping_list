package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.repository.ShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShoppingListServiceImpl implements ShoppingListService {

    public final ShoppingListRepository shoppingListRepository;

    public ShoppingList findActiveList(UserDto userDto) {
        List<ShoppingList> lists = shoppingListRepository.findActiveList(userDto.getId());
        if (CollectionUtils.isEmpty(lists) || lists.size() != 1) {
            throw new IllegalStateException(
                "The number of active lists for a user with id = " + userDto.getId() + " is not equal to 1"
            );
        }
        return lists.get(0);
    }

    @Override
    public void addItem(ShoppingList shoppingList, String item) {
        shoppingList.getItems().add(ShoppingListItem.builder().value(item).build());
        shoppingListRepository.save(shoppingList);
    }

}
