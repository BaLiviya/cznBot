package baliviya.com.github.cznBot.entity.custom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Quest {

    private int id;
    private String questAnswer;
    private int idSurvey;
    private int languageId;

    public Quest setId(int id) {
        this.id = id;
        return this;
    }

    public Quest setIdLanguage(int languageId) {
        this.languageId = languageId;
        return this;
    }

    public Quest setQuestAnswer(String questAnswer) {
        this.questAnswer = questAnswer;
        return this;
    }
}
