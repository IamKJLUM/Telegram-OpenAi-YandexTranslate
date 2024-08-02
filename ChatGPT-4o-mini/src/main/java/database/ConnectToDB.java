package database;

import api.OpenAi;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectToDB {

    private final ConcurrentHashMap<Long, String> cashDB = new ConcurrentHashMap<>();
    private final Session session;
    private final int     LIMIT_MESSAGE     = 15;
    private final int     LIMIT_TEXT_LENGTH = 6500;
    private final float   COMPRESSION_TEXT  = 1.5f;
    private final int     THRESHOLD_MESSAGE = 3;
    private final OpenAi  OPEN_AI           = new OpenAi();

    public ConnectToDB() {
        this.session = SingletonSession.getInstance().getSession();
    }

    public void recoveryContextDB(long chatId, String context) {
        cashDB.put(chatId, context);
    }

    private void logic(long chatId) {

        EntityUserChat chat         = getRecordFromDB(chatId);
        String         name         = chat.getName();
        String         context      = textProcessing(chat.getText(), chatId);
        int            countMessage = chat.getCountMessage();

        if (countMessage >= LIMIT_MESSAGE || context.length() >= LIMIT_TEXT_LENGTH) {

            createRecord(chatId, name);
        } else {
            if ((countMessage + 1) % THRESHOLD_MESSAGE == 0) {

                context = compressionText(context);
                cashDB.put(chatId, context);
            }
            chat.setChatId(chatId);
            chat.setCountMessage(countMessage + 1);
            chat.setText(context);
            updateRecord(chat);
        }
    }

    private String textProcessing(String context, long chatId) {

        String text = cashDB.get(chatId);
        int startIndex = findDivergenceIndex(context, text);

        if (startIndex == -1) {
            return text;
        }

        return context + text.substring(startIndex);
    }

    private int findDivergenceIndex(String context, String text) {

        int minLength = Math.min(context.length(), text.length());

        for (int i = 0; i < minLength; i++) {
            if (context.charAt(i) != text.charAt(i)) {
                return i;
            }
        }

        if (context.length() <= text.length()) {
            return context.length();
        }

        return -1;
    }

    private String compressionText(String text) {
        String prompt = "Сократи текст беседы в " + COMPRESSION_TEXT + " раза не меняя структуру текста. Постарайся сохранить ключевые слова и фразы, которые несут основную смысловую нагрузку беседы. В своем ответе кроме сокращенного текста больше ничего не пиши. Текст:\n";
        return OPEN_AI.requestOpenAi(OPEN_AI.responseOpenAi(prompt + text));
    }

    private EntityUserChat getRecordFromDB(Long chatId) {

        EntityUserChat chatUser = null;

        try {
            chatUser = session.get(EntityUserChat.class, chatId);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatUser;
    }

    public void createRecord(long chatId, String name) {

        EntityUserChat newUserChat = new EntityUserChat();
        newUserChat.setCountMessage(0);
        newUserChat.setChatId(chatId);
        newUserChat.setName(name);
        newUserChat.setText("");

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.merge(newUserChat);
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {

                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    private void updateRecord(EntityUserChat userChat) {

        Long chatId         = userChat.getChatId();
        String nameUser     = userChat.getName();
        String newText      = userChat.getText();
        int newCountMessage = userChat.getCountMessage();

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            EntityUserChat chatUser = session.get(EntityUserChat.class, chatId);

            if (chatUser != null) {

                chatUser.setText(newText);
                chatUser.setName(nameUser);
                chatUser.setCountMessage(newCountMessage);
                session.update(chatUser);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {

                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void putCashDBChatForId(long chatId, String text, String opponent) {

        String temp = getCashDBChatForId(chatId);

        cashDB.put(chatId, (temp == null?
                "": temp).concat(
                opponent
                        .concat(
                                " - ".concat(text))
                        .concat("\n")));

        if (opponent.equals("ChatGPT")) {
            logic(chatId);
        }
    }

    public String getCashDBChatForId(long chatId) {
        String cash = cashDB.get(chatId);
        return cash == null? "": cash;
    }

    public void deleteRecord(Long chatId) {

        Transaction transaction = null;

        try {

            transaction = session.beginTransaction();
            EntityUserChat chatUser = session.get(EntityUserChat.class, chatId);

            if (chatUser != null) {

                session.delete(chatUser);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null) {

                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<EntityUserChat> getAllRecords() {
        List<EntityUserChat> chatUsers = null;
        try {
            Query<EntityUserChat> query = session.createQuery("from EntityUserChat", EntityUserChat.class);
            chatUsers = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chatUsers;
    }
}