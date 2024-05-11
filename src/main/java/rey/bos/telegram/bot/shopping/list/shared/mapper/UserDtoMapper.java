package rey.bos.telegram.bot.shopping.list.shared.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import rey.bos.telegram.bot.shopping.list.io.LanguageCode;
import rey.bos.telegram.bot.shopping.list.io.entity.User;
import rey.bos.telegram.bot.shopping.list.shared.dto.UserDto;

@Mapper(componentModel="spring")
public abstract class UserDtoMapper {

    public abstract User map(UserDto userDto);

    public abstract UserDto map(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "telegramId", source = "id")
    public abstract UserDto map(org.telegram.telegrambots.meta.api.objects.User user);

    LanguageCode map(String code) {
        for (LanguageCode languageCode : LanguageCode.values()) {
            if (languageCode.getValue().equals(code)) {
                return languageCode;
            }
        }
        return LanguageCode.EN;
    }

}
