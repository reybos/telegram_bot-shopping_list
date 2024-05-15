package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findByTelegramId(long telegramId);

    Optional<User> findByUserName(String userName);

}
