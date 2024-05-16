package rey.bos.telegram.bot.shopping.list.io.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;

import java.util.List;

@Repository
public interface JoinRequestRepository extends CrudRepository<JoinRequest, Long> {

    @Query(
        """
        SELECT jr.id AS requestId,
            '@' || u.user_name AS owner_user_name
        FROM join_request jr
        LEFT JOIN users u on u.id = jr.owner_id
        WHERE jr.user_id = :userId
            AND NOT jr.approved
            AND NOT jr.expired
            AND NOT jr.rejected
        """
    )
    List<JoinRequestParams> findActiveJoinRequest(@Param("userId") long userId);

}
