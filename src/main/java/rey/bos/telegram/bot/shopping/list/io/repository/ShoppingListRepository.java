package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;

import java.util.List;

@Repository
public interface ShoppingListRepository extends CrudRepository<ShoppingList, Long> {

    @Query(
        """
        SELECT l.*
        FROM users_list
        LEFT JOIN list l on l.id = users_list.list_id
        WHERE user_id = :userId AND active
        """)
    List<ShoppingList> findActiveList(long userId);

}
