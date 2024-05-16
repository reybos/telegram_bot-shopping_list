package rey.bos.telegram.bot.shopping.list.bot.handler.impl.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.bot.handler.BotHandler;
import rey.bos.telegram.bot.shopping.list.bot.util.BotUtil;
import rey.bos.telegram.bot.shopping.list.bot.util.JoinRequestHelper;
import rey.bos.telegram.bot.shopping.list.io.entity.ShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.params.JoinRequestParams;
import rey.bos.telegram.bot.shopping.list.io.repository.params.UserShoppingListGroupParams;
import rey.bos.telegram.bot.shopping.list.service.JoinRequestService;
import rey.bos.telegram.bot.shopping.list.service.ShoppingListService;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.service.impl.UserShoppingListServiceImpl;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.MessageEntityType.MENTION;
import static rey.bos.telegram.bot.shopping.list.bot.handler.impl.command.MenuCommand.MENU_COMMAND_JOIN_USER;

@Component
@RequiredArgsConstructor
@Slf4j
public class JoinRequestCommandHandler extends BotHandler {

    private final BotUtil botUtil;
    private final JoinRequestService joinRequestService;
    private  final JoinRequestHelper joinRequestHelper;
    private final UserService userService;
    private final UserShoppingListServiceImpl userShoppingListService;
    private final ShoppingListService shoppingListService;
    private final TelegramClient telegramClient;

    @Override
    public boolean handle(Update update, UserDto user) {
        List<MessageEntity> mention = update.getMessage().getEntities().stream()
            .filter(entity -> entity.getType().equals(MENTION.getDescription()))
            .toList();
        if (isEmptyMention(mention, user) || hasTooManyMentions(mention, user) || hasActiveJoinRequests(user)) {
            return true;
        }

        String mentionUserName = mention.get(0).getText();
        Optional<UserDto> mentionUserO = userService.findUserByUserName(mentionUserName);
        if (isMentionUserNotExist(mentionUserO, mentionUserName, user)) {
            return true;
        }

        UserDto mentionUser = mentionUserO.get();
        SendMessage mentionUserMessage;
        try {
            ShoppingList mentionUserShoppingList = shoppingListService.findActiveList(mentionUser.getId());
            ShoppingList currentShoppingList = shoppingListService.findActiveList(user.getId());
            if (hasActiveGroup(currentShoppingList, user, mentionUserName)) {
                return true;
            }
            mentionUserMessage = buildMentionUserMessage(user, mentionUser, mentionUserShoppingList);
        } catch (IllegalStateException e) {
            log.error(e.getMessage(), e);
            return false;
        }
        SendMessage currentUserMessage = joinRequestHelper.buildSendJoinRequestSuccess(user, mentionUserName);
        try {
            Message sentMessage = telegramClient.execute(mentionUserMessage);
            joinRequestService.createJoinRequest(user.getId(), mentionUser.getId(), sentMessage.getMessageId());
            telegramClient.execute(currentUserMessage);
        } catch (TelegramApiRequestException e) {
            if (e.getErrorCode() == HttpStatus.FORBIDDEN.value()) {
                String message = botUtil.getText(user.getLanguageCode(), DictionaryKey.CANT_SEND_MESSAGE)
                    .formatted(mentionUserName);
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

    private boolean isEmptyMention(List<MessageEntity> mention, UserDto user) {
        if (mention.isEmpty()) {
            botUtil.sendMessageByKey(
                user.getTelegramId(), user.getLanguageCode(), DictionaryKey.ERROR_EMPTY_MENTION_IN_JOIN
            );
            return true;
        }
        return false;
    }

    private boolean hasTooManyMentions(List<MessageEntity> mention, UserDto user) {
        if (mention.size() != 1) {
            botUtil.sendMessageByKey(
                user.getTelegramId(), user.getLanguageCode(), DictionaryKey.ERROR_TOO_MANY_MENTION_IN_JOIN
            );
            return true;
        }
        return false;
    }

    private boolean hasActiveJoinRequests(UserDto user) {
        List<JoinRequestParams> requestParams = joinRequestService.findActiveJoinRequest(user.getId());
        if (!requestParams.isEmpty()) {
            SendMessage message = joinRequestHelper.buildHasRequestMessage(user, requestParams);
            botUtil.executeMethod(message);
            return true;
        }
        return false;
    }

    private boolean isMentionUserNotExist(Optional<UserDto> mentionUserO, String mentionUserName, UserDto user) {
        if (mentionUserO.isEmpty()) {
            String message = botUtil.getText(user.getLanguageCode(), DictionaryKey.USER_NOT_EXIST)
                .formatted(mentionUserName);
            botUtil.sendMessage(user.getTelegramId(), message);
            return true;
        }
        return false;
    }

    private boolean hasActiveGroup(ShoppingList shoppingList, UserDto user, String mentionUser) {
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
        UserDto currUser, UserDto mentionUser, ShoppingList mentionUserShoppingList
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
        return supportMenuCommand(update, MENU_COMMAND_JOIN_USER);
    }

}