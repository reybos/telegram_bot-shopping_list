package rey.bos.telegram.bot.shopping.list.shared.dto;

import lombok.Builder;
import lombok.Data;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;

@Data
@Builder
public class UserDto {

    private Long id;

    private long telegramId;

    private String userName;

    private String firstName;

    private LanguageCode languageCode;

    private boolean blocked;

}
