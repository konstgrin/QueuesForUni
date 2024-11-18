package org.justgroup_;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World i guess...");

        String botToken = "";
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            properties.load(fis);
            botToken = properties.getProperty("TELEGRAM_BOT_TOKEN");
        } catch (IOException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyBot(botToken));
            System.out.println("Bot successfully started");
            // Ensure this process wait forever
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
    }
}