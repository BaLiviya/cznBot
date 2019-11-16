package baliviya.com.github.cznBot.config;

import baliviya.com.github.cznBot.util.UpdateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class UpdateHandler {

    private Map<Long, Conversation> conversations = new HashMap<>();
    private DefaultAbsSender bot;
    private static final Logger logger = LoggerFactory.getLogger(UpdateHandler.class);
    private boolean isInit = false;

    public void handle(Update update, DefaultAbsSender bot) {
        try {
            if (!isInit) {
                this.bot = bot;
                isInit = true;
            }
            Conversation conversation = getConversation(update);
            conversation.handleUpdate(update, bot);
        } catch (Exception e) {
            logger.error("Exception: ", e);
        }
    }

    private Conversation getConversation(Update update) throws TelegramApiException {
        Long chatId = UpdateUtil.getChatId(update);
        Conversation conversation = conversations.get(chatId);
        if (conversation == null) {
            logger.info("{} - InitNormal new conversation for '{}'", bot.getMe().getUserName(), chatId);
            conversation = new Conversation();
            conversations.put(chatId, conversation);
        }
        return conversation;
    }
}
