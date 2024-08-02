package main;

import api.telegramBot.SendMessageFromBot;
import database.ConnectToDB;
import database.EntityUserChat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataExplorer {

    private final           String                YOU_WONT_PASS  = "https://2ip.ru/member_photo/499398.gif";
    public final static     String                EnglishRegex   = "[^\u0400-\u04FF\u0500-\u052F]+";
    private final transient String                PASSWORD       = "пароль";
    public final static     long                  ADMIN_ID       = 0; // CHAT_ID_ADMIN;
    private final ConcurrentHashMap<Long, String> dataTranslate  = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> dataPostman    = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> dataOpenAi     = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> listEnableUser = new ConcurrentHashMap<>();
    private final ConnectToDB connectToDatabase;

    public DataExplorer(ConnectToDB connectToDatabase) {
        this.connectToDatabase = connectToDatabase;
        recoveryEnableUserDB();
    }

    private void recoveryEnableUserDB() {

        List<EntityUserChat> recovery = connectToDatabase.getAllRecords();
        if (recovery != null) {

            for (EntityUserChat record: recovery) {

                long chatId = record.getChatId();
                String name = record.getName();
                String context = record.getText();
                listEnableUser.put(chatId, name);
                connectToDatabase.recoveryContextDB(chatId,context);
            }
        }
    }
    public void putUser(Long chatId, String name) {

        listEnableUser.put(chatId, name);
        System.out.println("Добавлен новый пользователь: " + name + "\nchatId: " + chatId);
//        SendMessageFromBot.sndMsg(ADMIN_ID, "Добавлен новый пользователь: " + name + "\nchatId: " + chatId);
    }
    public void putUserNameDB(Long chatId, String name) {

        listEnableUser.put(chatId, name);
        connectToDatabase.createRecord(chatId, name);
        System.out.println("Добавлен новый пользователь: " + name + "\nchatId: " + chatId);
//        SendMessageFromBot.sndMsg(ADMIN_ID, "Добавлен новый пользователь: " + name + "\nchatId: " + chatId);
    }
    public String getUsername(long chatId) {
        String username = listEnableUser.get(chatId);
        return username == null? "": username;
    }
    public void removeUser(Long chatId) {

        if (!chatId.equals(ADMIN_ID)) {
            String name = listEnableUser.get(chatId);
            listEnableUser.remove(chatId);
            connectToDatabase.deleteRecord(chatId);
//            SendMessageFromBot.sndMsg(ADMIN_ID, "Удален пользователь: " + name + "\nchatId: " + chatId);
        } else {
            System.out.println("Warning -deleteUserInList");
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

    public boolean checkPasswordCorrect(String password, long chatId) {
        if (this.PASSWORD.equals(password)) {

            SendMessageFromBot.sndMsg(chatId,"Right!");
            listEnableUser.put(chatId, "");
            return true;
        }
        SendMessageFromBot.sndMsg(chatId,"You won't pass " + YOU_WONT_PASS + "\nWrong password");
        return false;
    }
}