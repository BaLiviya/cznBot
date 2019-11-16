package baliviya.com.github.cznBot;

import baliviya.com.github.cznBot.config.Bot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static DefaultAbsSender bot;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        logger.info("ApiContextInitializer.InitNormal()");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        Bot bot = new Bot();
        Main.bot = bot;
        try {
            telegramBotsApi.registerBot(bot);
            logger.info("Bot was registered: " + bot.getBotUsername());
        } catch (TelegramApiRequestException e) {
            logger.error("Error in main", e);
        }
    }

    public static DefaultAbsSender getBot() {
        return bot;
    }
}
