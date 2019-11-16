package baliviya.com.github.cznBot.service;

import baliviya.com.github.cznBot.dao.DaoFactory;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.entity.standart.LanguageUser;

import java.util.HashMap;
import java.util.Map;

public class LanguageService {

    private static Map<Long, Language> langMap = new HashMap<>();

    public static Language getLanguage(long chatId) {
        Language language = langMap.get(chatId);
        if (language == null) {
            LanguageUser langUser = DaoFactory.getFactory().getLanguageUserDao().getByChatId(chatId);
            if (langUser != null) {
                language = langUser.getLanguage();
                langMap.put(chatId, language);
            }
        }
        return language;
    }

    public static void setLanguage(long chatId, Language language) {
        langMap.put(chatId, language);
        DaoFactory.getFactory().getLanguageUserDao().insertOrUpdate(new LanguageUser(chatId, language));
    }
}
