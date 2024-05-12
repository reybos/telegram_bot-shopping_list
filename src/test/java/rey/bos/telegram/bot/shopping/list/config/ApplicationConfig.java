package rey.bos.telegram.bot.shopping.list.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.longpolling.starter.TelegramBotInitializer;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static org.mockito.Mockito.mock;

@TestConfiguration
@Profile("stub")
public class ApplicationConfig {

    @Bean
    public TelegramClient telegramClient() {
        return mock(TelegramClient.class);
    }

    @Bean
    public TelegramBotInitializer telegramBotInitializer() {
        return mock(TelegramBotInitializer.class);
    }

}
