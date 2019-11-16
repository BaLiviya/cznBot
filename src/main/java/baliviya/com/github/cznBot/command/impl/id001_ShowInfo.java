package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.entity.standart.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id001_ShowInfo extends Command {
    @Override
    public boolean execute() throws TelegramApiException {
        if (!usersDao.isRegistered(chatId)) {
            User user = new User();
            user.setChatId(chatId);
            user.setFullName(update.getMessage().getFrom().getFirstName() + " " + update.getMessage().getFrom().getLastName());
            user.setUserName(update.getMessage().getFrom().getUserName());
            usersDao.insert(user);
        }
        sendMessageWithAddition();
        return EXIT;
    }
}
