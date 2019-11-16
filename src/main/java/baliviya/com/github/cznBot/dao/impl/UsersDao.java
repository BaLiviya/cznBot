package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.standart.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersDao extends AbstractDao<User> {

    public boolean isRegistered(long chatId) {
        sql = "SELECT count(*) FROM STANDARD.USERS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), Integer.class) > 0;
    }

    public User getUserByChatId(long chatId) {
        sql = "SELECT * FROM standard.users WHERE chat_id = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(chatId), this::mapper);
    }

    public User getUserById(long id) {
        sql = "SELECT * FROM STANDARD.USERS WHERE CHAT_ID = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id), this::mapper);
    }

    public void insert(User user) {
        if (user.getChatId() == 0) return;
        sql = "INSERT INTO standard.users(chat_id, user_name, phone, full_name, email) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, user.getChatId(), user.getUserName(), user.getPhone(), user.getFullName(), user.getEmail());
    }

    @Override
    protected User mapper(ResultSet resultSet, int index) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt(1));
        user.setChatId(resultSet.getLong(2));
        user.setPhone(resultSet.getString(3));
        user.setFullName(resultSet.getString(4));
        user.setEmail(resultSet.getString(5));
        user.setUserName(resultSet.getString(6));
        return user;
    }
}
