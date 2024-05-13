package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("message_list")
@Data
@Builder
public class MessageShoppingList {

    @Id
    private Long id;

    private long userId;

    @Column("list_id")
    private long shoppingListId;

    private int messageId;

}
