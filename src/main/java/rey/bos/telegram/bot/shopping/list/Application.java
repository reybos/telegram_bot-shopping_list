package rey.bos.telegram.bot.shopping.list;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import rey.bos.telegram.bot.shopping.list.bot.ShoppingListBot;
import rey.bos.telegram.bot.shopping.list.config.ShoppingListBotConfig;

@SpringBootApplication
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class Application implements CommandLineRunner {

    private final ShoppingListBotConfig shoppingListBotConfig;
    private final ShoppingListBot shoppingListBot;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        try (final var botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(shoppingListBotConfig.getToken(), shoppingListBot);
            log.info("Bot successfully started!");
            Thread.currentThread().join();
        } catch (Exception e) {
            log.error("Can't start bot", e);
            throw e;
        }
    }

}
