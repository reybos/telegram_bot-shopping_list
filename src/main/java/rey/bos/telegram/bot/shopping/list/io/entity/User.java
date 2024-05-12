package rey.bos.telegram.bot.shopping.list.io.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

@Table("users")
@Data
public class User {

    @Id
    private Long id;

    private long telegramId;

    private String userName;

    private String firstName;

    private LanguageCode languageCode;

}