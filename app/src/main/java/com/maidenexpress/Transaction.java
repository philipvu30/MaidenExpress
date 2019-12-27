package com.maidenexpress;

import java.io.Serializable;

public class Transaction implements Serializable {

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Transaction(long dateCreated, String reason, String amount) {
        this.dateCreated = dateCreated;
        this.reason = reason;
        this.amount = amount;
    }

    private long dateCreated;
    private String reason;
    private String amount;
    private int room;
    private int kitchen;
    private int livRoom;
    private int bathroom;
    private long scheduleTime;
    private long scheduleDate;
    private String services;

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public int getKitchen() {
        return kitchen;
    }

    public void setKitchen(int kitchen) {
        this.kitchen = kitchen;
    }

    public int getLivRoom() {
        return livRoom;
    }

    public void setLivRoom(int livRoom) {
        this.livRoom = livRoom;
    }

    public int getBathroom() {
        return bathroom;
    }

    public void setBathroom(int bathroom) {
        this.bathroom = bathroom;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public long getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(long scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public Transaction(long dateCreated, String reason, String amount, int room, int kitchen, int livRoom, int bathroom, long scheduleDate, String services) {
        this.dateCreated = dateCreated;
        this.reason = reason;
        this.amount = amount;
        this.room = room;
        this.kitchen = kitchen;
        this.livRoom = livRoom;
        this.bathroom = bathroom;
        this.scheduleDate = scheduleDate;
        this.services = services;
    }

    public Transaction(){}
}
