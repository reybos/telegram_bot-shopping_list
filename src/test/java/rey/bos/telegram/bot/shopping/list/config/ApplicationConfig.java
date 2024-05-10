package rey.bos.telegram.bot.shopping.list.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;
import rey.bos.telegram.bot.shopping.list.bot.ShoppingListBot;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class ApplicationConfig {

    @Bean
    public ShoppingListBot shoppingListBot() {
        return mock(ShoppingListBot.class);
    }

    @Bean
    public TelegramBotInitializer telegramBotInitializer() {
        return mock(TelegramBotInitializer.class);
    }

}
