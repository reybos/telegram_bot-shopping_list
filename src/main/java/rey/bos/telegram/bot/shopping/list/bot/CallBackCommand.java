package rey.bos.telegram.bot.shopping.list.bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CallBackCommand {

    DELETE_ITEM("delete_item-");

    private final String command;
}
