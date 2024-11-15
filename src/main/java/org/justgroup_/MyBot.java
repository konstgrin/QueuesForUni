package org.justgroup_;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MyBot extends TelegramBotsLongPollingApplication implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    public MyBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        System.out.println(update.getMessage().getChatId() + " " + update.getMessage().getText());
        commandsHandler(update.getMessage().getText(), update.getMessage().getChatId());
    }

    public void commandsHandler(String command, long chatId) {
        switch (command){
            case "/start": startCommand(chatId); break;
        }
    }

    private void startCommand(long chatId){
        String textMessage = "Bot to create queues for labs for students.";
        File file = Paths.get("Images/sillyCatForStart.jpg").toFile();
        InputFile inputFile = new InputFile(file);

        SendPhoto sendPhoto = SendPhoto
                .builder()
                .chatId(chatId)
                .photo(inputFile)
                .caption(textMessage)
                .build();

        try{
            telegramClient.execute(sendPhoto);
            System.out.println("EVERYTHING WENT FUCKING EXCELLENT");
        } catch (TelegramApiException e) {
            System.out.println("Something went wrong");
            throw new RuntimeException(e);
        }
    }
}
