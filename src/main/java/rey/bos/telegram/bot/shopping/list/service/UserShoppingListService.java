package rey.bos.telegram.bot.shopping.list.service;

import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

public interface UserShoppingListService {

    List<UserShoppingListGroupParams> findActiveGroupByListId(long listId);

    UserShoppingList findActiveUserShoppingList(long userId);

    void changeSenderActiveList(JoinRequest joinRequest, UserShoppingList newList);

    UserShoppingList restoreMainList(UserDto user, UserShoppingList activeList);

}
