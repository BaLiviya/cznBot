package baliviya.com.github.cznBot.dao;

import baliviya.com.github.cznBot.config.Conversation;
import baliviya.com.github.cznBot.dao.enums.TableNames;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.service.LanguageService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

@NoArgsConstructor
public abstract class AbstractDao<T> {

    protected static DaoFactory factory = DaoFactory.getFactory();
    private static final Logger logger = LoggerFactory.getLogger(AbstractDao.class);

    protected String sql;

    private long getChatId() {
        return Conversation.getCurrentChatId();
    }

    protected Language getLanguage() {
        if (getChatId() == 0) return Language.ru;
        return LanguageService.getLanguage(getChatId());
    }

    public int getNextId(TableNames tableNames) {
        return getNextId(tableNames.name());
    }

    public int getNextId(String tableNames) {
        sql = "SELECT MAX(id) FROM standard." + tableNames;
        try {
            return getJdbcTemplate().queryForObject(sql, Integer.class) + 1;
        } catch (Exception e) {
            logger.info("getNextId for {} has exception, return id = 1", tableNames);
            logger.error("getNextId:", e);
            return 1;
        }
    }

    protected Object[] setParam(Object... args) {
        return args;
    }

    protected abstract T mapper(ResultSet rs, int index) throws SQLException;

    private static DataSource getDataSource() {
        return DaoFactory.getDataSource();
    }

    protected static JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }
}
