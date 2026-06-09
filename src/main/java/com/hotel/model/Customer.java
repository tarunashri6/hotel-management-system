package com.hotel.model;

import javafx.beans.property.*;

public class Customer {

    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final ObjectProperty<Room> room = new SimpleObjectProperty<>();
    private final StringProperty checkInDate = new SimpleStringProperty();
    private final IntegerProperty days = new SimpleIntegerProperty();
    private final StringProperty status = new SimpleStringProperty("Active");

    public Customer(String name, String phone, String email, Room room,
            String checkInDate, int days) {
        this.name.set(name);
        this.phone.set(phone);
        this.email.set(email);
        this.room.set(room);
        this.checkInDate.set(checkInDate);
        this.days.set(days);
    }

    public String getName() {
        return name.get();
    }

    public String getCheckInDate() {
        return checkInDate.get();
    }

    public StringProperty checkInDateProperty() {
        return checkInDate;
    }

    public int getDays() {
        return days.get();
    }

    public IntegerProperty daysProperty() {
        return days;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getPhone() {
        return phone.get();
    }

    @Override
    public String toString() {
        return getName();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public Room getRoom() {
        return room.get();
    }

    public ObjectProperty<Room> roomProperty() {
        return room;
    }

    public String getStatus() {
        return status.get();
    }

    public void setStatus(String s) {
        status.set(s);
    }

    public StringProperty statusProperty() {
        return status;
    }
}