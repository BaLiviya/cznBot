package baliviya.com.github.cznBot.entity.custom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Survey {

    private int id;
    private String surveyName;
    private String questsId;
    private SurveyType surveyType;
    private int languageId;
    private boolean isHide;
    private String questText;

    public Survey setQuestText(String questText) {
        this.questText = questText;
        return this;
    }
}
