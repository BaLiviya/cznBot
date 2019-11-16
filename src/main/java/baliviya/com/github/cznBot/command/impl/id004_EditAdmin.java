package baliviya.com.github.cznBot.command.impl;

import baliviya.com.github.cznBot.command.Command;
import baliviya.com.github.cznBot.entity.standart.User;
import baliviya.com.github.cznBot.util.Const;
import baliviya.com.github.cznBot.util.DateUtil;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class id004_EditAdmin extends Command {

    private int mess;
    private static String delete;
    private static String deleteIcon;
    private static String showIcon;
    private StringBuilder text;
    private List<Long> allAdmins;

    @Override
    public boolean execute() throws SQLException, TelegramApiException {
        if (!isAdmin()) {
            sendMessage(Const.NO_ACCESS);
            return EXIT;
        }
        if (deleteIcon == null) {
            deleteIcon = getText(Const.ICON_CROSS);
            showIcon = getText(Const.ICON_LOUPE);
            delete = getText(Const.DELETE_BUTTON_SLASH);
        }
        if (mess != 0) {
            deleteMessage(mess);
        }
        if (hasContact()) {
            registerNewAdmin();
            return COMEBACK;
        }
        if (updateMessageText.contains(delete)) {
            if (allAdmins.size() > 1) {
                int numberAdminList = Integer.parseInt(updateMessageText.replaceAll("[^0-9]",""));
                adminDao.delete(allAdmins.get(numberAdminList));
            }
        }
        sendEditorAdmin();
        return COMEBACK;
    }

    private boolean registerNewAdmin() throws TelegramApiException, SQLException {
        long newAdminChatId = update.getMessage().getContact().getUserID();
        if (!usersDao.isRegistered(newAdminChatId)) {
            sendMessage(Const.USER_DO_NOT_REGISTERED);
            return true;
        } else {
            if (adminDao.isAdmin(newAdminChatId)) {
                sendMessage(Const.USER_IS_ADMIN);
                return true;
            } else {
                User user = usersDao.getUserByChatId(newAdminChatId);
                adminDao.addAssistant(newAdminChatId,String.format("%s %s %s", user.getUserName(), user.getPhone(), DateUtil.getDbMmYyyyHhMmSs(new Date())));
                User userAdmin = usersDao.getUserByChatId(chatId);
                getLogger().info("{} added new admin - {} ", getInfoByUser(userAdmin), getInfoByUser(user));
                sendEditorAdmin();
            }
        }
        return false;
    }

    private void sendEditorAdmin() throws SQLException, TelegramApiException {
        deleteMessage();
        try {
            getText(true);
            mess = sendMessage(String.format(getText(Const.ADMIN_SHOW_LIST), text.toString()));
        } catch (TelegramApiException e) {
            getText(false);
            mess = sendMessage(String.format(getText(Const.ADMIN_SHOW_LIST), text.toString()));
        }
        toDeleteMessage(mess);
    }

    private String getInfoByUser(User user) {
        return String.format("%s %s %s", user.getFullName(), user.getPhone(), user.getChatId());
    }

    private void getText(boolean withLink) throws SQLException {
        text = new StringBuilder("");
        allAdmins = adminDao.getAll();
        int count = 0;
        for (Long admin : allAdmins) {
            try {
                User user = usersDao.getUserById(admin);
                if (allAdmins.size() == 1) {
                    if (withLink) {
                        text.append(getLinkForUser(user.getChatId(), user.getUserName())).append(space).append(next);
                    } else {
                        text.append(getInfoByUser(user)).append(space).append(next);
                    }
                    text.append(getText(Const.WARNING_INFO_ABOUT_ADMIN)).append(next);
                    count++;
                } else {
                    if (withLink) {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(showIcon).append(getLinkForUser(user.getChatId(), user.getUserName())).append(space).append(next);
                    } else {
                        text.append(delete).append(count).append(deleteIcon).append(" - ").append(getInfoByUser(user)).append(space).append(next);
                    }
                    count++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
