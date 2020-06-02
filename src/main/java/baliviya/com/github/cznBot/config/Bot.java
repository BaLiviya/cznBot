package baliviya.com.github.cznBot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);
    private UpdateHandler updateHandler = new UpdateHandler();

    @Override
    public void onUpdateReceived(Update update) {
        logger.debug("------ get UPDATE: " + getBotUsername());
        updateHandler.handle(update, this);
        logger.debug("------ UPDATE processed success");
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        if (method instanceof SendMessage) {
            ((SendMessage) method).disableWebPagePreview();
        }
        return super.execute(method);
    }

    @Override
    public String getBotUsername() {
        return "czn_almaty";
    }

    @Override
    public String getBotToken() {
        return "840528216:AAG0nr1Yi22M8A0u_QR3s6hV7uVvu0_1GkA"; // мой
//        return "1018614657:AAFSXmF1acRFpyo6TkOpnsPdwLkwNc71838";
    }
}
