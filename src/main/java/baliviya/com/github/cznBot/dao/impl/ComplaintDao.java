package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.custom.Complaint;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class ComplaintDao extends AbstractDao<Complaint> {

    public void insert(Complaint complaint) {
        sql = "INSERT INTO standard.complaints (FULL_NAME, PHONE_NUMBER, LOCATION, TEXT, POST_DATE) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(complaint.getFullName(), complaint.getPhoneNumber(),
                complaint.getLocation(), complaint.getText(), complaint.getPostDate()));
    }

    public int getCount() {
        sql = "SELECT COUNT(id) FROM standard.complaints";
        return getJdbcTemplate().queryForObject(sql, Integer.class);
    }

    public List<Complaint> getComplaintsByTime(Date dateBegin, Date deadline) {
        sql = "SELECT * FROM standard.complaints WHERE post_date BETWEEN to_date (?,'YYYY-MM-DD') AND to_date (?, 'YYYY-MM-DD') ORDER BY id";
        return getJdbcTemplate().query(sql, new Object[]{dateBegin, deadline}, this::mapper);
    }

    @Override
    protected Complaint mapper(ResultSet rs, int index) throws SQLException {
        Complaint complaint = new Complaint();
        complaint.setId(rs.getInt(1));
        complaint.setFullName(rs.getString(2));
        complaint.setPhoneNumber(rs.getString(3));
        complaint.setLocation(rs.getString(4));
        complaint.setText(rs.getString(5));
        complaint.setPostDate(rs.getDate(6));
        return complaint;
    }
}
