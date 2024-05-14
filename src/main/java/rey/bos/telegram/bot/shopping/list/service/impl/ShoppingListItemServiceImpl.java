package rey.bos.telegram.bot.shopping.list.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.repository.ShoppingListItemRepository;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListItemService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShoppingListItemServiceImpl implements ShoppingListItemService {

    private final ShoppingListItemRepository shoppingListItemRepository;

    @Override
    @Transactional
    public void deleteItemById(long itemId) {
        Optional<ShoppingListItem> shoppingListItemO = shoppingListItemRepository.findById(itemId);
        shoppingListItemO.ifPresent(shoppingListItemRepository::delete);
    }

}
