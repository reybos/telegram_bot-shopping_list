package rey.bos.telegram.bot.shopping.list.bot.dictionary.impl;

import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.Dictionary;
import rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

import java.util.HashMap;
import java.util.Map;

import static rey.bos.telegram.bot.shopping.list.bot.dictionary.DictionaryKey.UNHANDLED_COMMAND;

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
        dictionary.put(UNHANDLED_COMMAND, """
        Спасибо за интерес проявленный к боту! 
        Правда сейчас он находится на стадии разработки и почти ничего не умеет 😿 Когда это изменится, мы пришлем вам уведомление.
        """
        );
    }

}
