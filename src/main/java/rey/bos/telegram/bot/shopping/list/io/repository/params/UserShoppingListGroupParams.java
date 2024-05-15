package rey.bos.telegram.bot.shopping.list.io.repository.params;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShoppingListGroupParams {

    private long userId;
    private String userName;
    private boolean owner;

}
