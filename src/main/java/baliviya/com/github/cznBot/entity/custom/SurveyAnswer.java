package baliviya.com.github.cznBot.entity.custom;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class SurveyAnswer {

    private int id;
    private int surveyId;
    private long chatId;
    private String button;
}
