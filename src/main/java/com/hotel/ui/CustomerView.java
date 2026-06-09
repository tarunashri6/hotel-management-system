package com.hotel.ui;

import com.hotel.data.DataStore;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Predicate;

public class CustomerView {

    private final BorderPane root;
    private final FlowPane cardsContainer;
    private final TextField searchField;
    private final FilteredList<Customer> filteredCustomers;
    private Predicate<Customer> statusPredicate = c -> true;
    private Predicate<Customer> searchPredicate = c -> true;

    private final TextField nameField = new TextField();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final ComboBox<Room> roomBox = new ComboBox<>();
    private final DatePicker checkInPicker = new DatePicker();
    private final TextField daysField = new TextField();
    private final FilteredList<Room> availableRooms;

    public CustomerView() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setPadding(new Insets(20));

        availableRooms = new FilteredList<>(DataStore.getRooms(), r -> "Available".equals(r.getStatus()));
        setupRoomComboBox();

        root.setLeft(createRegistrationForm());

        filteredCustomers = new FilteredList<>(DataStore.getCustomers(), c -> true);
        cardsContainer = new FlowPane(20, 20);
        cardsContainer.setPrefWrapLength(1000);
        cardsContainer.setPadding(new Insets(10));
        cardsContainer.setAlignment(Pos.TOP_LEFT);

        searchField = new TextField();

        root.setCenter(createRightSection());

        DataStore.getCustomers().addListener((ListChangeListener.Change<? extends Customer> c) -> {
            refreshCards();
        });

        filteredCustomers.predicateProperty().addListener((obs, old, newVal) -> refreshCards());

        refreshCards();
    }

    private void setupRoomComboBox() {
        roomBox.setPromptText("Select Room");
        roomBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                setText(empty || room == null ? null
                        : room.getRoomNumber() + " - " + room.getType() + " ($" + room.getPrice() + ")");
            }
        });
        roomBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Room room, boolean empty) {
                super.updateItem(room, empty);
                setText(empty || room == null ? null : room.getRoomNumber() + " - " + room.getType());
            }
        });
        roomBox.setItems(availableRooms);
        roomBox.setMaxWidth(Double.MAX_VALUE);

        Runnable updateFilter = () -> {
            Room selected = roomBox.getValue();

            LocalDate newStart = checkInPicker.getValue();

            if (newStart == null || !daysField.getText().matches("\\d+")) {
                availableRooms.setPredicate(r -> "Available".equals(r.getStatus()));
            } else {
                int days = Integer.parseInt(daysField.getText());
                LocalDate newEnd = newStart.plusDays(days);

                availableRooms.setPredicate(room -> {
                    if (!"Available".equals(room.getStatus()))
                        return false;
                    for (Customer c : DataStore.getCustomers()) {
                        if (c.getRoom() != null && c.getRoom().getRoomNumber() == room.getRoomNumber()) {
                            try {
                                LocalDate existStart = LocalDate.parse(c.getCheckInDate());
                                LocalDate existEnd = existStart.plusDays(c.getDays());

                                if (newStart.isBefore(existEnd) && newEnd.isAfter(existStart)) {
                                    return false;
                                }
                            } catch (Exception ignored) {
                            }
                        }
                    }
                    return true;
                });
            }

            if (selected != null && availableRooms.contains(selected)) {
                roomBox.setValue(selected);
            } else {
                roomBox.setValue(null);
            }
        };

        checkInPicker.valueProperty().addListener((obs, old, newVal) -> updateFilter.run());
        daysField.textProperty().addListener((obs, old, newVal) -> updateFilter.run());
        DataStore.getCustomers().addListener((ListChangeListener.Change<? extends Customer> c) -> updateFilter.run());
        DataStore.getRooms().addListener((ListChangeListener.Change<? extends Room> c) -> updateFilter.run());
    }

    private VBox createRegistrationForm() {
        VBox form = new VBox(15);
        form.setPrefWidth(350);
        form.setPadding(new Insets(25));
        form.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label title = new Label("Register Guest");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        styleField(nameField, "Full Name");
        styleField(phoneField, "Contact Number");
        styleField(emailField, "Email Address");
        styleField(daysField, "Duration (Days)");

        checkInPicker.setPromptText("Check-in Date");
        checkInPicker.setMaxWidth(Double.MAX_VALUE);
        checkInPicker.setStyle("-fx-control-inner-background: #334155; -fx-text-fill: white;");

        Button registerBtn = new Button("Register Guest");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle(
                "-fx-background-color: #facc15; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

        Button clearBtn = new Button("Clear");
        clearBtn.setMaxWidth(Double.MAX_VALUE);
        clearBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-border-color: #475569; -fx-border-radius: 8; -fx-padding: 10; -fx-cursor: hand;");

        registerBtn.setOnAction(e -> handleRegistration());
        clearBtn.setOnAction(e -> clearForm());

        form.getChildren().addAll(
                title,
                createStyledLabel("Guest Name"), nameField,
                createStyledLabel("Contact Info"), phoneField, emailField,
                createStyledLabel("Room Assignment"), roomBox,
                createStyledLabel("Stay Details"), checkInPicker, daysField,
                new Region(), registerBtn, clearBtn);

        form.setOnMouseEntered(e -> form.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(250,204,21,0.2), 15, 0, 0, 5);"));
        form.setOnMouseExited(e -> form.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"));

        VBox formWrapper = new VBox(form);
        formWrapper.setPadding(new Insets(0, 20, 0, 0));
        return formWrapper;
    }

    private void styleField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 5; -fx-padding: 8;");
    }

    private Label createStyledLabel(String text) {
        Label lbl = new Label(text);
        lbl.setTextFill(Color.web("#94a3b8"));
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        VBox.setMargin(lbl, new Insets(5, 0, -10, 0));
        return lbl;
    }

    private VBox createRightSection() {
        VBox rightSection = new VBox(20);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Guest Records");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("System", FontWeight.BOLD, 26));

        searchField.setPromptText("Search by name or room...");
        searchField.setPrefWidth(250);
        searchField.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-background-radius: 20; -fx-padding: 8 15;");
        searchField.textProperty().addListener((obs, old, val) -> {
            searchPredicate = c -> val == null || val.isEmpty() ||
                    c.getName().toLowerCase().contains(val.toLowerCase()) ||
                    String.valueOf((c.getRoom() != null) ? c.getRoom().getRoomNumber() : "No Room").contains(val);
            updateCombinedPredicate();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox filters = new HBox(10);
        ToggleButton btnAll = createFilterButton("All Guests", true);
        ToggleButton btnActive = createFilterButton("Currently Staying", false);
        ToggleButton btnLeft = createFilterButton("Checked Out", false);

        ToggleGroup filterGroup = new ToggleGroup();
        btnAll.setToggleGroup(filterGroup);
        btnActive.setToggleGroup(filterGroup);
        btnLeft.setToggleGroup(filterGroup);

        filterGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == null) {
                btnAll.setSelected(true);
                return;
            }
            if (newVal == btnAll) {
                statusPredicate = c -> true;
            } else if (newVal == btnActive) {
                statusPredicate = c -> "Active".equals(c.getStatus());
            } else if (newVal == btnLeft) {
                statusPredicate = c -> "Checked Out".equals(c.getStatus());
            }
            updateCombinedPredicate();
        });

        filters.getChildren().addAll(btnAll, btnActive, btnLeft);
        header.getChildren().addAll(title, spacer, searchField, filters);

        ScrollPane scrollPane = new ScrollPane(cardsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #0f172a; -fx-border-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        rightSection.getChildren().addAll(header, scrollPane);
        return rightSection;
    }

    private ToggleButton createFilterButton(String text, boolean selected) {
        ToggleButton btn = new ToggleButton(text);
        btn.setSelected(selected);
        btn.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                btn.setStyle(
                        "-fx-background-color: #facc15; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
            } else {
                btn.setStyle(
                        "-fx-background-color: #1e293b; -fx-text-fill: #94a3b8; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
            }
        });

        if (selected)
            btn.setStyle(
                    "-fx-background-color: #facc15; -fx-text-fill: #0f172a; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 5 15; -fx-cursor: hand;");
        return btn;
    }

    private void updateCombinedPredicate() {
        filteredCustomers.setPredicate(c -> statusPredicate.test(c) && searchPredicate.test(c));
    }

    private void refreshCards() {
        cardsContainer.getChildren().clear();
        for (Customer c : filteredCustomers) {
            cardsContainer.getChildren().add(createCustomerCard(c));
        }
    }

    private VBox createCustomerCard(Customer customer) {
        VBox card = new VBox(15);
        card.setPrefWidth(300);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");

        HBox topRow = new HBox(10);
        topRow.setAlignment(Pos.CENTER_LEFT);

        StackPane avatar = createAvatar(customer.getName());

        VBox nameBox = new VBox(2);
        Label nameLbl = new Label(customer.getName());
        nameLbl.setTextFill(Color.WHITE);
        nameLbl.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label roomLbl = new Label(
                (customer.getRoom() != null)
                        ? "Room " + customer.getRoom().getRoomNumber() + " (" + customer.getRoom().getType() + ")"
                        : "No Room Assigned");
        roomLbl.setTextFill(Color.web("#94a3b8"));
        roomLbl.setFont(Font.font("System", 12));
        nameBox.getChildren().addAll(nameLbl, roomLbl);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        boolean checkedOut = "Checked Out".equals(customer.getStatus());
        Label statusBadge = new Label(customer.getStatus());
        statusBadge.setTextFill(Color.web(checkedOut ? "#fca5a5" : "#4ade80"));
        statusBadge.setStyle("-fx-background-color: " + (checkedOut ? "#7f1d1d" : "#14532d")
                + "; -fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 10px; -fx-font-weight: bold;");

        topRow.getChildren().addAll(avatar, nameBox, spacer, statusBadge);

        VBox details = new VBox(5);
        details.getChildren().addAll(
                createDetailRow("📞", customer.getPhone()),
                createDetailRow("✉", customer.getEmail()),
                createDetailRow("📅", customer.getCheckInDate() + " (" + customer.getDays() + " days)"));

        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_LEFT);
        Label totalLbl = new Label("Total Amount:");
        totalLbl.setTextFill(Color.web("#94a3b8"));
        Region tSpacer = new Region();
        HBox.setHgrow(tSpacer, Priority.ALWAYS);
        double total = (customer.getRoom() != null) ? customer.getRoom().getPrice() * customer.getDays() : 0.0;
        Label amountLbl = new Label(String.format("$%.2f", total));
        amountLbl.setTextFill(Color.web("#facc15"));
        amountLbl.setFont(Font.font("System", FontWeight.BOLD, 16));
        totalBox.getChildren().addAll(totalLbl, tSpacer, amountLbl);

        HBox actions = new HBox(10);
        actions.setAlignment(Pos.CENTER);
        Button viewBtn = createActionButton("View", "#3b82f6");
        Button editBtn = createActionButton("Edit", "#10b981");
        Button checkoutBtn = createActionButton("Checkout", "#ef4444");

        if (checkedOut)
            checkoutBtn.setDisable(true);

        checkoutBtn.setOnAction(e -> {
            DataStore.setSelectedCustomer(customer);
            if (checkoutBtn.getScene() != null) {
                TabPane tabPane = (TabPane) checkoutBtn.getScene().lookup(".tab-pane");
                if (tabPane != null) {
                    for (Tab t : tabPane.getTabs()) {
                        if ("Checkout".equals(t.getText())) {
                            tabPane.getSelectionModel().select(t);
                            break;
                        }
                    }
                }
            }
        });

        actions.getChildren().addAll(viewBtn, editBtn, checkoutBtn);

        card.getChildren().addAll(topRow, new Separator(), details, new Separator(), totalBox, new Separator(),
                actions);

        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: #27354f; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 4); -fx-translate-y: -2;"));
        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2); -fx-translate-y: 0;"));

        return card;
    }

    private StackPane createAvatar(String name) {
        String initials = "";
        if (name != null && !name.trim().isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            initials += parts[0].substring(0, 1).toUpperCase();
            if (parts.length > 1) {
                initials += parts[parts.length - 1].substring(0, 1).toUpperCase();
            }
        }

        Circle circle = new Circle(20, Color.web("#facc15"));
        Text initText = new Text(initials);
        initText.setFont(Font.font("System", FontWeight.BOLD, 14));
        initText.setFill(Color.web("#0f172a"));

        return new StackPane(circle, initText);
    }

    private HBox createDetailRow(String icon, String text) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label iconLbl = new Label(icon);
        iconLbl.setTextFill(Color.web("#64748b"));
        Label textLbl = new Label(text);
        textLbl.setTextFill(Color.web("#cbd5e1"));
        textLbl.setFont(Font.font("System", 12));
        row.getChildren().addAll(iconLbl, textLbl);
        return row;
    }

    private Button createActionButton(String text, String baseHexColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + baseHexColor + "20; -fx-text-fill: " + baseHexColor
                + "; -fx-background-radius: 5; -fx-padding: 5 12; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: " + baseHexColor + "40; -fx-text-fill: "
                + baseHexColor
                + "; -fx-background-radius: 5; -fx-padding: 5 12; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + baseHexColor + "20; -fx-text-fill: "
                + baseHexColor
                + "; -fx-background-radius: 5; -fx-padding: 5 12; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;"));
        return btn;
    }

    private boolean isCustomerCheckedOut(Customer c) {
        try {
            LocalDate start = LocalDate.parse(c.getCheckInDate());
            LocalDate end = start.plusDays(c.getDays());
            return LocalDate.now().isAfter(end) || LocalDate.now().isEqual(end);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private void handleRegistration() {
        try {
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            Room selectedRoom = roomBox.getValue();
            String date = checkInPicker.getValue() != null ? checkInPicker.getValue().toString() : "";

            if (!daysField.getText().matches("\\d+")) {
                showAlert("Enter valid number of days!", Alert.AlertType.ERROR);
                return;
            }
            int days = Integer.parseInt(daysField.getText());

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || selectedRoom == null || date.isEmpty()) {
                showAlert("Fill all fields!", Alert.AlertType.ERROR);
                return;
            }

            if (!email.endsWith("@gmail.com")) {
                showAlert("Only @gmail.com emails allowed!", Alert.AlertType.ERROR);
                return;
            }

            if (days <= 0) {
                showAlert("Days must be > 0", Alert.AlertType.ERROR);
                return;
            }

            LocalDate newStart = checkInPicker.getValue();
            LocalDate newEnd = newStart.plusDays(days);
            boolean hasConflict = false;
            for (Customer existing : DataStore.getCustomers()) {
                if (existing.getRoom() != null && existing.getRoom().getRoomNumber() == selectedRoom.getRoomNumber()) {
                    try {
                        LocalDate existStart = LocalDate.parse(existing.getCheckInDate());
                        LocalDate existEnd = existStart.plusDays(existing.getDays());
                        if (newStart.isBefore(existEnd) && newEnd.isAfter(existStart)) {
                            hasConflict = true;
                            break;
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            if (hasConflict) {
                showAlert("Room " + selectedRoom.getRoomNumber() + " is already booked for these dates!",
                        Alert.AlertType.ERROR);
                return;
            }

            Customer customer = new Customer(name, phone, email, selectedRoom, date, days);
            customer.setStatus("Active");
            DataStore.getCustomers().add(customer);

            clearForm();
            showAlert("Guest registered successfully!", Alert.AlertType.INFORMATION);

        } catch (Exception ex) {
            showAlert("Invalid input", Alert.AlertType.ERROR);
        }
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        daysField.clear();
        checkInPicker.setValue(null);
        roomBox.setValue(null);
    }

    private void showAlert(String msg, Alert.AlertType type) {
        Alert alert = new Alert(type, msg);
        alert.setHeaderText(null);
        alert.show();
    }

    public BorderPane getView() {
        return root;
    }
}