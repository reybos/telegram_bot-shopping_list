package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

public interface ShoppingListService {

    ShoppingList findActiveList(UserDto userDto);

    void addItem(ShoppingList shoppingList, String item);

}
