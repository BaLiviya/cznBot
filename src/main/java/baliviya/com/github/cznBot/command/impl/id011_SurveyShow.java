package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.entity.custom.Quest;
import baliviya.com.github.cznBot.entity.custom.Survey;
import baliviya.com.github.cznBot.entity.custom.SurveyAnswer;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.service.LanguageService;
import baliviya.com.github.cznBot.util.ButtonsLeaf;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class id011_SurveyShow extends Command {

    private Language currentLanguage;
    private List<Survey> allSurveys;
    private List<Survey> otherSurveys;
    private ButtonsLeaf buttonsLeaf;
    private Survey survey;
    private List<Quest> allQuest;
    private List<String> listAnswers;
    private SurveyAnswer surveyAnswer;

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        switch (waitingType) {
            case START:
                currentLanguage = LanguageService.getLanguage(chatId);
                allSurveys = factory.getSurveyDao().getAllActive(currentLanguage, chatId);
                if (allSurveys == null || allSurveys.size() == 0) {
                    sendMessage(Const.SURVEY_END);
                    return EXIT;
                }
                sendMessage(Const.KEYBOARD_FROM_SURVEY);
                List<String> list = new ArrayList<>();
                allSurveys.forEach((e) -> list.add(e.getSurveyName()));
                buttonsLeaf = new ButtonsLeaf(list);
                toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_SURVEY_TEXT, buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOOSE_SURVEY;
                return COMEBACK;
            case CHOOSE_SURVEY:
                if (hasCallbackQuery()) {
                    deleteMessage();
                    if (buttonsLeaf.isNext(updateMessageText)) {
                        deleteMessage();
                        toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_SURVEY_TEXT, buttonsLeaf.getListButton()));
                    } else {
                        survey = allSurveys.get(Integer.parseInt(updateMessageText));
                        allQuest = factory.getQuestDao().getAll(survey.getId(), currentLanguage);
                        listAnswers = new ArrayList<>();
                        allQuest.forEach((e) -> Collections.addAll(listAnswers, e.getQuestAnswer().split(",")));
                        buttonsLeaf = new ButtonsLeaf(listAnswers);
                        toDeleteKeyboard(sendMessageWithKeyboard(survey.getQuestText(), buttonsLeaf.getListButton()));
                        waitingType = WaitingType.CHOOSE_OPTION_SURVEY;
                    }
                }
                return COMEBACK;
            case CHOOSE_OPTION_SURVEY:
                if (hasCallbackQuery()) {
                    deleteMessage();
                    String button = listAnswers.get(Integer.parseInt(updateMessageText));
                    surveyAnswer = new SurveyAnswer();
                    surveyAnswer.setButton(button);
                    surveyAnswer.setChatId(chatId);
                    surveyAnswer.setSurveyId(survey.getId());
                    factory.getSurveyAnswerDao().insert(surveyAnswer);

                    allSurveys = factory.getSurveyDao().getAllActive(currentLanguage, chatId);
                    if (allSurveys == null || allSurveys.size() == 0) {
                        sendMessage(Const.SURVEY_END);
                        return EXIT;
                    }
                    sendMessage(Const.KEYBOARD_FROM_SURVEY);
                    List<String> listSurveyName = new ArrayList<>();
                    allSurveys.forEach((e) -> listSurveyName.add(e.getSurveyName()));
                    buttonsLeaf = new ButtonsLeaf(listSurveyName);
                    toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_SURVEY_TEXT, buttonsLeaf.getListButton()));
                    waitingType = WaitingType.CHOOSE_SURVEY;
                    return COMEBACK;
                }
                return COMEBACK;
        }
        return EXIT;
    }

}
