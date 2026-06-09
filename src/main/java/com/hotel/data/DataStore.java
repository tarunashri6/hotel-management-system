package com.hotel.data;

import com.hotel.model.Customer;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataStore {

    private static final String ROOMS_FILE = "rooms.txt";
    private static final String CUSTOMERS_FILE = "customers.txt";
    private static boolean isLoading = false;

    private static final ObservableList<Room> rooms = FXCollections.observableArrayList(
            r -> new javafx.beans.Observable[]{r.statusProperty(), r.priceProperty(), r.typeProperty()}
    );

    private static final ObservableList<Customer> customers = FXCollections.observableArrayList(
            c -> new javafx.beans.Observable[]{c.statusProperty(), c.roomProperty(), c.daysProperty()}
    );

    static {
        rooms.addListener((ListChangeListener<Room>) c -> {
            if (!isLoading) saveRoomsToFile();
        });
        customers.addListener((ListChangeListener<Customer>) c -> {
            if (!isLoading) saveCustomersToFile();
        });
    }

    public static void loadRoomsFromFile() {
        Path path = Paths.get(ROOMS_FILE);
        if (!Files.exists(path)) return;

        isLoading = true;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    try {
                        int roomNumber = Integer.parseInt(parts[0].trim());
                        String type = parts[1].trim();
                        double price = Double.parseDouble(parts[2].trim());
                        String status = parts[3].trim();
                        int floor = Integer.parseInt(parts[4].trim());
                        rooms.add(new Room(roomNumber, type, price, status, floor));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {} finally {
            isLoading = false;
        }
    }

    public static void saveRoomsToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(ROOMS_FILE))) {
            for (Room r : rooms) {
                String type = r.getType() != null ? r.getType().replace(",", " ") : "";
                String status = r.getStatus() != null ? r.getStatus().replace(",", " ") : "";
                bw.write(r.getRoomNumber() + "," + type + "," + r.getPrice() + "," + status + "," + r.getFloor() + "\n");
            }
        } catch (IOException ignored) {}
    }

    public static void loadCustomersFromFile() {
        Path path = Paths.get(CUSTOMERS_FILE);
        if (!Files.exists(path)) return;

        isLoading = true;
        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    try {
                        String name = parts[0].trim();
                        String phone = parts[1].trim();
                        String email = parts[2].trim();
                        int roomNumber = Integer.parseInt(parts[3].trim());
                        String checkInDate = parts[4].trim();
                        int days = Integer.parseInt(parts[5].trim());
                        String status = parts[6].trim();

                        Room matchedRoom = null;
                        for (Room r : rooms) {
                            if (r.getRoomNumber() == roomNumber) {
                                matchedRoom = r;
                                break;
                            }
                        }

                        Customer c = new Customer(name, phone, email, matchedRoom, checkInDate, days);
                        c.setStatus(status);
                        customers.add(c);
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException ignored) {} finally {
            isLoading = false;
        }
    }

    public static void saveCustomersToFile() {
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(CUSTOMERS_FILE))) {
            for (Customer c : customers) {
                int roomNo = (c.getRoom() != null) ? c.getRoom().getRoomNumber() : -1;
                String name = c.getName() != null ? c.getName().replace(",", " ") : "";
                String phone = c.getPhone() != null ? c.getPhone().replace(",", " ") : "";
                String email = c.getEmail() != null ? c.getEmail().replace(",", " ") : "";
                String checkIn = c.getCheckInDate() != null ? c.getCheckInDate().replace(",", " ") : "";
                String status = c.getStatus() != null ? c.getStatus().replace(",", " ") : "Active";

                bw.write(name + "," + phone + "," + email + "," + roomNo + "," + checkIn + "," + c.getDays() + "," + status + "\n");
            }
        } catch (IOException ignored) {}
    }

    public static ObservableList<Room> getRooms() {
        return rooms;
    }

    public static void addRoom(Room room) {
        rooms.add(room);
    }

    public static ObservableList<Customer> getCustomers() {
        return customers;
    }

    private static Customer selectedCustomer;
    private static final javafx.beans.property.ObjectProperty<Customer> sharedSelectedCustomer = new javafx.beans.property.SimpleObjectProperty<>();

    public static void setSelectedCustomer(Customer c) {
        selectedCustomer = c;
        sharedSelectedCustomer.set(c);
    }

    public static Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public static javafx.beans.property.ObjectProperty<Customer> sharedSelectedCustomerProperty() {
        return sharedSelectedCustomer;
    }
}