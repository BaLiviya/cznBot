package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.custom.Survey;
import baliviya.com.github.cznBot.entity.standart.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SurveyDao extends AbstractDao<Survey> {

    public void insert(Survey survey) {
        sql = "INSERT INTO standard.surveys (id, survey_name, lang_id, is_hide, quest_text) VALUES (?,?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(survey.getId(), survey.getSurveyName(), survey.getLanguageId(), survey.isHide(), survey.getQuestText()));
    }

    public List<Survey> getAllActive(Language language, long chatId) {
        sql = "SELECT * FROM standard.surveys WHERE is_hide = false AND lang_id = ? and id NOT IN (SELECT survey_id FROM standard.SURVEY_ANSWERS WHERE chat_id = ?) ORDER BY ID";
        return getJdbcTemplate().query(sql, setParam(language.getId(), chatId), this::mapper);
    }

    public List<Survey> getAll(Language language) {
        sql = "SELECT * FROM standard.surveys WHERE lang_id = ? ORDER BY id";
        return getJdbcTemplate().query(sql, setParam(language.getId()), this::mapper);
    }

    public Survey getById(int id, Language language) {
        sql = "SELECT * FROM standard.surveys WHERE id = ? AND lang_id = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public void update(Survey survey) {
        sql = "UPDATE standard.surveys SET survey_name = ? WHERE id = ? AND lang_id = ?";
        getJdbcTemplate().update(sql, survey.getSurveyName(), survey.getId(), survey.getLanguageId());
    }

    public void updateQuestText(Survey survey) {
        sql = "UPDATE standard.surveys SET quest_text = ? WHERE id = ? AND lang_id = ?";
        getJdbcTemplate().update(sql, survey.getQuestText(), survey.getId(), survey.getLanguageId());
    }

    public void delete(int id) {
        sql = "DELETE FROM standard.surveys WHERE id = ?";
        getJdbcTemplate().update(sql, id);
    }

    @Override
    protected Survey mapper(ResultSet rs, int index) throws SQLException {
        Survey survey = new Survey();
        survey.setId(rs.getInt(1));
        survey.setSurveyName(rs.getString(2));
        survey.setLanguageId(rs.getInt(3));
        survey.setHide(rs.getBoolean(4));
        survey.setQuestText(rs.getString(5));
        return survey;
    }
}
