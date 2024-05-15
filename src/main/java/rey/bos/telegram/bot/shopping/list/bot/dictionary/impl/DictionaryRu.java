package rey.bos.telegram.bot.shopping.list.bot.dictionary.impl;

import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.Dictionary;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

import java.util.HashMap;
import java.util.Map;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.*;

@Component
public class DictionaryRu implements Dictionary {

    private final LanguageCode languageCode;
    private final Map<DictionaryKey, String> dictionary;

    public DictionaryRu() {
        languageCode = LanguageCode.RU;
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
            Что-то пошло не так 😿 Если проблемы сохраняются, напишите создателю бота @reybos, он попробует помочь.
            """
        );
        dictionary.put(ERROR_ITEM_ADD_TOO_LONG_ITEM, """
            Слишком длинное сообщение, должно содержать не более 30 символов.
            """
        );
        dictionary.put(ERROR_ITEM_ADD_TOO_LONG_LIST, """
            В текущем списке слишком много элементов. Удалите ненужные или полностью очистите список с помощью команды /clear_list
            """
        );
        dictionary.put(ACTION_ITEM_ADDED_TO_LIST, """
            Элемент успешно добавлен в список.
            """
        );
        dictionary.put(EMPTY_LIST_MESSAGE, """
            Список пуст, отправьте сообщение боту что бы добавить элемент в список.
            """
        );
        dictionary.put(NOT_EMPTY_LIST_MESSAGE, """
            <b>Ваш список 📋</b>
                    
            Нажмите на любом элементе, чтобы удалить его.
            """
        );
        dictionary.put(UNSUPPORTED_COMMAND, """
            Команда не поддерживается, список доступных команд можно посмотреть в меню или вызвав команду /help 
            """
        );
        dictionary.put(ERROR_EMPTY_MENTION_IN_JOIN, """
            Чтобы объединить списки покупок с другим пользователем, введите команду /join @login, где @login - это имя пользователя telegram, с которым вы хотите объединиться. При объединении вы будете использовать общий список с другим пользователем, ваш текущий список станет неактивным.
            """
        );
        dictionary.put(ERROR_TOO_MANY_MENTION_IN_JOIN, """
            Вы указали слишком много пользователей в запросе, можно присоединиться только к одному
            """
        );
        dictionary.put(ERROR_HAS_JOIN_REQUEST, """
            Вы уже сделали запросы на объединение с пользователями: %s, дождитесь от них подтверждения, либо отмените текущие заявки что бы сделать новую.
                    
            Отменить текущие заявки?
            """
        );
        dictionary.put(CONFIRM_MSG, """
            Да ✅"""
        );
        dictionary.put(REJECT_MSG, """
            Нет ❌"""
        );
        dictionary.put(USER_NOT_EXIST, """
            %s не найден среди пользователей бота. Чтобы объединиться с пользователем в группу, попросите его начать использовать этого бота @reybos_shoping_list_bot 
            """
        );
        dictionary.put(ERROR_OWNER_HAS_ACTIVE_GROUP, """
            Сейчас вы состоите в одной группе с %s, владельцем которой являетесь. Чтобы вступить в группу с %s, нужно распустить текущую, после этого %s будет использовать свой собственный список покупок.
            
            Распустить текущую группу?
            """
        );
        dictionary.put(ERROR_MEMBER_OF_GROUP, """
            Сейчас вы состоите в одной группе с %s. Чтобы вступить в группу с %s, нужно выйти из текущей, после этого текущий список покупок будет не доступен.
            
            Выйти из группы?
            """
        );
    }

}
