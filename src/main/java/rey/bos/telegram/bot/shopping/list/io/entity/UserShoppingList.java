package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users_list")
@Data
@Builder
public class UserShoppingList {

    @Id
    private Long id;

    private long userId;

    @Column("list_id")
    private long shoppingListId;

    private boolean owner;

    private boolean active;

}
