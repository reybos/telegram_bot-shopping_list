package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.MessageShoppingList;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageShoppingListRepository extends CrudRepository<MessageShoppingList, Long> {

    Optional<MessageShoppingList> findByUserIdAndShoppingListIdAndMessageId(long userId, long listId, int messageId);

    @Modifying
    @Query(
        """
        DELETE
        FROM message_list
        WHERE user_id = :userId
          AND list_id = :listId
        """
    )
    void deleteOldMessage(@Param("userId") long userId, @Param("listId") long listId);

    @Query(
        """
        SELECT u.telegram_id AS telegram_id,
            message_list.message_id AS message_id,
            u.language_code AS language_code
        FROM message_list
            LEFT JOIN users u on u.id = message_list.user_id
        WHERE list_id = :listId
        """
    )
    List<MessageParams> findAllByShoppingListId(@Param("listId") long listId);

}
