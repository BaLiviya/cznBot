package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.service.ReportService;
import baliviya.com.github.cznBot.util.Const;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;

public class id014_ReportSurvey extends Command {

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        sendReport();
        return EXIT;
    }

    private void sendReport() throws TelegramApiException {
        int preview = sendMessage("Список подготавливается...");
        ReportService reportService = new ReportService();
        reportService.sendSurveyReport(chatId, bot, preview);
    }
}
