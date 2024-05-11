package rey.bos.telegram.bot.shopping.list.bot.dictionary.impl;

import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.Dictionary;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

import java.util.HashMap;
import java.util.Map;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.UNHANDLED_COMMAND;

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
        dictionary.put(UNHANDLED_COMMAND, """
        Thank you for your interest in the bot!
        However, it is currently under development and can do almost nothing 😿 When this changes, we will send you a notification.
        """
        );
    }

}
