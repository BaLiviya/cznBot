package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.service.SuggestionReportService;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.DateKeyboard;
import baliviya.com.github.cznBot.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;

public class id008_ReportSuggestion extends Command {

    private DateKeyboard dateKeyboard;
    private Date start;
    private Date end;

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (waitingType) {
            case START:
                dateKeyboard = new DateKeyboard();
                sendStartDate();
                waitingType = WaitingType.START_DATE;
                return COMEBACK;
            case START_DATE:
                if (hasCallbackQuery()) {
                    deleteMessage();
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    start = dateKeyboard.getDateDate(updateMessageText);
                    sendEndDate();
                    waitingType = WaitingType.END_DATE;
                    return COMEBACK;
                }
                return COMEBACK;
            case END_DATE:
                if (hasCallbackQuery()) {
                    deleteMessage();
                    if (dateKeyboard.isNext(updateMessageText)) {
                        sendStartDate();
                        return COMEBACK;
                    }
                    end = dateKeyboard.getDateDate(updateMessageText);
                    sendReport();
                    waitingType = WaitingType.END_DATE;
                    return COMEBACK;
                }
                return COMEBACK;
        }
        sendLightReport();
        return EXIT;
    }

    private void sendReport() throws TelegramApiException {
        int preview = sendMessage(Const.REPORT_PREPARED);
        SuggestionReportService reportService = new SuggestionReportService();
        reportService.sendSuggestionReport(chatId, bot, start, end, preview);
    }

    private int sendStartDate() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(sendLightReport() + getText(Const.SELECT_START_DATE), dateKeyboard.getCalendarKeyboard()));
    }

    private int sendEndDate() throws TelegramApiException {
        return toDeleteKeyboard(sendMessageWithKeyboard(Const.SELECT_END_DATE, dateKeyboard.getCalendarKeyboard()));
    }

    private String sendLightReport() {
        String result = getText(Const.NUMBER_OF_SUGGESTION);
        result = String.format(result, suggestionDao.getCount());
        return result;
    }
}
