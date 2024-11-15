package org.justgroup_;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    ArrayList<Long> admins = new ArrayList<>(Arrays.asList(1219763978L, 5115035519L));
    private ArrayList<QueueForLab> queues = new ArrayList<>();
    private HashMap<Long, Integer> usersAndId = new HashMap(); //first - idOfChat; second - idOfMessage

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
            //case "/listOfQueues":
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

        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text("I'm very pleased you use this shit :>")
                .build();

        try{
            telegramClient.execute(sendPhoto);
            Message sentMessage = telegramClient.execute(sendMessage);
            usersAndId.put(chatId, sentMessage.getMessageId());

            System.out.println("EVERYTHING WENT FUCKING EXCELLENT");
        } catch (TelegramApiException e) {
            System.out.println("Something went wrong");
            throw new RuntimeException(e);
        }
    }

    private void printListOfQueue(long chatId){
        StringBuilder textMessage = new StringBuilder();
        int indexOfQueue = 1;
        for(QueueForLab queue : queues){
            textMessage.append(indexOfQueue + ". " + queue.getClassName() + queue.getListAsList() + "\n\n");
        }

        Integer messageId;
        if(usersAndId.get(chatId) != null){
            messageId = usersAndId.get(chatId);
        }
        else{
            handleNoMessageError(chatId);
            messageId = usersAndId.get(chatId);
        }

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text("This is the edited message.")
                .build();

        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
            System.out.println();
            throw new RuntimeException(e);
        }
    }

    public void handleNoMessageError(long chatId){
        SendMessage sendMessage = SendMessage
                .builder()
                .chatId(chatId)
                .text(".")
                .build();

        try {
            Message message = telegramClient.execute(sendMessage);
            usersAndId.put(chatId, message.getMessageId());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
