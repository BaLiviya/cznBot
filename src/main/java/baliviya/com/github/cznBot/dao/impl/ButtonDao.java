package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.standart.Button;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.exception.CommandNotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ButtonDao extends AbstractDao<Button> {

    public void update(Button button) {
        sql = "UPDATE standard.button SET name = ?, url=? WHERE id = ? AND lang_id=?";
        getJdbcTemplate().update(sql, button.getName(), button.getUrl(), button.getId(), button.getLanguage().getId());
    }

    public Button getButton(String text) throws CommandNotFoundException {
        try {
            sql = "SELECT * FROM standard.button WHERE name=? AND lang_id=?";
            return getJdbcTemplate().queryForObject(sql, setParam(text, getLanguage().getId()), this::mapper);
        } catch (Exception e) {
            if (e.getMessage().contains("Incorrect result size: expected 1, actual 0")) {
                throw new CommandNotFoundException(e);
            }
            throw e;
        }
    }

    public String getButtonText(int id) {
        sql = "SELECT name FROM standard.button WHERE ID=? AND lang_id=?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), String.class);
    }

    public Button getButton(int id) {
        sql = "SELECT * FROM  standard.button WHERE id =? AND lang_id = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, getLanguage().getId()), this::mapper);
    }

    public Button getButton(int id, Language language) {
        sql = "SELECT * FROM standard.button WHERE id =? AND lang_id = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public int getButtonId(String text, Language language) {
        sql = "SELECT ID FROM STANDARD.BUTTON WHERE NAME = ? AND LANG_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(text, language.getId()), Integer.class);
    }

    public boolean isExist(String text, Language language) {
        sql = "SELECT COUNT(*) FROM standard.button WHERE name=? AND lang_id=?";
        return getJdbcTemplate().queryForObject(sql, setParam(text, language.getId()), Integer.class) > 0;
    }

    protected Button mapper(ResultSet rs, int index) throws SQLException {
        Button button = new Button();
        button.setId(rs.getInt(1));
        button.setName(rs.getString(2));
        button.setCommandId(rs.getInt(3));
        button.setUrl(rs.getString(4));
        button.setRequestContact(rs.getBoolean(5));
        button.setMessageId(rs.getInt(6));
        button.setLanguage(Language.getById(rs.getInt(7)));
        return button;
    }
}
