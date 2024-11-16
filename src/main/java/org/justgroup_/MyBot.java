package org.justgroup_;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    ArrayList<Long> admins = new ArrayList<>(Arrays.asList(1219763978L, 5115035519L));
    private ArrayList<QueueForLab> queues = new ArrayList<>();
    private HashMap<Long, Integer> usersAndId = new HashMap(); //first - idOfChat; second - idOfMessage
    private ArrayList<Long> adminsChatIDs = new ArrayList<>(
            Arrays.asList(
                    1219763978L,
                    5115035519L
            )
    );

    private ArrayList<InlineKeyboardRow> basicRows = new ArrayList<>(
            Arrays.asList(
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("List of current Queues")
                                    .callbackData("show_queues")
                                    .build()
                    ),
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("Admin panel :3")
                                    .callbackData("admin_panel")
                                    .build()
                    )
            )
    );

    private ArrayList<InlineKeyboardRow> adminRows = new ArrayList<>(
            Arrays.asList(
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("Add Queue")
                                    .callbackData("add_queue")
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text("Change queue")
                                    .callbackData("change_queue")
                                    .build()
                    ),
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("Delete Queue")
                                    .callbackData("delete_queue")
                                    .build()
                    ),
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("UwU")
                                    .callbackData("uwu")
                                    .build()
                    )
            )
    );

    private ArrayList<InlineKeyboardRow> listOfQueuesRows = new ArrayList<>();

    public MyBot(String botToken) {
        telegramClient = new OkHttpTelegramClient(botToken);
    }

    @Override
    public void consume(Update update) {
        if(update.hasCallbackQuery()){
            inlineButtonsHandler(update.getCallbackQuery());
        } else {
            commandsHandler(update.getMessage().getText(), update.getMessage().getChatId(), update.getMessage().getMessageId());
        }
        queues.add(new QueueForLab("Something", new Time(12, 0, 0), new Time(12, 0, 0), 123));
    }

    public void commandsHandler(String command, long chatId, int messageId) {
        DeleteMessage deleteMessage = DeleteMessage
                .builder()
                .chatId(chatId) // Chat ID as String
                .messageId(messageId)      // ID of the message to delete
                .build();

        try{
            telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

        switch (command){
            case "/start": startCommand(chatId); break;
            case "/listOfQueues": printListOfQueue(chatId); break;
        }
    }

    public void inlineButtonsHandler(CallbackQuery call) {
        System.out.println("I GOT FCKING QUERRY");
        String call_data = call.getData();
        int messageId = call.getMessage().getMessageId();
        long chat_id = call.getMessage().getChatId();

        if (call_data.equals("admin_panel")) {
            if (admins.contains(chat_id)) {
                adminInlineKeybord(chat_id, messageId);
            }
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
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(basicRows)
                        .build())
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

    int index = 0;
    private void printListOfQueue(long chatId){
        StringBuilder textMessage = new StringBuilder();
        textMessage.append(index);
        index++;
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
            System.out.println("Something went wrong");
            messageId = usersAndId.get(chatId);
        }

        fillInlineKeyboardWithQueues();
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(textMessage.toString())
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(listOfQueuesRows)
                        .build())
                .build();

        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
            handleNoMessageError(chatId);
            System.out.println("Something went wrong");
            messageId = usersAndId.get(chatId);

            try {
                telegramClient.execute(editMessageText);
            } catch (TelegramApiException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    private void adminInlineKeybord(long chatId, int messageId){

        EditMessageText editMessageText = EditMessageText.builder()
                .text("yo")
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(adminRows)
                        .build()
                )
                .build();

        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
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

    private void fillInlineKeyboardWithQueues(){
        for(QueueForLab queue : queues){
            listOfQueuesRows.add(new InlineKeyboardRow(
                    InlineKeyboardButton.builder()
                            .text(queue.getClassName())
                            .callbackData(Long.toString(queue.getIdInQueue()))
                            .build()
            ));
        }
    }
}
