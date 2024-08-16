package main;

import api.telegramBot.SendMessageFromBot;

import java.util.Map;

public class Postman implements Runnable {

    private final DataExplorer dataExplorer;
    private final int          LIMIT_LENGTH = 4096;

    public Postman(DataExplorer dataExplorer) {
        this.dataExplorer = dataExplorer;
    }

    @Override
    public void run() {

        while (true) {

            if (dataExplorer.getDataPostman().size() > 0) {

                for (Map.Entry<Long, String> entry : dataExplorer.getDataPostman().entrySet()) {

                    Long chatId = entry.getKey();
                    String text = entry.getValue();
                    dataExplorer.removeDataPostman(chatId);

                    if(text.length() >= LIMIT_LENGTH) {
                        largeText(chatId, text);
                    } else {
                        SendMessageFromBot.sndMsg(chatId, text);
                    }
                }
            }
        }
    }

    private void largeText(long chatId, String text) {

        int i = 0;
        StringBuilder result = new StringBuilder();

        for (char symbol: text.toCharArray()) {
            if (i >= (LIMIT_LENGTH -1)) {
                SendMessageFromBot.sndMsg(chatId, result.toString());
                result.setLength(0);
                i = 0;
            }
            result.append(symbol);
            i++;
        }
    }
}