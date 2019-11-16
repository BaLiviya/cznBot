package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.custom.Suggestion;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class SuggestionDao extends AbstractDao<Suggestion> {

    public void insert(Suggestion suggestion) {
        sql = "INSERT INTO standard.suggestions (FULL_NAME, PHONE_NUMBER, LOCATION, TEXT, POST_DATE) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(suggestion.getFullName(), suggestion.getPhoneNumber(),
                suggestion.getLocation(), suggestion.getText(), suggestion.getPostDate()));
    }

    public int getCount() {
        sql = "SELECT COUNT(id) FROM standard.suggestions";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public List<Suggestion> getSuggestionsByTime(Date dateBegin, Date deadline) {
        sql = "SELECT * FROM standard.suggestions WHERE post_date BETWEEN to_date (?,'YYYY-MM-DD') AND to_date (?, 'YYYY-MM-DD') ORDER BY id";
        return getJdbcTemplate().query(sql, new Object[]{dateBegin, deadline}, this::mapper);
    }

    @Override
    protected Suggestion mapper(ResultSet rs, int index) throws SQLException {
        Suggestion suggestion = new Suggestion();
        suggestion.setId(rs.getInt(1));
        suggestion.setFullName(rs.getString(2));
        suggestion.setPhoneNumber(rs.getString(3));
        suggestion.setLocation(rs.getString(4));
        suggestion.setText(rs.getString(5));
        suggestion.setPostDate(rs.getDate(6));
        return suggestion;
    }
}
