package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingListItem;

@Repository
public interface ShoppingListItemRepository extends CrudRepository<ShoppingListItem, Long> {
}
