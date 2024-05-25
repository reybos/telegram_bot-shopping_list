package rey.bos.telegram.bot.shopping.list.bot.handler.impl.action;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.helper.JoinRequestHelper;
import rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.impl.UserShoppingListServiceImpl;
import rey.bos.telegram.bot.shopping.list.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.util.MessageUtil;

import java.util.List;
import java.util.Optional;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.MENTION;
import static rey.bos.telegram.bot.shopping.list.dictionary.DictionaryKey.SEND_JOIN_REQUEST_SUCCESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class MentionUserHandler extends BotHandler {

    private final BotUtil botUtil;
    private final JoinRequestService joinRequestService;
    private final JoinRequestHelper joinRequestHelper;
    private final UserService userService;
    private final UserShoppingListServiceImpl userShoppingListService;
    private final ShoppingListService shoppingListService;
    private final TelegramClient telegramClient;
    private final MessageUtil messageUtil;

    @Override
    public boolean handle(Update update, User user) {
        List<MessageEntity> mention = update.getMessage().getEntities().stream()
            .filter(entity -> entity.getType().equals(MENTION.getDescription()))
            .toList();
        if (hasTooManyMentions(mention, user) || hasActiveJoinRequests(user)) {
            return true;
        }

        String mentionLogin = mention.get(0).getText();
        if (isMentionedHimself(user, mentionLogin)) {
            return true;
        }
        Optional<User> mentionUserO = userService.findActiveUserByLogin(mentionLogin);
        if (isMentionUserNotExist(mentionUserO, mentionLogin, user)
            || isMentionUserBlockRequests(mentionUserO.get(), mentionLogin, user)) {
            return true;
        }

        User mentionUser = mentionUserO.get();
        SendMessage mentionUserMessage;
        try {
            ShoppingList mentionUserShoppingList = shoppingListService.findActiveList(mentionUser.getId());
            ShoppingList currentShoppingList = shoppingListService.findActiveList(user.getId());
            if (hasActiveGroup(currentShoppingList, user, mentionLogin)) {
                return true;
            }
            mentionUserMessage = buildMentionUserMessage(user, mentionUser, mentionUserShoppingList);
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        SendMessage currentUserMessage = messageUtil.buildSendMessage(user, SEND_JOIN_REQUEST_SUCCESS, mentionLogin);
        try {
            Message sentMessage = telegramClient.execute(mentionUserMessage);
            joinRequestService.createJoinRequest(user.getId(), mentionUser.getId(), sentMessage.getMessageId());
            telegramClient.execute(currentUserMessage);
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == HttpStatus.FORBIDDEN.value()) {
                String message = botUtil.getText(user.getLanguageCode(), DictionaryKey.CANT_SEND_MESSAGE)
                    .formatted(mentionLogin);
                botUtil.sendMessage(user.getTelegramId(), message);
                return true;
            }
            log.error("Can't execute command", e);
            return false;
        } catch (TelegramApiException e) {
            log.error("Can't execute command", e);
            return false;
        }
        return true;
    }

    private boolean hasTooManyMentions(List<MessageEntity> mention, User user) {
        if (mention.size() != 1) {
            botUtil.sendMessageByKey(
                user.getTelegramId(), user.getLanguageCode(), DictionaryKey.ERROR_TOO_MANY_MENTION_IN_JOIN
            );
            return true;
        }
        return false;
    }

    private boolean hasActiveJoinRequests(User user) {
        List<JoinRequestParams> requestParams = joinRequestService.findActiveJoinRequest(user.getId());
        if (!requestParams.isEmpty()) {
            SendMessage message = joinRequestHelper.buildHasRequestMessage(user, requestParams);
            botUtil.executeMethod(message);
            return true;
        }
        return false;
    }

    private boolean isMentionedHimself(User user, String mentionLogin) {
        if (mentionLogin.replaceFirst("@", "").equals(user.getUserName())) {
            botUtil.sendMessageByKey(
                user.getTelegramId(), user.getLanguageCode(), DictionaryKey.ERROR_MENTION_THEMSELF
            );
            return true;
        }
        return false;
    }

    private boolean isMentionUserNotExist(Optional<User> mentionUserO, String mentionUserName, User user) {
        if (mentionUserO.isEmpty()) {
            String message = botUtil.getText(user.getLanguageCode(), DictionaryKey.USER_NOT_EXIST)
                .formatted(mentionUserName);
            botUtil.sendMessage(user.getTelegramId(), message);
            return true;
        }
        return false;
    }

    private boolean isMentionUserBlockRequests(User mentionUser, String mentionUserName, User user) {
        if (mentionUser.isJoinRequestDisabled()) {
            String message = botUtil.getText(user.getLanguageCode(), DictionaryKey.OWNER_BLOCK_JOIN_REQUEST_MESSAGE)
                .formatted(mentionUserName);
            botUtil.sendMessage(user.getTelegramId(), message);
            return true;
        }
        return false;
    }

    private boolean hasActiveGroup(ShoppingList shoppingList, User user, String mentionUser) {
        List<UserShoppingListGroupParams> group = userShoppingListService.findActiveGroupByListId(
            shoppingList.getId()
        );
        if (group.size() == 1) {
            return false;
        } else if (group.stream().anyMatch(item -> item.getUserId() == user.getId() && item.isOwner())) {
            SendMessage message = joinRequestHelper.buildHasActiveGroupMessage(group, user, mentionUser);
            botUtil.executeMethod(message);
            return true;
        } else if (group.stream().anyMatch(item -> item.getUserId() == user.getId() && !item.isOwner())) {
            SendMessage message = joinRequestHelper.buildLeaveGroupMessage(group, user, mentionUser);
            botUtil.executeMethod(message);
            return true;
        }
        throw new IllegalStateException("The group does not have an owner " + group);
    }

    private SendMessage buildMentionUserMessage(
        User currUser, User mentionUser, ShoppingList mentionUserShoppingList
    ) {
        List<UserShoppingListGroupParams> group = userShoppingListService.findActiveGroupByListId(
            mentionUserShoppingList.getId()
        );
        if (group.size() == 1) {
            return joinRequestHelper.buildAcceptJoinRequestWithoutActiveGroup(currUser, mentionUser);
        } else if (group.stream().anyMatch(item -> item.getUserId() == mentionUser.getId() && item.isOwner())) {
            return joinRequestHelper.buildAcceptJoinRequestWithOwnActiveGroup(currUser, mentionUser, group);
        } else if (group.stream().anyMatch(item -> item.getUserId() == mentionUser.getId() && !item.isOwner())) {
            return joinRequestHelper.buildAcceptJoinRequestWithActiveGroup(currUser, mentionUser, group);
        }
        throw new IllegalStateException("The group does not have an owner " + group);
    }

    @Override
    public boolean support(Update update) {
        return update.hasMessage() && update.getMessage().hasText()
            && (!CollectionUtils.isEmpty(update.getMessage().getEntities())
            && update.getMessage().getEntities()
                .stream()
                .map(MessageEntity::getType)
                .anyMatch(MENTION.getDescription()::equals));
    }

}
