package baliviya.com.github.cznBot.command;

import baliviya.com.github.cznBot.command.impl.*;
import baliviya.com.github.cznBot.exception.NotRealizedMethodException;

public class CommandFactory {

    public static Command getCommand(long id) {
        Command result = getCommandWithoutReflection((int) id);
        if (result == null) throw new NotRealizedMethodException("Not realized for type: " + id);
        return result;
    }
    private static Command getCommandWithoutReflection(int id) {
        switch (id) {
            case 1:
                return new id001_ShowInfo();
            case 2:
                return new id002_SelectionLanguage();
            case 3:
                return new id003_ShowAdminInfo();
            case 4:
                return new id004_EditAdmin();
            case 5:
                return new id005_EditMenu();
            case 6:
                return new id006_MapLocationSend();
            case 7:
                return new id007_Suggestion();
            case 8:
                return new id008_ReportSuggestion();
            case 9:
                return new id009_Complaint();
            case 10:
                return new id010_ReportComplaint();
            case 11:
                return new id011_SurveyShow();
            case 12:
                return new id012_AddSurvey();
            case 13:
                return new id013_EditSurvey();
            case 14:
                return new id014_ReportSurvey();
        }
        return null;
    }
}
