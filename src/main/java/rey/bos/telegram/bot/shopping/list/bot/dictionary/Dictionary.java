package rey.bos.telegram.bot.shopping.list.bot.dictionary;

import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

public interface Dictionary {

    boolean isSuitable(LanguageCode languageCode);

    String get(DictionaryKey key);

}
