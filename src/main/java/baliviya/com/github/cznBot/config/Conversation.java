package baliviya.com.github.cznBot.config;

import baliviya.com.github.cznBot.Main;
import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.dao.DaoFactory;
import baliviya.com.github.cznBot.dao.impl.MessageDao;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.entity.standart.Message;
import baliviya.com.github.cznBot.exception.CommandNotFoundException;
import baliviya.com.github.cznBot.service.CommandService;
import baliviya.com.github.cznBot.service.LanguageService;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.DateUtil;
import baliviya.com.github.cznBot.util.SetDeleteMessages;
import baliviya.com.github.cznBot.util.UpdateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;

public class Conversation {

    private CommandService commandService = new CommandService();
    private Command command;
    private Long chatId;
    private static long currentChatId;
    private DaoFactory factory = DaoFactory.getFactory();
    private static final Logger logger = LoggerFactory.getLogger(Conversation.class);
    private MessageDao messageDao;

    public static long getCurrentChatId() {
        return currentChatId;
    }

    public void handleUpdate(Update update, DefaultAbsSender bot) throws SQLException, TelegramApiException {
        printUpdate(update);
        chatId = UpdateUtil.getChatId(update);
        currentChatId = chatId;
        messageDao = factory.getMessageDao();
        checkLang(chatId);
        try {
            command = commandService.getCommand(update);
            if (command != null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
            }
        } catch (CommandNotFoundException e) {
            if (chatId < 0) {
                return;
            }
            if (command == null) {
                SetDeleteMessages.deleteKeyboard(chatId, bot);
                SetDeleteMessages.deleteMessage(chatId, bot);
                Message message = messageDao.getMessage(Const.COMMAND_NOT_FOUND);
                SendMessage sendMessage = new SendMessage(chatId, message.getName());
                bot.execute(sendMessage);
            }
        }
        if (command != null) {
            if (command.isInitNotNormal(update, bot)) {
                clear();
                return;
            }
            boolean commandFinished = command.execute();
            if (commandFinished) {
                clear();
            }
        }
    }

    private void checkLang(long chatId) {
        if (LanguageService.getLanguage(chatId) == null) {
            LanguageService.setLanguage(chatId, Language.ru);
        }
    }

    private void printUpdate(Update update) {
        String dateMessage = "";
        if (update.hasMessage()) {
            dateMessage = DateUtil.getDbMmYyyyHhMmSs(new Date((long) update.getMessage().getDate() * 1000));
        }
        logger.debug("New update get {} -> send response {}", dateMessage, DateUtil.getDbMmYyyyHhMmSs(new Date()));
        logger.debug(UpdateUtil.toString(update));
    }

    public static DefaultAbsSender getBot() {
        return Main.getBot();
    }

    void clear() {
        command.clear();
        command = null;
    }
}
