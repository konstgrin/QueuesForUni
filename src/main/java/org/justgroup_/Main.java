package org.justgroup_;

import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World i guess...");

        String botToken = "7846865433:AAHUMvK_g3s2mLCRm-UbLUz-9fKAzTzWxfw";
        // Using try-with-resources to allow autoclose to run upon finishing
        try (TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication()) {
            botsApplication.registerBot(botToken, new MyBot(botToken));
            System.out.println("Bot successfully started");
            // Ensure this prcess wait forever
            Thread.currentThread().join();
        } catch (Exception e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        }
    }
}