package org.justgroup_;

import java.util.HashMap;

public class Student {
    private String name;
    private long telegramID;
    private HashMap<String, Integer> queuesLol = new HashMap<>();

    public Student(String name, long telegramID) {
        this.name = name;
        this.telegramID = telegramID;
    }

    private HashMap<String, Integer> getQueuesLol() {
        return queuesLol;
    }

    public String getName() {
        return name;
    }
    public long getTelegramID() {
        return telegramID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTelegramID(long telegramID) {
        this.telegramID = telegramID;
    }
}
