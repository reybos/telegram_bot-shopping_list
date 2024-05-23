package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum MenuCommand {

    MENU_COMMAND_SHOW_LIST("/list", """
        посмотреть список 📋"""),
    MENU_COMMAND_JOIN_USER("/join", """
        объединить списки 🤝"""),
    MENU_COMMAND_START("/start", ""),
    MENU_COMMAND_CHANGE_LANGUAGE("/language", """
        изменить язык ⚙️"""),
    MENU_COMMAND_CLEAR_LIST("/clear", """
        очистить список 🗑"""),
    MENU_COMMAND_GROUP("/group", """
        посмотреть группу 👥"""),
    MENU_COMMAND_INCOMING_REQUEST_SETTING("/request", """
        входящие запросы ⚙️️"""),;

    private final String command;
    private final String description;

    public static List<MenuCommand> getCommandsForMenu() {
        return List.of(
            MENU_COMMAND_SHOW_LIST,
            MENU_COMMAND_CLEAR_LIST,
            MENU_COMMAND_JOIN_USER,
            MENU_COMMAND_GROUP,
            MENU_COMMAND_CHANGE_LANGUAGE,
            MENU_COMMAND_INCOMING_REQUEST_SETTING
        );
    }

}
