package org.justgroup_;

import org.springframework.cache.annotation.CacheAnnotationParser;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Array;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class MyBot implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    ArrayList<Long> admins = new ArrayList<>(Arrays.asList(1219763978L, 5115035519L));
    private int indexOfQueue = 0;
    private boolean getAdminShit = false;

    private ArrayList<QueueForLab> queues = new ArrayList<>(
            Arrays.asList(
                    new QueueForLab("Something", new Time(12, 0, 0), DayOfWeek.MONDAY,  new Time(12, 0, 0), 123),
                    new QueueForLab("Something22", new Time(12, 0, 0), DayOfWeek.THURSDAY, new Time(12, 0, 0), 124),
                    new QueueForLab("Something33", new Time(12, 0, 0), DayOfWeek.SUNDAY, new Time(12, 0, 0), 125)
            )
    );
    private HashMap<Long, Integer> usersAndId = new HashMap(); //first - idOfChat; second - idOfMessage
    private long idForLab = 0;
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

    private ArrayList<InlineKeyboardRow> printRows = new ArrayList<>(
            Arrays.asList(
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("<-")
                                    .callbackData("show_left")
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text("Join queue")
                                    .callbackData("join_queue")
                                    .build(),
                            InlineKeyboardButton.builder()
                                    .text("->")
                                    .callbackData("show_right")
                                    .build()
                    ),
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("Leave queue")
                                    .callbackData("leave_queue")
                                    .build()
                    ),
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("Back")
                                    .callbackData("back_to_basic")
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
                                    .text("Delete Queue")
                                    .callbackData("delete_queue")
                                    .build()
                    ),
                    new InlineKeyboardRow(
                            InlineKeyboardButton.builder()
                                    .text("Back")
                                    .callbackData("back_to_basic")
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
    }

    public void commandsHandler(String command, long chatId, int messageId) {
        if (getAdminShit) {
            SendMessage popup = SendMessage.builder()
                    .text("Wrong format of someting or everything)))")
                    .chatId(chatId)
                    .build();

            String[] parts = command.split("\n");
            if(parts.length == 4) {
                try{
                    queues.add(new QueueForLab(parts[0], Time.valueOf(parts[1]), DayOfWeek.valueOf(parts[2].toUpperCase()), Time.valueOf(parts[3]), idForLab++));
                } catch (Exception e){
                    try {
                        telegramClient.execute(popup);
                    } catch (TelegramApiException e1) {
                        e1.printStackTrace();
                    }
                }
                System.out.println(queues.getLast());
            } else {
                try {
                    telegramClient.execute(popup);
                } catch (TelegramApiException e) {
                    System.out.println("Something went wrong");
                    e.printStackTrace();
                }
            }
            getAdminShit = false;
            adminInlineKeybord(chatId, usersAndId.get(chatId));
        }

        DeleteMessage deleteMessage = DeleteMessage
                .builder()
                .chatId(chatId) // Chat ID as String
                .messageId(messageId)      // ID of the message to delete
                .build();

        try {
            telegramClient.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        switch (command) {
            case "/start":
                startCommand(chatId);
                break;
            case "/listOfQueues":
                printListOfQueue(chatId);
                break;
        }
    }

    private void joinQueue(CallbackQuery callbackQuery) {
        String name = callbackQuery.getFrom().getUserName().toString();
        long chat_id = callbackQuery.getMessage().getChatId();
        boolean isOk = true;
        for(Student student : queues.get(indexOfQueue).getListOfStudent()){
            if(student.getName().equals(name)) isOk = false;
            System.out.println(student);
            System.out.println(student.equals(new Student(name, chat_id)));
        }

        if(isOk) {
            queues.get(indexOfQueue).addStudentInQueue(new Student(name, chat_id));
        } else {
            int yourIndex = 0;
            for(int i = 0; i < queues.get(indexOfQueue).getListOfStudent().size(); i++) {
                if(queues.get(indexOfQueue).getListOfStudent().get(i).getName().equals(name)) {
                    yourIndex = i+1;
                }
            }

            AnswerCallbackQuery popup = AnswerCallbackQuery.builder()
                    .text("You already joined queue\nYour position: " + yourIndex + ".")
                    .callbackQueryId(callbackQuery.getId())
                    .showAlert(true)
                    .build();
            try {
                telegramClient.execute(popup);
            } catch (TelegramApiException e) {
                System.out.println("Something went wrong");
                e.printStackTrace();
            }
        }
    }

    private void leaveQueue(CallbackQuery callbackQuery) {
        long chat_id = callbackQuery.getMessage().getChatId();
        for(int i = 0; i < queues.get(indexOfQueue).getListOfStudent().size(); i++) {
            if(queues.get(indexOfQueue).getListOfStudent().get(i).getTelegramID() == chat_id) { // )))))))))))))
                queues.get(indexOfQueue).getListOfStudent().remove(i);
                break;
            }
        }
    }

    public void inlineButtonsHandler(CallbackQuery call) {
        System.out.println("I GOT FCKING QUERRY " + call.getMessage().getChatId() + " " + call.getMessage().getMessageId());
        String call_data = call.getData();
        int messageId = call.getMessage().getMessageId();
        long chat_id = call.getMessage().getChatId();
        EditMessageText editMessageText;
        if(usersAndId.get(chat_id) == null) {
            handleNoMessageError(chat_id);
            DeleteMessage deleteOldMessage = DeleteMessage.builder()
                    .messageId(messageId)
                    .chatId(chat_id)
                    .build();
            try {
                telegramClient.execute(deleteOldMessage);
            } catch (TelegramApiException e) {
                System.out.println("Something went wrong");
            }
        }

        try {
            switch (call_data) {
                case "show_queues":
                    printListOfQueue(chat_id);
                    break;
                case "admin_panel":
                    if (admins.contains(chat_id)) {
                        adminInlineKeybord(chat_id, messageId);
                    } else {
                        AnswerCallbackQuery popup = AnswerCallbackQuery.builder()
                                .text("Naaah)))))")
                                .callbackQueryId(call.getId())
                                .showAlert(true)
                                .build();
                        try {
                            telegramClient.execute(popup);
                        } catch (TelegramApiException e) {
                            System.out.println("Something went wrong");
                            e.printStackTrace();
                        }
                    }
                    ;
                    break;
                case "back_to_basic":
                    changeRowsToBasic(call);
                    break;
                case "join_queue":
                    joinQueue(call);
                    printListOfQueue(chat_id);
                    break;
                case "leave_queue":
                    leaveQueue(call);
                    printListOfQueue(chat_id);
                    break;
                case "show_right":
                    indexOfQueue++;
                    printListOfQueue(chat_id);
                    break;
                case "show_left":
                    indexOfQueue--;
                    printListOfQueue(chat_id);
                    break;
                case "add_queue":
                    addQueue(call);
                    break;
                case "delete_queue":
                    fillInlineKeyboardWithQueues();
                    idForLab = 0;
                    editMessageText = EditMessageText.builder()
                            .text("Choose queue to delete")
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboard(listOfQueuesRows)
                                    .build()
                            )
                            .chatId(chat_id)
                            .messageId(messageId)
                            .build();

                    try {
                        telegramClient.execute(editMessageText);
                    } catch (TelegramApiException e) {
                        System.out.println("Something went wrong");
                    }
                    break;
                default:
                    deleteQueue(call);
                    fillInlineKeyboardWithQueues();
                    editMessageText = EditMessageText.builder()
                            .text("Choose queue to delete")
                            .replyMarkup(InlineKeyboardMarkup.builder()
                                    .keyboard(listOfQueuesRows)
                                    .build()
                            )
                            .chatId(chat_id)
                            .messageId(messageId)
                            .build();

                    try {
                        telegramClient.execute(editMessageText);
                    } catch (TelegramApiException e) {
                        System.out.println("Something went wrong");
                    }
                    break;
            }
        } catch (Exception e) {
            handleNoMessageError(chat_id);
        }
    }

    private void deleteQueue(CallbackQuery callbackQuery) {
        for(int i = 0; i < queues.size(); i++) {
            if(queues.get(i).getIdInQueue() == (Long.parseLong(callbackQuery.getData()))) {
                queues.remove(i);
                break;
            }
        }
    }

    private void addQueue(CallbackQuery callbackQuery) {
        getAdminShit = true;
        long chat_id = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();

        EditMessageText editMessageText = EditMessageText.builder()
                .text("Format of new Queue: \nName of class\nTime for queue to get cleared\nDay Of week\nTime of class start")
                .chatId(chat_id)
                .messageId(messageId)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(
                                InlineKeyboardButton.builder()
                                        .text("Back")
                                        .callbackData("admin_panel")
                                        .build()
                        ))
                        .build()
                )
                .build();

        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
            System.out.println("Something went wrong");
        }
    }

    private void changeRowsToBasic(CallbackQuery callbackQuery) {
        long chat_id = callbackQuery.getMessage().getChatId();
        int messageId = callbackQuery.getMessage().getMessageId();
        StringBuilder stringBuilder = new StringBuilder("I'm very pleased you use my shit :>\n/////////////////////////////\n\nYour positions in queues:\n");

        int index = 1;
        for(QueueForLab queue: queues) {
            System.out.println("shit");
            for(int i = 0; i < queue.getListOfStudent().size(); i++) {
                Student student = queue.getListOfStudent().get(i);
                if(student.getTelegramID() == chat_id) {
                    System.out.println("shit found");
                    stringBuilder.append((index++) + ". " + student.getName() + " | Your position - " + (i + 1) + "\n");
                }
            }
        }

        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chat_id)
                .messageId(messageId)
                .text(stringBuilder.toString())
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboard(basicRows)
                                .build()
                )
                .build();

        try {
            telegramClient.execute(editMessageText);
        }
        catch (TelegramApiException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
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

    private void printListOfQueue(long chatId){
        Integer messageId;
        messageId = usersAndId.get(chatId);

        EditMessageText editMessageText;
        if(queues.isEmpty()) {
            System.out.println("shit");
            editMessageText = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                    .text("Nothing here...")
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(
                                            InlineKeyboardButton.builder()
                                                    .text("Back")
                                                    .callbackData("back_to_basic")
                                                    .build()
                                    )
                            )
                            .build()
                    )
                    .build();

            try {
                telegramClient.execute(editMessageText);
            } catch (TelegramApiException e) {
                System.out.println("Something went wrong");
            }
            return;
        }

        if(indexOfQueue < 0){
            indexOfQueue = queues.size() - 1;
        }
        else if(indexOfQueue == queues.size()){
            indexOfQueue = 0;
        }

        StringBuilder textMessage = new StringBuilder();
        textMessage.append(queues.get(indexOfQueue).getClassName() + "\nClass start time: " + queues.get(indexOfQueue).getTimeOfClass().toString() + " | " + queues.get(indexOfQueue).getDayOfWeek().toString() + "\nQueue:" + queues.get(indexOfQueue).getListAsList());

        editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId(messageId)
                .text(textMessage.toString())
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(printRows)
                        .build())
                .build();

        try {
            telegramClient.execute(editMessageText);
        } catch (TelegramApiException e) {
            System.out.println("Something went wrong");
        }
    }

    private void adminInlineKeybord(long chatId, int messageId){
        EditMessageText editMessageText = EditMessageText.builder()
                .text("Hoho, welcome :3")
                .chatId(chatId)
                .messageId(messageId)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(adminRows)
                        .build()
                )
                .build();
        getAdminShit = false;
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
                .text("I'm very pleased you use this shit :>")
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(basicRows)
                        .build())
                .build();

        try {
            Message message = telegramClient.execute(sendMessage);
            usersAndId.put(chatId, message.getMessageId());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillInlineKeyboardWithQueues(){
        listOfQueuesRows.clear();
        System.out.println(queues);
        for(QueueForLab queue : queues){
            listOfQueuesRows.add(new InlineKeyboardRow(
                    InlineKeyboardButton.builder()
                            .text(queue.getClassName())
                            .callbackData(Long.toString(queue.getIdInQueue()))
                            .build()
            ));
        }
        listOfQueuesRows.add(new InlineKeyboardRow(
                InlineKeyboardButton.builder()
                        .text("Back")
                        .callbackData("admin_panel")
                        .build())
        );
    }
}
