package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id006_MapLocationSend extends Command {
    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        SendLocation sendLocation = new SendLocation();
        sendLocation.setLatitude((float)43.2610684);
        sendLocation.setLongitude((float)76.9454389);
        bot.execute(sendLocation.setChatId(chatId)).getMessageId();
        return EXIT;
    }
}
