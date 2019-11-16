package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.standart.Button;
import baliviya.com.github.cznBot.entity.standart.Keyboard;
import baliviya.com.github.cznBot.entity.standart.Language;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KeyboardMarkUpDao extends AbstractDao<Keyboard> {

    private ButtonDao buttonDao;

    public KeyboardMarkUpDao() {
        buttonDao = factory.getButtonDao();
    }


    public ReplyKeyboard select(long keyboardMarkUpId) {
        if (keyboardMarkUpId < 0) {
            ReplyKeyboardRemove keyboard = new ReplyKeyboardRemove();
            return keyboard;
        }
        if (keyboardMarkUpId == 0) {
            return null;
        }
        sql = "SELECT * FROM STANDARD.KEYBOARD WHERE ID=?";
        return getKeyboard(getJdbcTemplate().queryForObject(sql, new Object[]{keyboardMarkUpId}, this::mapper));
    }

    private ReplyKeyboard getKeyboard(Keyboard keyboard) {
        String buttonIds = keyboard.getButton_ids();
        if (buttonIds == null) {
            return null;
        }
        String[] rows = buttonIds.split(";");
        if (keyboard.isInline()) {
            return getInlineKeyboard(rows);
        } else {
            return getReplyKeyboard(rows);
        }
    }

    private ReplyKeyboard getKeyboard(Keyboard keyboard, Language language) {
        String buttonIds = keyboard.getButton_ids();
        if (buttonIds == null) {
            return null;
        }
        String[] rows = buttonIds.split(";");
        if (keyboard.isInline()) {
            return getInlineKeyboard(rows, language);
        } else {
            return getReplyKeyboard(rows, language);
        }
    }

    private ReplyKeyboard getReplyKeyboard(String[] rows) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        boolean isRequestContact = false;
        for (String buttonIdsString : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId));
                KeyboardButton button = new KeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                button.setRequestContact(buttonFromDb.isRequestContact());
                if (buttonFromDb.isRequestContact()) {
                    isRequestContact = true;
                }
                keyboardRow.add(button);
            }
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setOneTimeKeyboard(isRequestContact);
        return replyKeyboardMarkup;
    }

    private ReplyKeyboard getReplyKeyboard(String[] rows, Language language) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        boolean isRequestContact = false;
        for (String buttonIdsString : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), language);
                KeyboardButton button = new KeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                button.setRequestContact(buttonFromDb.isRequestContact());
                if (buttonFromDb.isRequestContact()) {
                    isRequestContact = true;
                }
                keyboardRow.add(button);
            }
            keyboardRowList.add(keyboardRow);
        }
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        replyKeyboardMarkup.setOneTimeKeyboard(isRequestContact);
        return replyKeyboardMarkup;
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[] rowIds) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId));
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    buttonText = buttonText.length() < 64 ? buttonText : buttonText.substring(0, 64);
                    button.setCallbackData(buttonText);
                }
                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    private InlineKeyboardMarkup getInlineKeyboard(String[] rowIds, Language language) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), language);
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    buttonText = buttonText.length() < 64 ? buttonText : buttonText.substring(0, 64);
                    button.setCallbackData(buttonText);
                }
                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public ReplyKeyboard selectForEdition(long keyboardMarkUpId, Language language) {
        if (keyboardMarkUpId < 0) {
            ReplyKeyboardRemove keyboard = new ReplyKeyboardRemove();
            return keyboard;
        }
        if (keyboardMarkUpId == 0) {
            return null;
        }
        sql = "SELECT * FROM STANDARD.KEYBOARD WHERE ID = ?";
        return getKeyboardForEdition(getJdbcTemplate().queryForObject(sql, new Object[]{keyboardMarkUpId}, this::mapper), language);
    }

    private ReplyKeyboard getKeyboardForEdition(Keyboard keyboard, Language language) {
        String buttonIds = keyboard.getButton_ids();
        if (buttonIds == null) {
            return null;
        }
        String[] rows = buttonIds.split(";");
        return getInlineKeyboardForEdition(rows, language);
    }

    private InlineKeyboardMarkup getInlineKeyboardForEdition(String[] rowIds, Language language) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        for (String buttonIdsString : rowIds) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            String[] buttonIds = buttonIdsString.split(",");
            for (String buttonId : buttonIds) {
                Button buttonFromDb = buttonDao.getButton(Integer.parseInt(buttonId), language);
                InlineKeyboardButton button = new InlineKeyboardButton();
                String buttonText = buttonFromDb.getName();
                button.setText(buttonText);
                String url = buttonFromDb.getUrl();
                if (url != null) {
                    button.setUrl(url);
                } else {
                    button.setCallbackData(buttonId);
                }
                row.add(button);
            }
            rows.add(row);
        }
        keyboard.setKeyboard(rows);
        return keyboard;
    }

    public boolean isInline(long keyboardMarkUpId) {
        sql = "SELECT INLINE FROM STANDARD.KEYBOARD WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, new Object[]{keyboardMarkUpId}, Boolean.class);
    }

    public List<Button> getList(int keyId) {
        List<Button> list = new ArrayList<>();
        for (String x : Arrays.asList(getButtonsString(keyId).split(";,"))) {
            list.add(buttonDao.getButton(Integer.parseInt(x)));
        }
        return list;
    }

    public List<Button> getListForEdit(int keyId) {
        List<Button> list = new ArrayList<>();
        for (String x : Arrays.asList(getButtonsString(keyId).split(";"))) {
            list.add(buttonDao.getButton(Integer.parseInt(x)));
        }
        return list;
    }

    public String getButtonsString(int id) {
        sql = "SELECT BUTTON_IDS FROM STANDARD.KEYBOARD WHERE ID = ?";
        return getJdbcTemplate().queryForObject(sql, new Object[]{id}, String.class);
    }

    @Override
    protected Keyboard mapper(ResultSet resultSet, int index) throws SQLException {
        Keyboard keyboard = new Keyboard();
        keyboard.setId(resultSet.getInt(1));
        keyboard.setButton_ids(resultSet.getString(2));
        keyboard.setInline(resultSet.getBoolean(3));
        keyboard.setComment(resultSet.getString(4));
        return keyboard;
    }
}
