package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;

import java.util.List;

public interface UserShoppingListService {

    List<UserShoppingListGroupParams> findActiveGroupByListId(long listId);

}
