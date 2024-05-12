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
        dictionary.put(ERROR_OR_UNHANDLED_COMMAND, """
        Something went wrong 😿 If problems persist, write to the creator of the bot @reybos, he will try to help.
        """
        );
        dictionary.put(TOO_LONG_ITEM, """
        Message is too long, should be no more than 30 characters.
        """
        );
        dictionary.put(TOO_LONG_LIST, """
        There are too many items in the current list. Delete unnecessary ones or clear the list completely with the /clear_list command
        """
        );
        dictionary.put(ITEM_ADDED_TO_LIST, """
        The item was successfully added to the list
        """
        );
    }

}
