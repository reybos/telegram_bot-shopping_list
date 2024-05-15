package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("join_request")
@Data
@Builder
public class JoinRequest {

    @Id
    private Long id;

    private long userId;

    private long ownerId;

    private boolean approved;

    private boolean expired;

    private boolean rejected;

    private int messageId;

}
