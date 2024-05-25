package rey.bos.telegram.bot.shopping.list.bot.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import rey.bos.telegram.bot.shopping.list.io.entity.User;

import static rey.bos.telegram.bot.shopping.list.io.LanguageCode.EN;
import static rey.bos.telegram.bot.shopping.list.io.LanguageCode.RU;

@Component
@RequiredArgsConstructor
public class IncomingRequestCommandHelper {

    public String getConditionMessage(User user) {
        if (user.getLanguageCode() == RU) {
            return user.isJoinRequestDisabled() ? "выключена" : "включена";
        } else if (user.getLanguageCode() == EN) {
            return user.isJoinRequestDisabled() ? "disabled" : "enabled";
        }
        return "";
    }

    public String getProposalMessage(User user) {
        if (user.getLanguageCode() == RU) {
            return user.isJoinRequestDisabled() ? "Включить" : "Выключить";
        } else if (user.getLanguageCode() == EN) {
            return user.isJoinRequestDisabled() ? "on" : "off";
        }
        return "";
    }

    public String getSwitchResultMessage(User user) {
        if (user.getLanguageCode() == RU) {
            return user.isJoinRequestDisabled() ? "выключены" : "включены";
        } else if (user.getLanguageCode() == EN) {
            return user.isJoinRequestDisabled() ? "disabled" : "enabled";
        }
        return "";
    }

}
