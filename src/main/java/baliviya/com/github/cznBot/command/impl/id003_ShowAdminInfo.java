package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.entity.standart.Message;
import baliviya.com.github.cznBot.util.Const;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id003_ShowAdminInfo extends Command {

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        deleteMessage(updateMessageId);
        Message message = messageDao.getMessage(messageId);
        sendMessage(messageId, chatId, null, message.getPhoto());
        if (message.getFile() != null) {
            switch (message.getFileType()) {
                case audio:
                    bot.execute(new SendAudio().setAudio(message.getFile()).setChatId(chatId));
                case video:
                    bot.execute(new SendVideo().setVideo(message.getFile()).setChatId(chatId));
                case document:
                    bot.execute(new SendDocument().setChatId(chatId).setDocument(message.getFile()));
            }
        }
        return EXIT;
    }
}
