package api.telegramBot;

import main.DataExplorer;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

import java.util.Objects;

public class Bot implements LongPollingSingleThreadUpdateConsumer {

    final private           DataExplorer   dataExplorer;
    final static            String         TOKEN_BOT            = "";
    static final            TelegramClient TELEGRAM_CLIENT      = new OkHttpTelegramClient(TOKEN_BOT);
    public final static     long           ADMIN_ID             = 0;

    private final           String         MATCHER_CONTAINING   = "Трибуну (мне|бот) (" + NAME_PATTERN + "|всем) .*";
    private final           String         YOU_WONT_PASS        = "https://2ip.ru/member_photo/499398.gif";
    public static final     String         NAME_PATTERN         = "[\\p{L}\\p{N}\\p{Zs}\\p{So}]+";
    private final           String         COMMAND_DELETE_USER  = "-removeUserOfTheSystem";
    private final transient String         PASSWORD             = "";


    public Bot(DataExplorer dataExplorer) {

        this.dataExplorer = dataExplorer;
        System.out.println("Bot successful initialization");
    }

    @Override
    public void consume(Update update) {

        Message message = update.getMessage();
        String  text    = message.getText();
        long    chatId  = message.getChatId();

        if (message.hasText()) {

            String username = dataExplorer.getUser(chatId);

            if (username == null || text.startsWith("/start")) {
                if (!checkPasswordCorrect(text, chatId)) {
                    SendMessageFromBot.sndMsg(chatId,"Введите пароль:");
                }

            } else {

                if (chatId == ADMIN_ID) {
                    command(text);
                } else
                    checkNameOrLaunchingDialog(chatId, username, text);
            }
        }
    }

    private void command(String text) {

        if (text != null) {

            if (text.matches(NAME_PATTERN + COMMAND_DELETE_USER)) {
                dataExplorer.removeUser(getNameOfTheUserDelete(text));

            } else if (text.matches(MATCHER_CONTAINING)) {

                contactingUsers(text);
            } else
                dataExplorer.putDataOpenAi(ADMIN_ID, text);
        }
    }

    private void contactingUsers(String text) {

        int getTarget = 12;

        if (text.length() > getTarget) {

            String userOrUsers = Objects.requireNonNull(text).substring(getTarget);
            userOrUsers = userOrUsers.substring(0, userOrUsers.indexOf(" "));

            if (text.startsWith("мне", getTarget - 4)) {

                if (userOrUsers.equals("всем")) {

                    dataExplorer.adminSaid(formatterText(text, userOrUsers));
                } else {

                    dataExplorer.adminSaid(formatterText(text, userOrUsers), userOrUsers);
                }
            } else if (text.startsWith("бот", getTarget - 4)) {

                if (userOrUsers.equals("всем")) {

                    dataExplorer.promtForUsers(formatterText(text, userOrUsers));
                } else {

                    dataExplorer.promtForUsers(formatterText(text, userOrUsers), userOrUsers);
                }
            }
        }
    }

    private String getNameOfTheUserDelete(String text) {
        return text.substring(0, text.indexOf(COMMAND_DELETE_USER)).trim();
    }

    private String formatterText(String text, String userOrUsers) {
        return text.substring(userOrUsers.length() + 12);
    }

    public boolean checkPasswordCorrect(String password, long chatId) {

        if (PASSWORD.equals(password)) {

            SendMessageFromBot.sndMsg(chatId,"Верно!");
            dataExplorer.putUser(chatId, "");
            SendMessageFromBot.sndMsg(chatId,"Введите ваше имя:");
            return true;
        }
        SendMessageFromBot.sndMsg(chatId,"You won't pass " + YOU_WONT_PASS + "\nWrong password");
        return false;
    }

    private void checkNameOrLaunchingDialog(long chatId, String username, String text) {

        if (username.isEmpty()) {

            dataExplorer.putUser(chatId, text);
            dataExplorer.putUserNameDB(chatId, text);
            SendMessageFromBot.sndMsg(chatId,"Добро пожаловать " + text);

        } else
            dataExplorer.putDataOpenAi(chatId, text);
    }

    public String getTOKEN_BOT() {
        return TOKEN_BOT;
    }
}