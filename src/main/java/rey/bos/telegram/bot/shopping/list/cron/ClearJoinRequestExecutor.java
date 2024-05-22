package rey.bos.telegram.bot.shopping.list.cron;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;
import rey.bos.telegram.bot.shopping.list.io.entity.JoinRequest;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;

import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.JOIN_REQUEST_EXPIRED_OWNER_MESSAGE;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.JOIN_REQUEST_EXPIRED_SENDER_MESSAGE;

@Component
@AllArgsConstructor
@Slf4j
public class ClearJoinRequestExecutor {

    public final static int HOURS_BEFORE_EXPIRE = 24;

    private final JoinRequestService joinRequestService;
    private final UserService userService;
    private final MessageUtil messageUtil;
    private final BotUtil botUtil;

    @Scheduled(cron = "0 0 * * * *")
    public void execute() {
        log.info("ClearJoinRequestExecutor is running");
        List<JoinRequest> expiredRequests = joinRequestService.expireRequests(HOURS_BEFORE_EXPIRE);
        if (CollectionUtils.isEmpty(expiredRequests)) {
            return;
        }
        for (JoinRequest joinRequest : expiredRequests) {
            UserDto owner = userService.findByIdOrThrow(joinRequest.getOwnerId());
            String ownerLogin = messageUtil.getLogin(owner.getUserName());
            UserDto sender = userService.findByIdOrThrow(joinRequest.getUserId());
            String senderLogin = messageUtil.getLogin(sender.getUserName());

            EditMessageText ownerMessage = messageUtil.buildEditMessageText(
                owner, joinRequest.getMessageId(), JOIN_REQUEST_EXPIRED_OWNER_MESSAGE, senderLogin
            );
            botUtil.executeMethod(ownerMessage);

            SendMessage senderMessage = messageUtil.buildSendMessage(
                sender, JOIN_REQUEST_EXPIRED_SENDER_MESSAGE, ownerLogin
            );
            botUtil.executeMethod(senderMessage);
        }

    }

}
