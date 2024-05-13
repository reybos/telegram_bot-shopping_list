package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;

import java.util.Optional;

@Repository
public interface MessageShoppingListRepository extends CrudRepository<MessageShoppingList, Long> {

    Optional<MessageShoppingList> findByUserIdAndShoppingListIdAndMessageId(long userId, long listId, int messageId);

}
