package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("list_item")
@Data
@Builder
public class ShoppingListItem implements Comparable<ShoppingListItem> {

    @Id
    private Long id;

    private String value;

    @Override
    public int compareTo(@NotNull ShoppingListItem o) {
        return this.getId().compareTo(o.getId());
    }

}
