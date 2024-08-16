package main;

import database.ConnectToDB;
import database.EntityUserChat;
import api.telegramBot.SendMessageFromBot;
import static api.telegramBot.Bot.ADMIN_ID;

import java.util.Map;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class DataExplorer {

    public final static String                          EnglishRegex   = "[^\u0400-\u04FF\u0500-\u052F]+";
    private final       ConcurrentHashMap<Long, String> dataTranslate  = new ConcurrentHashMap<>();
    private final       ConcurrentHashMap<Long, String> dataPostman    = new ConcurrentHashMap<>();
    private final       ConcurrentHashMap<Long, String> dataOpenAi     = new ConcurrentHashMap<>();
    private final       ConcurrentHashMap<Long, String> listEnableUser = new ConcurrentHashMap<>();
    private final       ConnectToDB                     connectToDatabase;

    public DataExplorer(ConnectToDB connectToDatabase) {
        this.connectToDatabase = connectToDatabase;
        recoveryEnableUserDB();
    }

    public void adminSaid(long chatId, String text) {
        putPostman(chatId, text);
    }
    public void adminSaid(String text, String name) {
        for (EntityUserChat entityUserChat: connectToDatabase.getAllRecords()) {

            if (entityUserChat.getName().equals(name)) {
                putPostman(entityUserChat.getChatId(), text);
            }
        }
    }
    public void adminSaid(String text) {

        for (long chatId: listEnableUser.keySet()) {
            adminSaid(chatId, text);
        }
    }

    public void promtForUsers(long chatId, String text) {
        putDataOpenAi(chatId, text);
    }
    public void promtForUsers(String text, String name) {
        for (EntityUserChat entityUserChat: connectToDatabase.getAllRecords()) {

            if (entityUserChat.getName().equals(name)) {
                putDataOpenAi(entityUserChat.getChatId(), text);
            }
        }
    }
    public void promtForUsers(String text) {

        for (long chatId: listEnableUser.keySet()) {
            promtForUsers(chatId, text);
        }
    }

    private void recoveryEnableUserDB() {

        List<EntityUserChat> recovery = connectToDatabase.getAllRecords();
        if (recovery != null) {

            for (EntityUserChat record: recovery) {

                long chatId = record.getChatId();
                String name = record.getName();
                String context = record.getText();
                listEnableUser.put(chatId, name);
                connectToDatabase.recoveryContextDB(chatId, context);
            }
        }
    }
    public void putUser(Long chatId, String name) {

        listEnableUser.put(chatId, name);
//        SendMessageFromBot.sndMsg(ADMIN_ID, "Добавлен новый пользователь: " + name + "\nchatId: " + chatId);
    }
    public void putUserNameDB(Long chatId, String name) {

        if (connectToDatabase.createRecord(chatId, name, 0)) {
            listEnableUser.put(chatId, name);
            System.out.println("The user has been added to the database:\nname: [" + name + "]\nchatId: [" + chatId + "]");
        SendMessageFromBot.sndMsg(ADMIN_ID, "Добавлен новый пользователь: " + name + "\nchatId: " + chatId);
        } else {
            listEnableUser.put(chatId, "");
            System.err.println("The user can't added to the database");
            SendMessageFromBot.sndMsg(chatId, "Вы используете недопустимые символы или слишком длинное имя, введите имя еще раз");
        }
    }
    public String getUser(long chatId) {
        return listEnableUser.get(chatId);
    }
    public void removeUser(String name) {

        Long chatId = listEnableUser.entrySet().stream().
                filter(longStringEntry -> longStringEntry.getValue().equals(name)).
                map(Map.Entry::getKey).
                findFirst().
                orElse(null);

        if (chatId != null && chatId != ADMIN_ID) {

            listEnableUser.remove(chatId);
            connectToDatabase.deleteRecord(chatId);
            SendMessageFromBot.sndMsg(ADMIN_ID, "User [" + name + "] delete\nchatId: " + chatId);
        } else {
            System.out.println("The user could not be deleted:\nchatId: [" + chatId + "]");
        }
    }

    public Map<Long, String> getDataTranslate() {
        return dataTranslate;
    }
    public void putTranslate(Long chatId, String text) {
        if (chatId > 0)
        dataTranslate.put(chatId, text);
    }
    public void removeDataTranslate(Long chatId) {
        if (chatId > 0)
            dataTranslate.remove(chatId);
    }

    public Map<Long, String> getDataPostman() {
        return dataPostman;
    }
    public void putPostman(Long chatId, String text) {
        if (chatId > 0) {
            dataPostman.put(chatId, text);
            connectToDatabase.putCashDBChatForId(
                    chatId,
                    text,
                    "ChatGPT");
        }
    }
    public void removeDataPostman(Long chatId) {
        if (chatId > 0)
            dataPostman.remove(chatId);
    }

    public Map<Long,String> getDataOpenAi() {
        return dataOpenAi;
    }
    public void putDataOpenAi(Long chatId, String text) {
        if (chatId > 0)

        dataOpenAi.put(chatId, connectToDatabase.getCashDBChatForId(chatId).concat(text));
        connectToDatabase.putCashDBChatForId(
                chatId,
                text,
                "User");
    }
    public void removeDataOpenAi(Long chatId) {
        if (chatId > 0)
            dataOpenAi.remove(chatId);
    }
}