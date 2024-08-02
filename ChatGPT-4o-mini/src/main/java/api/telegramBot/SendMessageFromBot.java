package api.telegramBot;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class SendMessageFromBot {

    public static void sndMsg(Long chatId, String text) {

        SendMessage sendMessage = new SendMessage(
                chatId.toString(),
                formatMarkdownV2(text));

        sendMessage.enableMarkdownV2(true);

        try {
            Bot.TELEGRAM_CLIENT.execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    private static String formatMarkdownV2(String text) {

        String startCode = "```(java|python|c\\+\\+|c|javaScript|php|c#|csharp|go)";
        String stopCode = "```";
        String specSymbols = "([_*\\[\\]()~><#+\\-=|{}.!\\\\`])";
        boolean flag = false;
        StringBuilder cleanText = new StringBuilder();

        for (String s : text.split("\n")) {
            if (s.matches(startCode) || flag) {
                cleanText.append(s).append("\n");
                flag = !s.matches(stopCode) || !flag;

            } else {

                String escapedText = s.replaceAll(specSymbols, "\\\\$1");
                cleanText.append(escapedText).append("\n");
            }
        }

        return cleanText.toString();
    }
}