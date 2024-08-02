package main;

import api.telegramBot.SendMessageFromBot;

import java.util.Map;

public class Postman implements Runnable {

    private final DataExplorer dataExplorer;

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

                    if (text.length() > 3000) {

                        int look = 1;

                        for (;;) {

                            if (look + 3000 > text.length()) {

                                SendMessageFromBot.sndMsg(DataExplorer.ADMIN_ID,text.replace("\"", "").substring(look));
                                break;
                            }


                            SendMessageFromBot.sndMsg(
                                    chatId,
                                    text.replace("\"", "").substring(look,look + 3000));

                            look += 3000;

                            try {
                                Thread.sleep(4000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }


                    } else {

                        SendMessageFromBot.sndMsg(chatId, text);
                    }
                }
            }
        }
    }
}