package baliviya.com.github.cznBot.service;

import baliviya.com.github.cznBot.dao.DaoFactory;
import baliviya.com.github.cznBot.entity.custom.Quest;
import baliviya.com.github.cznBot.entity.custom.Survey;
import baliviya.com.github.cznBot.entity.custom.SurveyAnswer;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.DateUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReportService {

    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private Language currentLanguage = Language.ru;
    private static DaoFactory daoFactory = DaoFactory.getFactory();
    private XSSFWorkbook wb = new XSSFWorkbook();
    private Sheet sheets;
    private Sheet sheet;
    private XSSFCellStyle style = wb.createCellStyle();

    public void sendSurveyReport(long chatId, DefaultAbsSender bot, int messagePrevReport) {
        try {
            currentLanguage = LanguageService.getLanguage(chatId);
            sendSurvey(chatId, bot, messagePrevReport);
        } catch (Exception e) {
            logger.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId, "Ошибка при создании отчета"));
            } catch (TelegramApiException e1) {
                logger.error("Can't send message", e);
            }
        }
    }

    private void sendSurvey(long chatId, DefaultAbsSender bot, int messagePrevReport) throws TelegramApiException {
        List<Survey> all = daoFactory.getSurveyDao().getAll(currentLanguage);
        if (all == null || all.size() == 0) {
            bot.execute(new DeleteMessage(chatId, messagePrevReport));
            bot.execute(new SendMessage(chatId, "Опросов нет"));
            return;
        }
        all.forEach(survey -> {
            sheets = wb.createSheet(survey.getSurveyName());
            sheet = sheets;
            List<Quest> questMessageList = daoFactory.getQuestDao().getAll(survey.getId(), currentLanguage);
            List<SurveyAnswer> surveyAnswerList = daoFactory.getSurveyAnswerDao().getAll(survey.getId());
            List<String> listOption = new ArrayList<>();
            for (Quest quest : questMessageList) {
                listOption.addAll(Arrays.asList(quest.getQuestAnswer().split(Const.SPLIT_RANGE)));
            }
            // -------------------------Стиль ячеек-------------------------
            BorderStyle thin = BorderStyle.THIN;
            short black = IndexedColors.BLACK.getIndex();
            XSSFCellStyle styleTitle = setStyle(wb, thin, black, style);

            //--------------------------------------------------------------------
            int rowIndex = 0;
//            createTitle(styleTitle, rowIndex, Arrays.asList(getText(460).split(Const.SPLIT)));//"chatId", "Регистрационные данные", "Данные телеграмм", "Телефон"
            createTitle(styleTitle, rowIndex, Arrays.asList("Вопрос", survey.getSurveyName()));//"chatId", "Регистрационные данные", "Данные телеграмм", "Телефон"
            rowIndex++;
            createTitle(styleTitle, ++rowIndex, Arrays.asList("Вариант ответа", "Кол-во ответов"));//"chatId", "Регистрационные данные", "Данные телеграмм", "Телефон"
            for (String s : listOption) {
                createTitle(styleTitle, ++rowIndex, Arrays.asList(s, String.valueOf(surveyAnswerList.stream().filter(x -> x.getButton().equals(s)).collect(Collectors.toList()).size())));
            }
            rowIndex++;
            int cellIndex;
            for (Quest quest : questMessageList) {
                createTitle(styleTitle, ++rowIndex, Arrays.asList("Группа:" + quest.getQuestAnswer(), "Сообщение: " + survey.getQuestText()));
                createTitle(styleTitle, ++rowIndex, Arrays.asList("Данные пользователя", "Ответ на сообщение", "Выбранный ответ"));
                List<String> strings = Arrays.asList(quest.getQuestAnswer().split(Const.SPLIT_RANGE));
                for (SurveyAnswer surveyAnswer : surveyAnswerList) {
                    if (strings.contains(surveyAnswer.getButton())) {
                        sheets.createRow(++rowIndex);
                        insertToRow(rowIndex, Arrays.asList(getString(String.valueOf(daoFactory.getUserDao().getUserByChatId(surveyAnswer.getChatId()).getFullName())), getString(surveyAnswer.getButton())), style);
                    }
                }
            }
            // ускоряем создание делая настройку ширины после заполнения - это долгий процесс поэтому нужно делать один раз
            cellIndex = 0;
            sheets.setColumnWidth(cellIndex++, 7000);
            sheets.setColumnWidth(cellIndex++, 30000);
            sheets.setColumnWidth(cellIndex++, 5000);
        });
        try {
            sendFileQuest(chatId, bot, messagePrevReport);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void createTitle(XSSFCellStyle styleTitle, int rowIndex, List<String> title) {
        sheets.createRow(rowIndex);
        insertToRow(rowIndex, title, styleTitle);
    }

    private void insertToRow(int row, List<String> cellValues, CellStyle cellStyle) {
        int cellIndex = 0;
        for (String cellValue : cellValues) {
            addCellValue(row, cellIndex++, cellValue, cellStyle);
        }
    }

    private void addCellValue(int rowIndex, int cellIndex, String cellValue, CellStyle cellStyle) {
        sheets.getRow(rowIndex).createCell(cellIndex).setCellValue(getString(cellValue));
        sheet.getRow(rowIndex).getCell(cellIndex).setCellStyle(cellStyle);
    }

    private String getString(String nullable) {
        if (nullable == null) {
            return "";
        }
        return nullable;
    }

    private XSSFCellStyle setStyle(XSSFWorkbook wb, BorderStyle thin, short black, XSSFCellStyle style) {
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setFillForegroundColor(new HSSFColor.BLUE().getIndex());
        style.setBorderTop(thin);
        style.setBorderBottom(thin);
        style.setBorderRight(thin);
        style.setBorderLeft(thin);
        style.setTopBorderColor(black);
        style.setRightBorderColor(black);
        style.setBottomBorderColor(black);
        style.setLeftBorderColor(black);

        BorderStyle tittle = BorderStyle.MEDIUM;
        XSSFCellStyle styleTitle = wb.createCellStyle();
        styleTitle.setWrapText(true);
        styleTitle.setAlignment(HorizontalAlignment.CENTER);
        styleTitle.setVerticalAlignment(VerticalAlignment.CENTER);

        styleTitle.setBorderTop(tittle);
        styleTitle.setBorderBottom(tittle);
        styleTitle.setBorderRight(tittle);
        styleTitle.setBorderLeft(tittle);

        styleTitle.setTopBorderColor(black);
        styleTitle.setRightBorderColor(black);
        styleTitle.setBottomBorderColor(black);
        styleTitle.setLeftBorderColor(black);

        style.setFillForegroundColor(new XSSFColor(new java.awt.Color(0, 52, 94)));
        return styleTitle;
    }

    private void sendFileQuest(long chatId, DefaultAbsSender bot, int messagePrevReport) throws IOException, TelegramApiException {
        String filename = "Опросы - " /*getText(466)*//*"Опросы - " */ + DateUtil.getDayDate(new Date()) + ".xlsx";
        String path = "C:\\" + filename;
        path += new Date().getTime(); // добавляем время создания, чтобы не было 1 файла для двух потоков
        FileOutputStream tables = new FileOutputStream(path);
        wb.write(tables);
        sendFile(chatId, bot, messagePrevReport, filename, path);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, int messagePrevReport, String filename, String path) throws FileNotFoundException, TelegramApiException {
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        //bot.execute(new DeleteMessage(chatId, messagePrevReport)); // оставляем сообщения чтобы было время создания
        bot.execute(new SendDocument()
                .setChatId(chatId)
                .setDocument(filename, fileInputStream));
        file.delete();
    }
}
