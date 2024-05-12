package rey.bos.telegram.bot.shopping.list.bot.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ChatMemberStatus {

    KICKED("kicked"),
    MEMBER("member");

    private final String value;

}
