package baliviya.com.github.cznBot.entity.custom;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum SurveyType {

    button(1),
    mix(2),
    text(3);

    private int id;

    public static SurveyType getById(int id) {
        for (SurveyType surveyType : values()) {
            if (surveyType.id == (id)) return surveyType;
        }
        return null;
    }
}
