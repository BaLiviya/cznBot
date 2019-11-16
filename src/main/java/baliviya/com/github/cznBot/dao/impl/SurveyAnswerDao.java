package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.dao.enums.TableNames;
import baliviya.com.github.cznBot.entity.custom.SurveyAnswer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SurveyAnswerDao extends AbstractDao<SurveyAnswer> {

    public void insert(SurveyAnswer surveyAnswer) {
//        int id = getNextId(TableNames.SURVEY_ANSWERS);
        sql = "INSERT INTO STANDARD.SURVEY_ANSWERS (survey_id, chat_id, BUTTON) VALUES (?,?,?)";
        getJdbcTemplate().update(sql, setParam( surveyAnswer.getSurveyId(), surveyAnswer.getChatId(), surveyAnswer.getButton()));
    }

    public void deleteByQuestId(int questId) {
        sql = "DELETE FROM standard.survey_answers WHERE survey_id = ?";
        getJdbcTemplate().update(sql, questId);
    }

    public List<SurveyAnswer> getAll(int surveyId) {
        sql = "SELECT * FROM standard.survey_answers WHERE SURVEY_ID = ? ORDER BY id";
        return getJdbcTemplate().query(sql, setParam(surveyId), this::mapper);
    }

    @Override
    protected SurveyAnswer mapper(ResultSet rs, int index) throws SQLException {
        SurveyAnswer surveyAnswer = new SurveyAnswer();
        surveyAnswer.setId(rs.getInt(1));
        surveyAnswer.setSurveyId(rs.getInt(2));
        surveyAnswer.setChatId(rs.getLong(3));
        surveyAnswer.setButton(rs.getString(4));
        return surveyAnswer;
    }
}
