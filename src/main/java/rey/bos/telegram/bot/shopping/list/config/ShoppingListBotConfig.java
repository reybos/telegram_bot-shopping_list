package rey.bos.telegram.bot.shopping.list.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
@Profile("prod")
public class ShoppingListBotConfig {

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

}
