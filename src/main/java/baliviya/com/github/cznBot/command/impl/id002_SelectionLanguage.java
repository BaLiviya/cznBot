package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.entity.standart.User;
import baliviya.com.github.cznBot.service.LanguageService;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class id002_SelectionLanguage extends Command {

    public boolean execute() throws TelegramApiException {
        deleteMessage(updateMessageId);
        chosenLanguage();
        sendMessage(Const.WELCOME_TEXT_WHEN_START);
        return EXIT;
    }

    private void chosenLanguage() {
        if (isButton(Const.RU_LANGUAGE)) {
            LanguageService.setLanguage(chatId, Language.ru);
        }
        if (isButton(Const.KZ_LANGUAGE)) {
            LanguageService.setLanguage(chatId, Language.kz);
        }
    }
}
