package api.telegramBot;

import main.DataExplorer;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;

public class Bot implements LongPollingSingleThreadUpdateConsumer {

    final private DataExplorer  dataExplorer;
    final static String         TOKEN_BOT = "5850080708:AAHh1tiLs28L0myK8crE0k2UrFxL6QdgfDo";
    static final TelegramClient TELEGRAM_CLIENT = new OkHttpTelegramClient(TOKEN_BOT);

    public Bot(DataExplorer dataExplorer) {

        this.dataExplorer = dataExplorer;
        System.out.println("Bot successful initialization");
    }

    @Override
    public void consume(Update update) {

        Message message = update.getMessage();
        String text = message.getText();
        long chatId = message.getChatId();

        if (text != null) {
            String username = dataExplorer.getUsername(chatId);

            if (text.startsWith("/start")) {
                SendMessageFromBot.sndMsg(chatId,"Введите пароль:");

            } else if (username != null){

                checkNameOrLaunchingDialog(
                        chatId,
                        username,
                        text);

            } else if (dataExplorer.checkPasswordCorrect(text, chatId)) {

                username = "";
                checkNameOrLaunchingDialog(
                        chatId,
                        username,
                        text);
            }
        }
    }

    private void checkNameOrLaunchingDialog(long chatId, String username, String text) {

        if (username.isEmpty()) {

            dataExplorer.putUser(chatId,"empty");
            SendMessageFromBot.sndMsg(chatId,"Введите ваше имя:");

        } else if (username.equals("empty")) {

            dataExplorer.putUserNameDB(chatId, text);
            SendMessageFromBot.sndMsg(chatId,"Добро пожаловать " + text);

        } else

            dataExplorer.putDataOpenAi(chatId, text);
    }

    public String getTOKEN_BOT() {
        return TOKEN_BOT;
    }
}