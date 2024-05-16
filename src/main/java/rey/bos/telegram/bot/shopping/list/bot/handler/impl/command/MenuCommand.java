package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MenuCommand {

    MENU_COMMAND_SHOW_LIST("/show_list", """
        View the current list 📋"""),
    MENU_COMMAND_JOIN_USER("/join", """
        merge lists with the user 🤝""");

    private final String command;
    private final String description;

}
