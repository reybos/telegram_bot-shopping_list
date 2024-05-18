package rey.bos.telegram.bot.shopping.list.bot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;

@Component
@RequiredArgsConstructor
public class AcceptJoinRequestHelper {

    private final BotUtil botUtil;

    public EditMessageText buildCantFindActiveJoinRequest(UserDto user, int messageId) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), CANT_FIND_ACTIVE_JOIN_REQUEST))
            .build();
    }

    public EditMessageText buildOwnerMsgJoinRequestRejected(UserDto user, String senderLogin, int messageId) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), OWNER_MSG_JOIN_REQUEST_REJECTED).formatted(senderLogin))
            .build();
    }

    public SendMessage buildSenderMsgJoinRequestRejected(UserDto user, String ownerLogin) {
        return SendMessage.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .text(botUtil.getText(user.getLanguageCode(), SENDER_MSG_JOIN_REQUEST_REJECTED).formatted(ownerLogin))
            .build();
    }

    public EditMessageText buildJoinRequestAcceptedOwner(UserDto user, String senderLogin, int messageId) {
        return EditMessageText.builder()
            .parseMode("HTML")
            .chatId(user.getTelegramId())
            .messageId(messageId)
            .text(botUtil.getText(user.getLanguageCode(), JOIN_REQUEST_ACCEPTED_OWNER).formatted(senderLogin))
            .build();
    }

}
