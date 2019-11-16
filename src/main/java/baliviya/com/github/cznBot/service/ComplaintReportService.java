package baliviya.com.github.cznBot.service;

import baliviya.com.github.cznBot.dao.DaoFactory;
import baliviya.com.github.cznBot.dao.impl.MessageDao;
import baliviya.com.github.cznBot.entity.custom.Complaint;
import baliviya.com.github.cznBot.entity.custom.Suggestion;
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

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ComplaintReportService {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionReportService.class);
    private DaoFactory daoFactory = DaoFactory.getFactory();
    private MessageDao messageDao = DaoFactory.getFactory().getMessageDao();
    private Language currantLanguage = Language.ru;
    private XSSFWorkbook workbook = new XSSFWorkbook();
    private Sheet sheets;
    private Sheet sheet;
    private XSSFCellStyle style = workbook.createCellStyle();

    private String getText(int messageId) {
        return messageDao.getMessageText(messageId, currantLanguage);
    }

    public void sendComplaintReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) {
        currantLanguage = LanguageService.getLanguage(chatId);
        try {
            sendCompReport(chatId, bot, dateBegin, dateEnd, messagePrevReport);
        } catch (Exception e) {
            logger.error("Can't create/send report", e);
            try {
                bot.execute(new SendMessage(chatId, getText(Const.ERROR_MESSAGE)));
            } catch (TelegramApiException ex) {
                logger.error("Can't send message", ex);
            }
        }
    }

    private void sendCompReport(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) throws TelegramApiException, IOException {
        sheets = workbook.createSheet(getText(Const.COMPLAINT));
        sheet = workbook.getSheetAt(0);
        List<Complaint> reports = daoFactory.getComplaintDao().getComplaintsByTime(dateBegin, dateEnd);
        if (reports == null || reports.size() == 0) {
            bot.execute(new DeleteMessage(chatId, messagePrevReport));
            bot.execute(new SendMessage(chatId, getText(Const.REPORT_EMPTY_MESSAGE)));
            return;
        }
        //  -------------------------Стиль ячеек------------------------- //
        BorderStyle thin = BorderStyle.THIN;
        short black = IndexedColors.BLACK.getIndex();
        XSSFCellStyle styleTitle = setStyle(workbook, thin, black, style);
        //  ------------------------------------------------------------- //
        int rowIndex = 0;
        createTitle(styleTitle, rowIndex, Arrays.asList(messageDao.getMessageText(Const.TABLE_HEAD_NAME, LanguageService.getLanguage(chatId)).split(Const.SPLIT)));
        List<List<String>> info = reports.stream().map(x -> {
            List<String> list = new ArrayList<>();
            list.add(String.valueOf(x.getId()));
            list.add(x.getFullName());
            list.add(x.getPhoneNumber());
            list.add(x.getLocation());
            list.add(x.getText());
            list.add(DateUtil.getDayDate(x.getPostDate()));
            list.add(DateUtil.getTimeDate(x.getPostDate()));
            return list;
        }).collect(Collectors.toList());
        addInfo(info, rowIndex);
        sendFile(chatId, bot, dateBegin, dateEnd, messagePrevReport);
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

    private void addInfo(List<List<String>> reports, int rowIndex) {
        int CellIndex;
        for (List<String> rE : reports) {
            sheets.createRow(++rowIndex);
            insertToRow(rowIndex, rE, style);
        }
        CellIndex = 0;
        sheets.autoSizeColumn(CellIndex++);
        sheets.setColumnWidth(CellIndex++, 4000);
        sheets.setColumnWidth(CellIndex++, 13200);
        sheets.autoSizeColumn(CellIndex++);
        sheets.autoSizeColumn(CellIndex++);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, Date dateBegin, Date dateEnd, int messagePrevReport) throws IOException, TelegramApiException {
        String fileName = "Предложения за: " + DateUtil.getDayDate(dateBegin) + " - " + DateUtil.getDayDate(dateEnd) + ".xlsx";
        String path = "C:\\CznAlmaty\\" + fileName;
        path += new Date().getTime();
        try (FileOutputStream tables = new FileOutputStream(path)) {
            workbook.write(tables);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sendFile(chatId, bot, fileName, path);
    }

    private void sendFile(long chatId, DefaultAbsSender bot, String fileName, String path) throws IOException, TelegramApiException {
        File file = new File(path);
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            bot.execute(new SendDocument().setChatId(chatId).setDocument(fileName, fileInputStream));
        }
        file.delete();
    }

    private XSSFCellStyle setStyle(XSSFWorkbook wb, BorderStyle thin, short black, XSSFCellStyle style) {
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setFillBackgroundColor(new HSSFColor.BLUE().getIndex());
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

        style.setFillForegroundColor(new XSSFColor(new Color(0, 52, 94)));
        return styleTitle;
    }
}
