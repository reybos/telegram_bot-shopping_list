package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;

public interface ShoppingListService {

    ShoppingList findActiveList(long userId);

    void addItem(ShoppingList shoppingList, String item);

    void clearActiveList(long userId);

}
