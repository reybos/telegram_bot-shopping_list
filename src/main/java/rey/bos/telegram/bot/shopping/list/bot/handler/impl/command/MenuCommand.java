package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MenuCommand {

    MENU_COMMAND_SHOW_LIST("/show_list", """
        View the current list 📋""");

    private final String command;
    private final String description;

}
