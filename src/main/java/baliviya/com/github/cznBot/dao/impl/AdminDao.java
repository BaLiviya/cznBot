package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.standart.Admin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AdminDao extends AbstractDao<Admin> {

    public boolean isAdmin(long chatId) {
        sql = "SELECT count(*) FROM standard.admin WHERE user_id=?";
        int count = getJdbcTemplate().queryForObject(sql, new Object[]{chatId}, Integer.class);
        if (count > 0) {
            return true;
        }
        return false;
    }

    public void addAssistant(long chatId, String comment) {
        sql = "INSERT INTO STANDARD.ADMIN VALUES (DEFAULT, ?, ?)";
        getJdbcTemplate().update(sql, chatId, comment);
    }

    public List<Long> getAll() {
        sql = "SELECT USER_ID FROM STANDARD.ADMIN ORDER BY ID";
        return getJdbcTemplate().queryForList(sql, Long.class);
    }

    public void delete(long chatId) {
        sql = "DELETE FROM STANDARD.ADMIN WHERE USER_ID = ?";
        getJdbcTemplate().update(sql, chatId);
    }

    @Override
    protected Admin mapper(ResultSet rs, int index) throws SQLException {
        Admin admin = new Admin();
        admin.setId(rs.getInt(1));
        admin.setUser_id(rs.getLong(2));
        admin.setComment(rs.getString(3));
        return admin;
    }
}
