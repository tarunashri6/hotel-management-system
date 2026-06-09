package com.hotel.model;

import javafx.beans.property.*;

public class Room {

    private final IntegerProperty roomNumber;
    private final StringProperty type;
    private final DoubleProperty price;
    private final StringProperty status;
    private final IntegerProperty floor;

    public Room(int roomNumber, String type, double price, String status, int floor) {
        this.roomNumber = new SimpleIntegerProperty(roomNumber);
        this.type = new SimpleStringProperty(type);
        this.price = new SimpleDoubleProperty(price);
        this.status = new SimpleStringProperty(status);
        this.floor = new SimpleIntegerProperty(floor);
    }

    public int getRoomNumber() {
        return roomNumber.get();
    }

    public String getType() {
        return type.get();
    }

    public double getPrice() {
        return price.get();
    }

    public String getStatus() {
        return status.get();
    }

    public int getFloor() {
        return floor.get();
    }

    public void setStatus(String status) {
        this.status.set(status);
    }

    public IntegerProperty roomNumberProperty() {
        return roomNumber;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public DoubleProperty priceProperty() {
        return price;
    }

    public StringProperty statusProperty() {
        return status;
    }

    public IntegerProperty floorProperty() {
        return floor;
    }

    @Override
    public String toString() {
        return getRoomNumber() + " - " + getType();
    }
}