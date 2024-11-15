package org.justgroup_;

public class Student {
    private String name;
    private long telegramID;

    public Student(String name, long telegramID) {
        this.name = name;
        this.telegramID = telegramID;
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