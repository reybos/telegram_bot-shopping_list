package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum MenuCommand {

    MENU_COMMAND_SHOW_LIST("/show_list", """
        view the current list 📋"""),
    MENU_COMMAND_JOIN_USER("/join", """
        merge lists with the user 🤝"""),
    MENU_COMMAND_START("/start", ""),
    MENU_COMMAND_CHANGE_LANGUAGE("/change_language", """
        change language 🇺🇸""");

    private final String command;
    private final String description;

    public static List<MenuCommand> getCommandsForMenu() {
        return List.of(
            MENU_COMMAND_SHOW_LIST, MENU_COMMAND_JOIN_USER, MENU_COMMAND_CHANGE_LANGUAGE
        );
    }

}
