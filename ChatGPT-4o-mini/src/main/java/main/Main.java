package main;

import api.OpenAi;
import api.YandexTranslate;

import api.telegramBot.Bot;
import database.ConnectToDB;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class Main {

    public static void main(String[] args) {

        ConnectToDB  connectToDB  = new ConnectToDB();
        DataExplorer dataExplorer = new DataExplorer(connectToDB);
        Bot          bot          = new Bot(dataExplorer);

        try {

            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(bot.getTOKEN_BOT(), bot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        new Thread(new YandexTranslate (dataExplorer)).start();
        new Thread(new OpenAi          (dataExplorer, OpenAi.ModelEnum.GPT_4O_MINI)).start();
        new Thread(new Postman         (dataExplorer)).start();
    }
}