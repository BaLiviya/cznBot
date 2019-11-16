package baliviya.com.github.cznBot.util;

import baliviya.com.github.cznBot.dao.DaoFactory;
import baliviya.com.github.cznBot.dao.impl.KeyboardMarkUpDao;
import baliviya.com.github.cznBot.entity.standart.Message;
import baliviya.com.github.cznBot.util.type.ParseMode;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendContact;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.util.Objects;

public class BotUtil {

    @Getter
    private DefaultAbsSender bot;
    private static DaoFactory daoFactory = DaoFactory.getFactory();
    private static final Logger logger = LoggerFactory.getLogger(BotUtil.class);

    public BotUtil(DefaultAbsSender bot) {
        this.bot = bot;
    }

    public int sendMessage(SendMessage sendMessage) throws TelegramApiException {
        try {
            return bot.execute(sendMessage).getMessageId();
        } catch (TelegramApiRequestException e) {
            if (e.getApiResponse().contains("Bad Request: can't parse entities")) {
                sendMessage.setParseMode(null);
                sendMessage.setText(sendMessage.getText() + "\nBad tags");
                return bot.execute(sendMessage).getMessageId();
            } else throw e;
        }
    }

    public int sendMessage(long messageId, long chatId) throws TelegramApiException {
        return sendMessage(messageId, chatId, null, null);
    }

    public int sendMessage(String text, long chatId) throws TelegramApiException {
        return sendMessage(text, chatId, ParseMode.html);
    }

    public int sendMessage(String text, long chatId, ParseMode parseMode) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        if (parseMode == ParseMode.WITHOUT) {
            sendMessage.setParseMode(null);
        } else {
            sendMessage.setParseMode(parseMode.name());
        }
        return sendMessage(sendMessage);
    }

    public int sendMessage(long messageId, long chatId, Contact contact, String photo) throws TelegramApiException {
        int result = 0;
        Message message = daoFactory.getMessageDao().getMessage(messageId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message.getName());
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode(ParseMode.html.name());
        KeyboardMarkUpDao keyboardMarkUpDao = daoFactory.getKeyboardMarkUpDao();
        if (keyboardMarkUpDao.select(message.getKeyboardMarkUpId()) != null) {
            sendMessage.setReplyMarkup(keyboardMarkUpDao.select(message.getKeyboardMarkUpId()));
        }
        boolean isCaption = false;
        if (photo != null) {
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(photo);
            if (message.getName().length() < 200) {
                sendPhoto.setCaption(message.getName());
                isCaption = true;
            }
            try {
                result = bot.execute(sendPhoto).getMessageId();
            } catch (Exception e) {
                logger.debug("Can't send photo", e);
                isCaption = false;
            }
        }
        if (!isCaption) {
            result = bot.execute(sendMessage).getMessageId();
        }
        if (contact != null) {
            sendContact(chatId, contact);
        }
        return result;
    }

    public int sendContact(long chatId, Contact contact) throws TelegramApiException {
        return bot.execute(new SendContact()
                .setChatId(chatId)
                .setFirstName(contact.getFirstName())
                .setLastName(contact.getLastName())
                .setPhoneNumber(contact.getPhoneNumber())
        ).getMessageId();
    }

    public void deleteMessage(long chatId, int messageId) {
        try {
            bot.execute(new DeleteMessage(chatId, messageId));
        } catch (TelegramApiException e) {
        }
    }

    public int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId) throws TelegramApiException {
        return sendMessageWithKeyboard(text, keyboard, chatId, 0);
    }

    private int sendMessageWithKeyboard(String text, ReplyKeyboard keyboard, long chatId, int replyMessageId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage()
                .setParseMode(ParseMode.html.name())
                .setChatId(chatId)
                .setText(text)
                .setReplyMarkup(keyboard);
        if (replyMessageId != 0) {
            sendMessage.setReplyToMessageId(replyMessageId);
        }
        return sendMessage(sendMessage);
    }

    public boolean hasContactOwner(Update update) {
        return (update.hasMessage() && update.getMessage().hasContact()) &&  Objects.equals(update.getMessage().getFrom().getId(), update.getMessage().getContact().getUserID());
    }
}
