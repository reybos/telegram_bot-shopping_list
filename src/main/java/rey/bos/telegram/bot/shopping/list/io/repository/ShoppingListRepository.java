package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;

@Repository
public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {
}
