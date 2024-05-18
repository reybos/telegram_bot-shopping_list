package rey.bos.telegram.bot.shopping.list.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.service.UserShoppingListService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

@Component
@Profile("stub")
@RequiredArgsConstructor
public class UserShoppingListFactory {

    private final JoinRequestFactory joinRequestFactory;
    private final UserShoppingListService userShoppingListService;
    private final TransactionTemplate transactionTemplate;

    public void joinUsersList(UserDto sender, UserDto owner) {
        JoinRequest joinRequest = joinRequestFactory.create(
            JoinRequestFactory.JoinRequestParams.builder()
                .userId(sender.getId())
                .ownerId(owner.getId())
                .build()
        );
        UserShoppingList ownerList = userShoppingListService.findActiveUserShoppingList(owner.getId());
        transactionTemplate.execute(status -> {
            userShoppingListService.changeSenderActiveList(joinRequest, ownerList);
            return null;
        });
    }

}
