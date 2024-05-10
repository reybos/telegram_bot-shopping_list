package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;

@Table("list")
@Data
public class ShoppingList {

    @Id
    private Long id;

    private Set<ShoppingListItem> items = new HashSet<>();

}
