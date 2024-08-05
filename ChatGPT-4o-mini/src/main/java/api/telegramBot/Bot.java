package api.telegramBot;

import main.DataExplorer;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

public class Bot implements LongPollingSingleThreadUpdateConsumer {

    private final           String         YOU_WONT_PASS   = "https://2ip.ru/member_photo/499398.gif";
    final private           DataExplorer   dataExplorer;
    final static            String         TOKEN_BOT       = "TOKEN_BOT";
    static final            TelegramClient TELEGRAM_CLIENT = new OkHttpTelegramClient(TOKEN_BOT);
    private final transient String         PASSWORD        = "пароль";

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

                checkNameOrLaunchingDialog(chatId, username, text);
            }
        }
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