package baliviya.com.github.cznBot.dao.impl;

import baliviya.com.github.cznBot.dao.AbstractDao;
import baliviya.com.github.cznBot.entity.custom.Quest;
import baliviya.com.github.cznBot.entity.standart.Button;
import baliviya.com.github.cznBot.entity.standart.Language;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class QuestDao extends AbstractDao<Quest> {

    public void insert(Quest quest) {
        sql = "INSERT INTO standard.quests(id, quest_answer, id_survey, lang_id) VALUES (?,?,?,?)";
        getJdbcTemplate().update(sql, setParam(quest.getId(), quest.getQuestAnswer(), quest.getIdSurvey(), quest.getLanguageId()));
    }

    public List<Quest> getAll(int surveyId, Language language) {
        sql = "SELECT * FROM standard.quests WHERE id_survey = ? AND lang_id = ? ORDER BY id";
        return getJdbcTemplate().query(sql, setParam(surveyId, language.getId()), this::mapper);
    }

    public Quest getById(int id, Language language) {
        sql = "SELECT * FROM standard.quests WHERE id = ? AND lang_id = ?";
        return getJdbcTemplate().queryForObject(sql, setParam(id, language.getId()), this::mapper);
    }

    public void update(Quest quest) {
        sql = "UPDATE standard.quests SET quest_answer = ? WHERE id = ? AND lang_id = ?";
        getJdbcTemplate().update(sql, quest.getQuestAnswer(), quest.getId(), quest.getLanguageId());
    }

    public void deleteByQuestId(int surveyId) {
        sql = "DELETE FROM standard.quests WHERE id_survey = ?";
        getJdbcTemplate().update(sql, surveyId);
    }

    public void delete(int id) {
        sql = "DELETE FROM standard.quests WHERE id = ?";
        getJdbcTemplate().update(sql, id);
    }

    @Override
    protected Quest mapper(ResultSet rs, int index) throws SQLException {
        Quest quest = new Quest();
        quest.setId(rs.getInt(1));
        quest.setQuestAnswer(rs.getString(2));
        quest.setIdSurvey(rs.getInt(3));
        quest.setLanguageId(rs.getInt(4));
        return quest;
    }
}
