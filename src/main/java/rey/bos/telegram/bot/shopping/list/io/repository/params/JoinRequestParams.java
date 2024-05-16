package rey.bos.telegram.bot.shopping.list.io.repository.params;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JoinRequestParams {

    private long requestId;
    private String ownerUserName;

}
