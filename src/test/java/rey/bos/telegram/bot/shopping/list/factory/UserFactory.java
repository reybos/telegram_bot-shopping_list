package rey.bos.telegram.bot.shopping.list.factory;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.Random;

@Component
@Profile("stub")
@RequiredArgsConstructor
public class UserFactory {

    private final UserService userService;

    public UserDto createUser(UserParams userParams) {
        return userService.createUser(
            UserDto.builder()
                .telegramId(userParams.getTelegramId())
                .userName(userParams.getUserName())
                .firstName(userParams.getFirstName())
                .languageCode(userParams.getLanguageCode())
                .build()
        );
    }

    public UserDto createUser() {
        return createUser(UserParams.builder().build());
    }

    @Builder
    @Getter
    public static class UserParams {

        @Builder.Default
        private long telegramId = new Random().nextLong();;

        @Builder.Default
        private String userName = RandomStringUtils.randomAlphanumeric(10);

        @Builder.Default
        private String firstName = RandomStringUtils.randomAlphanumeric(10);

        @Builder.Default
        private LanguageCode languageCode = LanguageCode.EN;

    }

}
