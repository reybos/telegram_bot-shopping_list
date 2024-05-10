package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;

import java.util.List;

@Repository
public interface UserShoppingListRepository extends CrudRepository<UserShoppingList, Long> {

    List<UserShoppingList> findByUserId(long userId);

}
