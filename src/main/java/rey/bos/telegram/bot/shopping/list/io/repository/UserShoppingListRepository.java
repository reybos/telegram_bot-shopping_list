package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;

import java.util.List;

@Repository
public interface UserShoppingListRepository extends CrudRepository<UserShoppingList, Long> {

    List<UserShoppingList> findByUserId(long userId);

    @Query(
        """
        SELECT ul.user_id AS user_id,
            ul.owner AS owner,
            u.user_name AS user_name
        FROM users_list ul
            LEFT JOIN users u on u.id = ul.user_id
        WHERE ul.list_id = :listId
            AND ul.active
        """
    )
    List<UserShoppingListGroupParams> findActiveGroupByListId(@Param("listId") long listId);

}
