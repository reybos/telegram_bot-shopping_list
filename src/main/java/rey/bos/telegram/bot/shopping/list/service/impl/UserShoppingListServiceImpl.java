package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import rey.bos.telegram.bot.shopping.list.io.repository.UserShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;

import java.util.List;

@Service
@AllArgsConstructor
public class UserShoppingListServiceImpl implements UserShoppingListService {

    private final UserShoppingListRepository userShoppingListRepository;

    @Override
    public List<UserShoppingListGroupParams> findActiveGroupByListId(long listId) {
        return userShoppingListRepository.findActiveGroupByListId(listId);
    }

}
