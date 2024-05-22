package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByTelegramId(long telegramId);

    Optional<User> findByUserNameAndBlocked(String userName, boolean blocked);

    Optional<User> findByIdAndBlocked(long id, boolean blocked);

    @Query(
        """
        SELECT *
        FROM users
        WHERE id IN (:ids)
            AND NOT blocked
        """
    )
    List<User> findByIds(@Param("ids") List<Long> ids);

}
