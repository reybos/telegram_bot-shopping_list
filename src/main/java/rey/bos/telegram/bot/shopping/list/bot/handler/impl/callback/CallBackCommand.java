package rey.bos.telegram.bot.shopping.list.bot.handler.impl.callback;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CallBackCommand {

    DELETE_ITEM("delete_item-"),
    CLEAR_EXIST_JOIN_REQUEST("clear_exist_join_request-"),
    DISBAND_GROUP_AND_JOIN("disband_group_and_join-"),
    LEAVE_GROUP_AND_JOIN("leave_group_and_join-"),
    ACCEPT_JOIN_REQUEST("accept_join_request-"),
    CHANGE_LANGUAGE("change_language-"),
    CLEAR_LIST("clear_list-"),
    REFRESH_LIST("refresh_list-"),
    LEAVE_GROUP_CONFIRM("leave_group_confirm-"),
    LEAVE_GROUP("leave_group-"),
    REMOVE_USER_FROM_GROUP_CONFIRM("remove_user_from_group_confirm-"),
    REMOVE_USER_FROM_GROUP("remove_user_from_group-"),

    CONFIRM("-yes"),
    REJECT("-no");

    private final String command;

}
