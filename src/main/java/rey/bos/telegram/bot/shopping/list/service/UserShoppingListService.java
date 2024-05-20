package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;

import java.util.List;

public interface UserShoppingListService {

    List<UserShoppingListGroupParams> findActiveGroupByListId(long listId);

    List<UserShoppingList> findActiveGroupByUserId(long userId);

    UserShoppingList findActiveUserShoppingList(long userId);

    void changeSenderActiveList(JoinRequest joinRequest, UserShoppingList newList);

    UserShoppingList restoreMainList(long userId, UserShoppingList activeList);

    void restoreMainList(long userId);

    UserShoppingListGroupParams getUserListParamsById(long userListId);

}
