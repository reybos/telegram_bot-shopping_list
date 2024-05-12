package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("list_item")
@Data
@Builder
public class ShoppingListItem {

    @Id
    private Long id;

    private String value;

}
