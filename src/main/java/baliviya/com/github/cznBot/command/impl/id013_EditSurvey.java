package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.dao.impl.SurveyDao;
import baliviya.com.github.cznBot.entity.custom.Quest;
import baliviya.com.github.cznBot.entity.custom.Survey;
import baliviya.com.github.cznBot.entity.standart.Language;
import baliviya.com.github.cznBot.service.LanguageService;
import baliviya.com.github.cznBot.util.ButtonUtil;
import baliviya.com.github.cznBot.util.ButtonsLeaf;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.UpdateUtil;
import baliviya.com.github.cznBot.util.type.WaitingType;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class id013_EditSurvey extends Command {

    private ButtonsLeaf buttonsLeaf;
    private List<Survey> all;
    private int surveyId;
    private int questId;
    private Survey survey;
    private Quest quest;
    private List<Quest> questMessageList;
    private static SurveyDao surveyDao = factory.getSurveyDao();
    private Language currentLanguage;
    private WaitingType updateType = WaitingType.START;
    private int editionMessageId;
    private Quest addQuest;


    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        switch (updateType) {
            case UPDATE_SURVEY:
                if (isCommand()) {
                    return COMEBACK;
                }
                break;
            case UPDATE_QUEST:
                if (isCommandQuest()) {
                    return COMEBACK;
                }
                break;
        }
        switch (waitingType) {
            case START:
                currentLanguage = LanguageService.getLanguage(chatId);
                all = surveyDao.getAll(currentLanguage);
                buttonsLeaf = new ButtonsLeaf(all.stream().map(Survey::getSurveyName).collect(Collectors.toList()));
                toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_SURVEY_FOR_EDIT_TEXT, buttonsLeaf.getListButton()));
                waitingType = WaitingType.CHOOSE_SURVEY_FOR_EDIT;
                return COMEBACK;
            case CHOOSE_SURVEY_FOR_EDIT:
                if (hasCallbackQuery()) {
                    deleteMessage();
                    if (buttonsLeaf.isNext(updateMessageText)) {
                        toDeleteKeyboard(sendMessageWithKeyboard(Const.CHOOSE_SURVEY_FOR_EDIT_TEXT, buttonsLeaf.getListButton()));
                    }
                    surveyId = all.get(Integer.parseInt(updateMessageText)).getId();
                    waitingType = WaitingType.EDITION;
                    updateType = WaitingType.UPDATE_SURVEY;
                    sendMessage(Const.KEYBOARD_EDIT);
                    sendEditor();
                    return COMEBACK;
                }
                return COMEBACK;
            case EDITION:
                isCommand();
                return COMEBACK;
            case QUEST_EDITION:
                isCommandQuest();
                return COMEBACK;
            case SET_NEW_NAME:
                if (hasMessageText()) {
                    survey.setSurveyName(ButtonUtil.getButtonName(updateMessageText, 200));
                    surveyDao.update(survey);
                    sendEditor();
                }
                return COMEBACK;
            case SET_NEW_QUEST_TEXT:
                if (hasMessageText()) {
                    survey.setQuestText(updateMessageText);
                    surveyDao.updateQuestText(survey);
                    sendEditor();
                }
                return COMEBACK;
            case SET_RANGE:
                if (hasMessageText()) {
                    if (quest.getQuestAnswer().split(Const.SPLIT_RANGE).length == updateMessageText.split(Const.SPLIT_RANGE).length) {
                        quest.setQuestAnswer(updateMessageText);
                        questDao.update(quest);
                    } else {
                        for (Language language : Language.values()) {
                            Quest byId = questDao.getById(questId, language);
                            byId.setQuestAnswer(updateMessageText);
                            questDao.update(byId);
                        }
                        sendMessage(Const.WRONG_NUMBER_CHOICE);
                    }
                    sendEditorQuest();
                }
                return COMEBACK;
            case DELETE:
                if (hasMessageText() && updateMessageText.equals(Const.DELETE_MESSAGE)) {
                    factory.getSurveyAnswerDao().deleteByQuestId(surveyId);
                    questDao.deleteByQuestId(surveyId);
                    surveyDao.delete(surveyId);
                    getLogger().info("Deleted question №{} - {}", questId, UpdateUtil.getUser(update).toString());
                    sendMessage(Const.SURVEY_DELETE_MESSAGE);
                    return EXIT;
                }
                return COMEBACK;
            case DELETE_QUEST:
                if (hasMessageText() && updateMessageText.equalsIgnoreCase(Const.DELETE_MESSAGE)) {
                    questDao.delete(questId);
                    getLogger().info("Deleted message №{} for quest №{} - {}", questId, surveyId, UpdateUtil.getUser(update).toString());
                    sendMessage(Const.CHOICE_DELETE_MESSAGE);
                    sendEditor();
                }
                return COMEBACK;
            case GET_RANGE:
                if (hasMessageText()) {
                    addQuest = new Quest();
                    addQuest.setQuestAnswer(updateMessageText).setIdSurvey(surveyId);
                    for (Language language : Language.values()) {
                        questDao.insert(addQuest.setIdLanguage(language.getId()));
                    }
                    sendEditor();
                }
                return COMEBACK;
        }
        return EXIT;
    }

    private void sendEditor() throws TelegramApiException {
        deleteMessage(editionMessageId);
        loadQuest();
        String text = String.format(getText(Const.NAME_SURVEY_NAME_QUEST_TEXT), messageDao.getMessageText(Const.Ru_KZ, currentLanguage), survey.getSurveyName(), survey.getQuestText());
        buttonsLeaf = new ButtonsLeaf(questMessageList.stream().map(Quest::getQuestAnswer).collect(Collectors.toList()));
        editionMessageId = sendMessageWithKeyboard(text, buttonsLeaf.getListButton());
        toDeleteKeyboard(editionMessageId);
        waitingType = WaitingType.EDITION;
    }

    private void sendEditorQuest() throws TelegramApiException {
        deleteMessage(editionMessageId);
        quest = questDao.getById(questId, currentLanguage);
        String text = String.format(getText(Const.QUEST_EDIT_TEXT), messageDao.getMessageText(Const.Ru_KZ, currentLanguage), quest.getQuestAnswer());
        sendMessageWithKeyboard(text, Const.EDIT_QUEST_KEYBOARD);
        waitingType = WaitingType.QUEST_EDITION;
    }

    private void loadQuest() {
        survey = surveyDao.getById(surveyId, currentLanguage);
        questMessageList = questDao.getAll(surveyId, currentLanguage);
    }

    private boolean isCommand() throws TelegramApiException {
        if (hasCallbackQuery()) {
            questId = questMessageList.get(Integer.parseInt(updateMessageText)).getId();
            updateType = WaitingType.UPDATE_QUEST;
            sendEditorQuest();
        } else if (isButton(Const.CHANGE_SURVEY_NAME)) {
            sendMessage(Const.SET_NEW_NAME_SURVEY_TEXT);
            waitingType = WaitingType.SET_NEW_NAME;
        } else if (isButton(Const.CHANGE_QUEST_TEXT)) {
            sendMessage(Const.SET_NEW_QUEST_TEXT);
            waitingType = WaitingType.SET_NEW_QUEST_TEXT;
        } else if (isButton(Const.DELETE_BUTTON)) {
            sendMessage(Const.DELETE_TEXT);
            waitingType = WaitingType.DELETE;
        } else if (isButton(Const.CHANGE_LANGUAGE)) {
            changeLanguage();
            sendEditor();
        } else if (isButton(Const.ADD_CHOICE)) {
            sendMessage(Const.SET_ANSWER_FOR_QUEST_RU_TEXT);
            waitingType = WaitingType.GET_RANGE;
        } else {
            return false;
        }
        return true;
    }

    private void changeLanguage() {
        if (currentLanguage == Language.ru) {
            currentLanguage = Language.kz;
        } else {
            currentLanguage = Language.ru;
        }
    }

    public boolean isCommandQuest() throws TelegramApiException {
        if (isButton(Const.CHANGE_CHOICE)) {
            sendMessage(Const.SET_NEW_CHOICE_TEXT);
            waitingType = WaitingType.SET_RANGE;
        } else if (isButton(Const.DELETE_BUTTON)) {
            sendMessage(Const.QUEST_DELETE_TEXT);
            waitingType = WaitingType.DELETE_QUEST;
        } else if (isButton(Const.CHANGE_LANGUAGE)) {
            changeLanguage();
            sendEditorQuest();
        } else if (isButton(Const.BACK_BUTTON)) {
            updateType = WaitingType.UPDATE_SURVEY;
            sendMessage(Const.KEYBOARD_EDIT);
            sendEditor();
        } else {
            return false;
        }
        return true;
    }
}
