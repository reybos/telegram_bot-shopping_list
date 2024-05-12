package rey.bos.telegram.bot.shopping.list.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import rey.bos.telegram.bot.shopping.list.Application;
import rey.bos.telegram.bot.shopping.list.BaeldungPostgresqlContainer;
import rey.bos.telegram.bot.shopping.list.config.ApplicationConfig;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.UserShoppingList;
import rey.bos.telegram.bot.shopping.list.io.repository.UserShoppingListRepository;
import rey.bos.telegram.bot.shopping.list.service.UserService;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {Application.class, ApplicationConfig.class})
@ActiveProfiles({"tc", "tc-auto", "stub"})
class UserServiceImplTest {

    @ClassRule
    public static PostgreSQLContainer<BaeldungPostgresqlContainer> postgreSQLContainer
        = BaeldungPostgresqlContainer.getInstance();

    @Autowired
    private UserService userService;

    @Autowired
    private UserShoppingListRepository userShoppingListRepository;

    @Test
    void whenGetNewUserThenCreate() {
        UserDto input = buildUserWithRandomTelegramId();
        UserDto userDto = userService.getOrCreateUser(input);
        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertThat(userDto).usingRecursiveComparison().ignoringFields("id").isEqualTo(input);

        List<UserShoppingList> userShoppingLists = userShoppingListRepository.findByUserId(userDto.getId());
        assertThat(userShoppingLists.size()).isNotZero();
    }

    @Test
    void whenGetOldUserThenNotCreate() {
        UserDto input = buildUserWithRandomTelegramId();
        UserDto userDto1 = userService.getOrCreateUser(input);
        UserDto userDto2 = userService.getOrCreateUser(input);
        assertThat(userDto1.getId()).isEqualTo(userDto2.getId());
    }

    private UserDto buildUserWithRandomTelegramId() {
        return UserDto.builder()
            .userName(RandomStringUtils.randomAlphanumeric(10))
            .firstName(RandomStringUtils.randomAlphanumeric(10))
            .telegramId(new Random().nextLong())
            .languageCode(LanguageCode.EN)
            .build();
    }

}