package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CallBackCommand {

    DELETE_ITEM("delete_item-"),
    CLEAR_JOIN_REQUEST("clear_join_request-"),
    DISBAND_CURRENT_GROUP("disband_current_group-"),
    LEAVE_CURRENT_GROUP("leave_current_group-"),
    ACCEPT_JOIN_REQUEST("accept_join_request-"),
    CHANGE_LANGUAGE("change_language-"),

    CONFIRM("-yes"),
    REJECT("-no");

    private final String command;

}
