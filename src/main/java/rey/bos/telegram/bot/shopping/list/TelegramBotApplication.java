package rey.bos.telegram.bot.shopping.list;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import rey.bos.telegram.bot.shopping.list.bot.TelegramBot;
import rey.bos.telegram.bot.shopping.list.config.TelegramBotConfig;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class TelegramBotApplication implements CommandLineRunner {

    private final TelegramBotConfig telegramBotConfig;
    private final TelegramBot telegramBot;

    public static void main(String[] args) {
        SpringApplication.run(TelegramBotApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (final var botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(telegramBotConfig.getToken(), telegramBot);
            log.info("Bot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            log.error("Can't start TelegramBot", e);
            throw e;
        }
    }

}
