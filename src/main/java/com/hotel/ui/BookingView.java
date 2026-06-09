package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.data.DataStore;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import java.time.LocalDate;

public class BookingView {

    private VBox root;

    public BookingView() {

        root = new VBox();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #0f172a;");

        BorderPane layout = new BorderPane();

        ComboBox<Customer> customerBox = new ComboBox<>();
        ComboBox<Room> roomBox = new ComboBox<>();
        DatePicker checkInPicker = new DatePicker();
        TextField nightsField = new TextField();
        TextField checkoutField = new TextField();
        Label previewLabel = new Label("Total: ₹0");
        Button confirmBtn = new Button("Confirm Booking");

        customerBox.setPromptText("Select Customer");
        customerBox.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        customerBox.setMaxWidth(Double.MAX_VALUE);

        checkInPicker.setPromptText("Check-in Date");
        checkInPicker.setStyle("-fx-control-inner-background: #334155; -fx-background-radius: 8;");
        checkInPicker.setMaxWidth(Double.MAX_VALUE);

        nightsField.setPromptText("e.g. 3");
        nightsField.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 8;");

        checkoutField.setPromptText("Auto-calculated");
        checkoutField.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 8;");
        checkoutField.setEditable(false);

        VBox formCard = new VBox(15);
        formCard.setPrefWidth(450);
        formCard.setMinWidth(300);
        formCard.setPadding(new Insets(20));
        formCard.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label title = new Label("New Booking");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        TextField contactField = new TextField();
        contactField.setPromptText("Contact Number");
        contactField.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 8; -fx-padding: 8;");

        VBox customerDetails = new VBox(10);
        customerDetails.getChildren().addAll(
                createLabel("Customer Name"), customerBox,
                createLabel("Contact Number"), contactField);

        VBox roomSelectionBox = new VBox(10);
        roomSelectionBox.getChildren().add(createLabel("Choose Available Room"));

        FlowPane roomsGrid = new FlowPane(10, 10);
        roomsGrid.setAlignment(Pos.TOP_LEFT);
        roomsGrid.setPrefWrapLength(250);
        roomsGrid.setPadding(new Insets(10));

        Runnable renderRooms = () -> {
            roomsGrid.getChildren().clear();
            if (roomBox.getItems() == null)
                return;
            for (Room r : roomBox.getItems()) {
                VBox rCard = new VBox(5);
                rCard.setPrefWidth(125);
                rCard.setPadding(new Insets(10));

                boolean isAvail = r.getStatus().equals("Available");
                String borderCol = isAvail ? "#22c55e" : "#ef4444";
                rCard.setStyle("-fx-background-color: #334155; -fx-border-color: " + borderCol
                        + "; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");

                Label rNum = new Label("Room " + r.getRoomNumber());
                rNum.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

                Label rType = new Label(r.getType());
                rType.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

                Label rPrice = new Label("₹" + r.getPrice() + " / day");
                rPrice.setStyle("-fx-text-fill: #facc15; -fx-font-weight: bold;");

                Label rFloor = new Label("Floor: " + r.getFloor());
                rFloor.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");

                Label badge = new Label(isAvail ? "Free" : "Taken");
                badge.setStyle("-fx-background-color: " + (isAvail ? "#14532d" : "#7f1d1d") + "; -fx-text-fill: "
                        + (isAvail ? "#4ade80" : "#f87171")
                        + "; -fx-padding: 2 6; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold;");

                rCard.getChildren().addAll(rNum, rType, rPrice, rFloor, badge);

                rCard.setOnMouseClicked(e -> {

                    for (javafx.scene.Node n : roomsGrid.getChildren()) {
                        n.setStyle(n.getStyle().replace("-fx-background-color: #475569;",
                                "-fx-background-color: #334155;"));
                    }
                    rCard.setStyle(rCard.getStyle().replace("-fx-background-color: #334155;",
                            "-fx-background-color: #475569;"));
                    roomBox.setValue(r);
                });
                roomsGrid.getChildren().add(rCard);
            }
        };

        roomBox.itemsProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                renderRooms.run();
                if (newV instanceof ObservableList) {
                    ((ObservableList<Room>) newV).addListener((ListChangeListener<Room>) chg -> renderRooms.run());
                }
            }
        });

        ScrollPane roomsScroll = new ScrollPane(roomsGrid);
        roomsScroll.setPrefHeight(120);
        roomsScroll.setMinHeight(100);
        roomsScroll.setFitToWidth(true);
        roomsScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        roomsScroll.setStyle(
                "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");
        VBox.setVgrow(roomsScroll, Priority.ALWAYS);
        VBox.setVgrow(roomSelectionBox, Priority.ALWAYS);
        roomSelectionBox.getChildren().add(roomsScroll);

        checkInPicker.setPrefWidth(120);
        nightsField.setPrefWidth(80);
        checkoutField.setPrefWidth(140);

        HBox stayDetails = new HBox(10);
        VBox ciBox = new VBox(5, createLabel("Check-in Date"), checkInPicker);
        VBox niBox = new VBox(5, createLabel("Nights"), nightsField);
        VBox coBox = new VBox(5, createLabel("Check-out Date"), checkoutField);
        stayDetails.getChildren().addAll(ciBox, niBox, coBox);

        VBox errorBox = new VBox();
        errorBox.setVisible(false);
        errorBox.setManaged(false);
        errorBox.setStyle(
                "-fx-background-color: #7f1d1d; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #ef4444; -fx-border-radius: 8;");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #fca5a5; -fx-font-weight: bold;");
        errorBox.getChildren().add(errorLabel);

        VBox successBox = new VBox();
        successBox.setVisible(false);
        successBox.setManaged(false);
        successBox.setStyle(
                "-fx-background-color: #14532d; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #22c55e; -fx-border-radius: 8;");
        Label successLabel = new Label();
        successLabel.setStyle("-fx-text-fill: #86efac; -fx-font-weight: bold;");
        successBox.getChildren().add(successLabel);

        VBox summaryCard = new VBox(8);
        summaryCard.setPadding(new Insets(15));
        VBox.setMargin(summaryCard, new Insets(15, 0, 0, 0));
        summaryCard.setStyle(
                "-fx-background-color: #0f172a; -fx-background-radius: 10; -fx-border-color: #334155; -fx-border-radius: 10;");
        Label sumTitle = new Label("Booking Summary");
        sumTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label sumRoom = createLabel("Room: --");
        Label sumRate = createLabel("Rate per night: --");
        Label sumDur = createLabel("Duration: --");
        Label sumDates = createLabel("Dates: --");
        HBox sumOut = new HBox(createLabel("Total Amount:"), new Region() {
            {
                HBox.setHgrow(this, Priority.ALWAYS);
            }
        });
        Label sumVal = new Label("₹0.00");
        sumVal.setStyle("-fx-text-fill: #facc15; -fx-font-weight: bold; -fx-font-size: 18px;");
        sumOut.getChildren().add(sumVal);

        summaryCard.getChildren().addAll(sumTitle, sumRoom, sumRate, sumDur, sumDates, new Separator(), sumOut);

        previewLabel.textProperty().addListener((obs, old, val) -> {
            if (val.startsWith("Total:")) {
                errorBox.setVisible(false);
                errorBox.setManaged(false);
                successBox.setVisible(false);
                successBox.setManaged(false);
            } else if (val.contains("Confirmed")) {
                successBox.setVisible(true);
                successBox.setManaged(true);
                successLabel.setText("Booking confirmed! Room "
                        + (roomBox.getValue() != null ? roomBox.getValue().getRoomNumber() : "") + " is now reserved.");
                errorBox.setVisible(false);
                errorBox.setManaged(false);
            } else {
                errorBox.setVisible(true);
                errorBox.setManaged(true);
                errorLabel.setText(val);
                successBox.setVisible(false);
                successBox.setManaged(false);
            }
        });

        confirmBtn.setMaxWidth(Double.MAX_VALUE);
        confirmBtn.setStyle(
                "-fx-background-color: #facc15; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-padding: 12; -fx-background-radius: 8; -fx-cursor: hand; -fx-font-size: 14px;");

        formCard.getChildren().addAll(
                title, customerDetails, roomSelectionBox, stayDetails, errorBox, successBox, summaryCard, confirmBtn);
        layout.setLeft(formCard);

        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(0));
        BorderPane.setMargin(rightPanel, new Insets(0, 0, 0, 20));
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        HBox rHeader = new HBox(20);
        rHeader.setAlignment(Pos.CENTER_LEFT);
        Label rTitle = new Label("All Bookings");
        rTitle.setStyle("-fx-text-fill: white; -fx-font-size: 26px; -fx-font-weight: bold;");

        TextField searchBar = new TextField();
        searchBar.setPromptText("Search bookings by guest name or room...");
        searchBar.setPrefWidth(320);
        searchBar.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-background-radius: 20; -fx-padding: 8 15;");

        Region rSpc = new Region();
        HBox.setHgrow(rSpc, Priority.ALWAYS);
        rHeader.getChildren().addAll(rTitle, rSpc, searchBar);

        TableView<Customer> table = new TableView<>();
        table.setStyle(
                "-fx-background-color: #1e293b; -fx-base: #1e293b; -fx-control-inner-background: #1e293b; -fx-text-fill: white; -fx-background-radius: 10;");
        table.setPrefHeight(250);

        TableColumn<Customer, String> colGuest = new TableColumn<>("Guest");
        colGuest.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        colGuest.setPrefWidth(150);

        TableColumn<Customer, String> colRoom = new TableColumn<>("Room");
        colRoom.setCellValueFactory(
                data -> new SimpleStringProperty(
                        data.getValue().getRoom() != null ? String.valueOf(data.getValue().getRoom().getRoomNumber())
                                : "N/A"));
        colRoom.setPrefWidth(80);

        TableColumn<Customer, String> colType = new TableColumn<>("Type");
        colType.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getRoom() != null ? data.getValue().getRoom().getType() : "N/A"));
        colType.setCellFactory(column -> new TableCell<Customer, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label l = new Label(item);
                    l.setStyle(
                            "-fx-background-color: #3b82f630; -fx-text-fill: #60a5fa; -fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                    setGraphic(l);
                }
            }
        });

        TableColumn<Customer, String> colCheckIn = new TableColumn<>("Check-in");
        colCheckIn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCheckInDate()));
        colCheckIn.setPrefWidth(120);

        TableColumn<Customer, String> colNights = new TableColumn<>("Nights");
        colNights.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getDays())));

        table.getColumns().addAll(colGuest, colRoom, colType, colCheckIn, colNights);

        FilteredList<Customer> filteredCustomers = new FilteredList<>(DataStore.getCustomers(), p -> true);
        searchBar.textProperty().addListener((obs, oldV, newV) -> {
            filteredCustomers.setPredicate(c -> {
                if (newV == null || newV.isEmpty())
                    return true;
                return c.getName().toLowerCase().contains(newV.toLowerCase())
                        || (c.getRoom() != null && String.valueOf(c.getRoom().getRoomNumber()).contains(newV));
            });
        });
        table.setItems(filteredCustomers);

        Label activeTitle = new Label("Active Booking Cards");
        activeTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        FlowPane activeGrid = new FlowPane(15, 15);
        activeGrid.setAlignment(Pos.TOP_LEFT);

        Runnable renderActive = () -> {
            activeGrid.getChildren().clear();
            for (Customer c : filteredCustomers) {
                if (c.getRoom() == null || !c.getRoom().getStatus().equals("Occupied"))
                    continue;

                VBox card = new VBox(10);
                card.setPrefWidth(220);
                card.setPadding(new Insets(15));
                card.setStyle(
                        "-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 3);");

                HBox tRow = new HBox(10);
                tRow.setAlignment(Pos.CENTER_LEFT);
                Circle circ = new Circle(15, Color.web("#facc15"));
                String inits = c.getName().length() > 0 ? c.getName().substring(0, 1).toUpperCase() : "G";
                StackPane av = new StackPane(circ, new Text(inits) {
                    {
                        setFill(Color.web("#0f172a"));
                        setFont(Font.font("System", FontWeight.BOLD, 12));
                    }
                });
                Label nLbl = new Label(c.getName());
                nLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                tRow.getChildren().addAll(av, nLbl);

                Label rLbl = createLabel("Room " + c.getRoom().getRoomNumber() + " - " + c.getRoom().getType());

                String dates = c.getCheckInDate();
                try {
                    LocalDate cid = LocalDate.parse(c.getCheckInDate());
                    dates = cid.getDayOfMonth() + " " + cid.getMonth().toString().substring(0, 3) + " - "
                            + cid.plusDays(c.getDays()).getDayOfMonth() + " "
                            + cid.plusDays(c.getDays()).getMonth().toString().substring(0, 3);
                } catch (Exception ignored) {
                }
                Label dLbl = createLabel("📅 " + dates);

                double tot = c.getDays() * c.getRoom().getPrice();
                Label totLbl = new Label(String.format("₹%.2f", tot));
                totLbl.setStyle("-fx-text-fill: #facc15; -fx-font-weight: bold; -fx-font-size: 14px;");

                card.getChildren().addAll(tRow, rLbl, dLbl, new Separator(), totLbl);
                activeGrid.getChildren().add(card);
            }
        };

        filteredCustomers.addListener((ListChangeListener<Customer>) change -> renderActive.run());
        renderActive.run();

        ScrollPane activeScroll = new ScrollPane(activeGrid);
        activeScroll.setFitToWidth(true);
        activeScroll.setStyle(
                "-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        rightPanel.getChildren().addAll(rHeader, table, activeTitle, activeScroll);
        layout.setCenter(rightPanel);

        customerBox.setItems(DataStore.getCustomers());
        customerBox.setOnAction(e -> {
            Customer c = customerBox.getValue();
            if (c == null)
                return;

            checkInPicker.setValue(java.time.LocalDate.parse(c.getCheckInDate()));
            nightsField.setText(String.valueOf(c.getDays()));

            roomBox.setValue(c.getRoom());
        });

        ObservableList<Room> initialRooms = DataStore.getRooms().filtered(
                r -> r.getStatus().equals("Available"));
        roomBox.setItems(initialRooms);

        renderRooms.run();
        initialRooms.addListener((ListChangeListener<Room>) chg -> renderRooms.run());

        Runnable updateSummary = () -> {
            Room selected = roomBox.getValue();
            LocalDate ci = checkInPicker.getValue();
            String nightsStr = nightsField.getText();

            boolean allValid = false;

            if (ci != null && nightsStr != null && !nightsStr.isEmpty()) {
                try {
                    int nights = Integer.parseInt(nightsStr);
                    LocalDate co = ci.plusDays(nights);
                    checkoutField.setText(co.toString());

                    if (selected != null) {
                        double total = nights * selected.getPrice();
                        previewLabel.setText("Total: ₹" + total);

                        sumRoom.setText("Room: " + selected.getRoomNumber() + " (" + selected.getType() + ")");
                        sumRate.setText("Rate per night: ₹" + selected.getPrice());
                        sumDur.setText("Duration: " + nights + " nights");
                        sumDates.setText("Dates: " + ci + "   →   " + co);
                        sumVal.setText("₹" + total);

                        allValid = true;
                    }
                } catch (Exception ignored) {
                }
            } else {
                checkoutField.setText("");
            }

            if (!allValid) {
                previewLabel.setText("Total: ₹0");

                sumRoom.setText("Room: --");
                sumRate.setText("Rate per night: --");
                sumDur.setText("Duration: --");
                sumDates.setText("Dates: --");
                sumVal.setText("--");
            }
        };

        nightsField.textProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        checkInPicker.valueProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());
        roomBox.valueProperty().addListener((obs, oldVal, newVal) -> updateSummary.run());

        roomBox.setOnAction(e -> updateSummary.run());

        confirmBtn.setOnAction(e -> {
            Customer customer = customerBox.getValue();
            Room room = roomBox.getValue();

            if (customer == null || room == null) {
                previewLabel.setText("Select customer & room!");
                return;
            }
            if (checkInPicker.getValue() == null || nightsField.getText().isEmpty()) {
                previewLabel.setText("Check-in date or nights missing!");
                return;
            }

            if (!room.getStatus().equals("Available")) {
                previewLabel.setText("Room already occupied!");
                return;
            }

            room.setStatus("Occupied");

            try {
                int nights = Integer.parseInt(nightsField.getText());
                String ciStr = checkInPicker.getValue().toString();

                customer.roomProperty().set(room);
                customer.checkInDateProperty().set(ciStr);
                customer.daysProperty().set(nights);

                int idx = DataStore.getCustomers().indexOf(customer);
                if (idx != -1) {
                    DataStore.getCustomers().set(idx, customer);
                } else {
                    DataStore.getCustomers().add(customer);
                }
            } catch (Exception ignored) {
            }

            previewLabel.setText("Booking Confirmed ✔");

            ObservableList<Room> updatedRooms = DataStore.getRooms().filtered(
                    r -> r.getStatus().equals("Available"));
            roomBox.setItems(updatedRooms);
            updatedRooms.addListener((ListChangeListener<Room>) chg -> renderRooms.run());
            renderRooms.run();
        });

        root.getChildren().add(layout);
    }

    private Label createLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        return l;
    }

    public VBox getView() {
        return root;
    }
}