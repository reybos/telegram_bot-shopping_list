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
        dictionary.put(ERROR_OR_UNHANDLED_COMMAND, """
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
    }

}
