package rey.bos.telegram.bot.shopping.list.bot.dictionary.impl;

import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.Dictionary;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

import java.util.HashMap;
import java.util.Map;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;

@Component
public class DictionaryEn implements Dictionary {

    private final LanguageCode languageCode;
    private final Map<DictionaryKey, String> dictionary;

    public DictionaryEn() {
        languageCode = LanguageCode.EN;
        dictionary = new HashMap<>();
        addValues();
    }

    @Override
    public boolean isSuitable(LanguageCode languageCode) {
        return languageCode == this.languageCode;
    }

    @Override
    public String get(DictionaryKey key) {
        return dictionary.get(key);
    }

    private void addValues() {
        dictionary.put(SOMETHING_WENT_WRONG, """
            Something went wrong 😿 If problems persist, write to the creator of the bot @reybos, he will try to help.
            """
        );
        dictionary.put(ERROR_ITEM_ADD_TOO_LONG_ITEM, """
            Message is too long, should be no more than 30 characters.
            """
        );
        dictionary.put(ERROR_ITEM_ADD_TOO_LONG_LIST, """
            There are too many items in the current list. Delete unnecessary ones or clear the list completely with the /clear_list command
            """
        );
        dictionary.put(ACTION_ITEM_ADDED_TO_LIST, """
            The item was successfully added to the list
            """
        );
        dictionary.put(EMPTY_LIST_MESSAGE, """
            The list is empty, send a message to the bot to add an item to the list.
            """
        );
        dictionary.put(NOT_EMPTY_LIST_MESSAGE, """
            <b>Your list 📋</b>
                    
            Click on any item to delete it.
            """
        );
        dictionary.put(UNSUPPORTED_COMMAND, """
            The command is not supported, the list of available commands can be viewed in the menu or by calling the /help command
            """
        );
        dictionary.put(ERROR_EMPTY_MENTION_IN_JOIN, """
            To merge shopping lists with another user, call the command "/join @login", where @login is the name of the telegram user you want to merge with. When merging, you will use a shared list with another user, your current list will become inactive.
            """
        );
        dictionary.put(ERROR_TOO_MANY_MENTION_IN_JOIN, """
            You have specified too many users in the request, you can only join one
            """
        );
        dictionary.put(ERROR_HAS_JOIN_REQUEST, """
            You have already made requests to merge with users: %s, wait for confirmation from them, or cancel the current requests to make a new one.
                                                                                   
            Cancel current applications?
            """
        );
        dictionary.put(CONFIRM_MSG, """
            Yes ✅"""
        );
        dictionary.put(REJECT_MSG, """
            No ❌"""
        );
        dictionary.put(USER_NOT_EXIST, """
            %s not found among the bot users. To join a group with a user, ask them to start using this bot @shoppy_guru_bot
            """
        );
        dictionary.put(ERROR_SENDER_IS_OWNER_ACTIVE_GROUP, """
            You are currently in the same group as %s, which you own. To join a group with %s, you need to dissolve the current one, after that %s will use its own shopping list.
            
            Disband the current group?
            """
        );
        dictionary.put(ERROR_SENDER_IS_MEMBER_OF_GROUP, """
            You are currently in the same group as user %s. To join a group with %s, you need to exit the current one, after that the current shopping list will be unavailable.
            
            Quit the group?
            """
        );
        dictionary.put(OWNER_ACCEPT_JOIN_REQUEST_WITHOUT_ACTIVE_GROUP, """
            The user %s wants to share a shopping list with you. If you accept the request, you will become the owner of the list and will be able to add other users to the group.
            
            Do you accept the request?
            """
        );
        dictionary.put(OWNER_ACCEPT_JOIN_REQUEST_WITH_OWN_ACTIVE_GROUP, """
            User %s wants to share a shopping list with you. But you already keep a list with %s. If you accept the request, you will use the list together.
            
            Do you accept the request?
            """
        );
        dictionary.put(OWNER_ACCEPT_JOIN_REQUEST_WITH_ACTIVE_GROUP, """
            User %s wants to share a shopping list with you. If you accept the request, you will become the owner of the list and will be able to add other users to the group. You will also leave the current group with %s and stop using the current shopping list.
            
            Do you accept the request?
            """
        );
        dictionary.put(SEND_JOIN_REQUEST_SUCCESS, """
            A request to merge the lists has been sent to the %s user, the request will be valid for 1 day. If it is not accepted during this time, you will need to resend the request.
            """
        );
        dictionary.put(CANT_SEND_MESSAGE, """
            %s blocked the bot. Ask the user to start using it @shoppy_guru_bot and repeat the request
            """
        );
        dictionary.put(CANT_FIND_ACTIVE_JOIN_REQUEST, """
            The request to merge the lists was not found. It may have already expired or been canceled earlier. Repeat the merge request "/join @login" if necessary.
            """
        );
        dictionary.put(OWNER_MSG_JOIN_REQUEST_REJECTED, """
            The request to merge the lists with the user %s has been rejected.
            """
        );
        dictionary.put(SENDER_MSG_JOIN_REQUEST_REJECTED, """
            Your request to merge lists with user %s has been rejected.
            """
        );
        dictionary.put(ERROR_MENTION_THEMSELF, """
            You cannot specify yourself in the request to merge lists.
            """
        );
        dictionary.put(JOIN_REQUEST_ACCEPTED_OWNER, """
            You have accepted the request to merge shopping lists with the user %s. Now you are the owner of the group and will be able to accept other users into it, in which case you will use the list all together.
            """
        );
        dictionary.put(JOIN_REQUEST_ACCEPTED_SENDER, """
            The request to merge the lists with %s has been approved, now you are using one list. If you send a new request to another user or accept a request to merge lists, the current list will become inactive for you and you will start using the merged list with another user.
            """
        );
        dictionary.put(GREETING_FOR_START, """
            Greetings %s! 👋
            I'll help you keep a shopping list, and you can also join other users in a group and keep one common list.
            
            🔸 Any message sent to the bot will be added to the list. Excluding commands and user mentions.
            🔸 Call the /list command to view the current list.
            🔸 Call the /join command to maintain a shared list with another user, or simply send the username of this user to the bot, for example: @login.
            🔸 Call the /change_language command to change the language of the bot.
            """
        );
        dictionary.put(CHANGE_LANGUAGE_COMMAND, """
            <b>Select the language of the bot</b>
            """
        );
        dictionary.put(ENGLISH_LANGUAGE, """
            EN 🇺🇸"""
        );
        dictionary.put(RUSSIAN_LANGUAGE, """
            RU 🇷🇺"""
        );
        dictionary.put(CHANGE_LANGUAGE_SUCCESS, """
            The language has been successfully changed ✅
            """
        );
        dictionary.put(CLEAR_LIST_COMMAND, """
            All items in the current list will be deleted.
            
            Clear it?
            """
        );
        dictionary.put(CLEAR_LIST_REJECTED, """
            The list cleanup command has been canceled.
            """
        );
        dictionary.put(CLEAR_LIST_ACCEPTED, """
            The current list has been cleared.
            """
        );
    }
}
