package rey.bos.telegram.bot.shopping.list.bot.handler.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageEntityType {

    BOT_COMMAND("bot_command"),
    MENTION("mention");

    private final String description;

}
