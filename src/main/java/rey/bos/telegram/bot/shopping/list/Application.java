package rey.bos.telegram.bot.shopping.list;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "rey.bos")
@Slf4j
public class Application{

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
