package rey.bos.telegram.bot.shopping.list.factory;

import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

public class VerifyMessage {

    public static SendMessage getVerifySendMessage(TelegramClient telegramClient) throws TelegramApiException {
        ArgumentCaptor<SendMessage> messageCapture = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getValue();
    }

    public static EditMessageText getVerifyEditMessageText(TelegramClient telegramClient) throws TelegramApiException {
        ArgumentCaptor<EditMessageText> messageCapture = ArgumentCaptor.forClass(EditMessageText.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getValue();
    }

    public static List<SendMessage> getVerifySendMessages(TelegramClient telegramClient) throws TelegramApiException {
        ArgumentCaptor<SendMessage> messageCapture = ArgumentCaptor.forClass(SendMessage.class);
        verify(telegramClient, atLeast(1)).execute(messageCapture.capture());
        return messageCapture.getAllValues();
    }

}
