package rey.bos.telegram.bot.shopping.list.factory;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;
import rey.bos.telegram.bot.shopping.list.io.repository.ShoppingListRepository;

@Component
@Profile("stub")
@RequiredArgsConstructor
public class ShoppingListItemFactory {

    private final ShoppingListRepository shoppingListRepository;

    public ShoppingListItem addItem(ShoppingList shoppingList) {
        return addItem(ShoppingListItemParams.builder().build(), shoppingList);
    }

    public ShoppingListItem addItem(ShoppingListItemParams itemParams, ShoppingList shoppingList) {
        ShoppingListItem item = ShoppingListItem.builder()
            .value(itemParams.getValue())
            .build();
        shoppingList.getItems().add(item);
        shoppingListRepository.save(shoppingList);
        return item;
    }

    @Builder
    @Getter
    public static class ShoppingListItemParams {

        @Builder.Default
        private String value = RandomStringUtils.randomAlphanumeric(10);

    }

}
