package com.hotel.ui;

import com.hotel.data.DataStore;
import com.hotel.model.Room;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class RoomView {

    private ScrollPane root;

   
    private TableView<Room> table;
    private FilteredList<Room> filteredList;

   
    private Label totalLabel = new Label("0");
    private Label availableLabel = new Label("0");
    private Label occupiedLabel = new Label("0");
    private Label cleaningLabel = new Label("0");
    private Label maintenanceLabel = new Label("0");

    
    private FlowPane roomStatusGrid;

    
    private FlowPane cardPreviewContainer;

    public RoomView() {
        VBox mainContainer = new VBox(20);
        mainContainer.setStyle("-fx-background-color: #0f172a;");
        mainContainer.setPadding(new Insets(20));

        root = new ScrollPane(mainContainer);
        root.setFitToWidth(true);
        root.setStyle("-fx-background: #0f172a; -fx-border-color: transparent;");

        
        HBox statsBox = new HBox(15);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.getChildren().addAll(
                createStatCard("Total Rooms", totalLabel, "#3b82f6"),
                createStatCard("Available", availableLabel, "#22c55e"),
                createStatCard("Occupied", occupiedLabel, "#ef4444"),
                createStatCard("Cleaning", cleaningLabel, "#facc15"),
                createStatCard("Maintenance", maintenanceLabel, "#f59e0b"));

       
        HBox contentBox = new HBox(20);

       
        VBox formBox = createFormPanel();

        
        VBox rightPanel = createRightPanel();
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        contentBox.getChildren().addAll(formBox, rightPanel);

      
        VBox gridSection = createGridSection();

        mainContainer.getChildren().addAll(statsBox, contentBox, gridSection);

       
        setupDataBindings();
    }

    private VBox createStatCard(String title, Label valueLabel, String color) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #94a3b8; -fx-font-weight: bold;");

        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox card = new VBox(10, titleLabel, valueLabel);
        card.setStyle(
                "-fx-background-color: #1e293b;" +
                        "-fx-padding: 15 25;" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + color + "; -fx-border-width: 0 0 0 4; -fx-border-radius: 12;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        card.setPrefWidth(200);

        return card;
    }

    private TextField roomNoField = new TextField();
    private ComboBox<String> typeBox = new ComboBox<>();
    private ComboBox<String> statusBox = new ComboBox<>();
    private TextField priceField = new TextField();
    private TextField floorField = new TextField();

    private VBox createFormPanel() {
        VBox formBox = new VBox(15);
        formBox.setStyle(
                "-fx-background-color: #1e293b; " +
                        "-fx-padding: 25; " +
                        "-fx-background-radius: 15; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");
        formBox.setPrefWidth(320);

        Label formTitle = new Label("Add / Edit Room");
        formTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        typeBox.getItems().addAll("Single", "Double", "Deluxe");
        statusBox.getItems().addAll("Available", "Occupied", "Cleaning", "Maintenance");
        statusBox.setValue("Available");

        styleField(roomNoField, "Room Number");
        styleField(priceField, "Price per Day");
        styleField(floorField, "Floor");
        typeBox.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");
        statusBox.setStyle("-fx-background-color: #334155; -fx-text-fill: white;");

        typeBox.setMaxWidth(Double.MAX_VALUE);
        statusBox.setMaxWidth(Double.MAX_VALUE);

        Button addBtn = new Button("Add Room");
        Button editBtn = new Button("Edit Selected Room");
        Button deleteBtn = new Button("Delete Room");
        Button clearBtn = new Button("Clear Fields");

        stylePrimaryButton(addBtn, "#facc15", "#0f172a");
        styleSecondaryButton(editBtn);
        styleDangerButton(deleteBtn);
        styleSecondaryButton(clearBtn);

        
        addBtn.setOnAction(e -> {
            boolean exists = DataStore.getRooms().stream()
                    .anyMatch(r -> r.getRoomNumber() == Integer.parseInt(roomNoField.getText()));

            if (exists) {
                showAlert("Room number already exists!");
                return;
            }
            if (roomNoField.getText().isEmpty() || typeBox.getValue() == null) {
                showAlert("Fill all fields");
                return;
            }

            try {
                int roomNo = Integer.parseInt(roomNoField.getText());
                double price = Double.parseDouble(priceField.getText());
                int floor = Integer.parseInt(floorField.getText());

                Room room = new Room(roomNo, typeBox.getValue(), price, statusBox.getValue(), floor);
                DataStore.addRoom(room);

                clearFormFields();
                showSuccess("Room added successfully!");

            } catch (Exception ex) {
                showAlert("Invalid input");
            }
        });

       
        editBtn.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select a room from the table first!");
                return;
            }
            try {
                
                if (!roomNoField.getText().isEmpty()) {
                    int newRoomNo = Integer.parseInt(roomNoField.getText());
                    if (newRoomNo != selected.getRoomNumber()) {
                        boolean exists = DataStore.getRooms().stream()
                                .anyMatch(r -> r.getRoomNumber() == newRoomNo);
                        if (exists) {
                            showAlert("Room number already exists!");
                            return;
                        }
                    }
                    selected.roomNumberProperty().set(newRoomNo);
                }

                
                if (typeBox.getValue() != null) {
                    selected.typeProperty().set(typeBox.getValue());
                }

                if (!priceField.getText().isEmpty()) {
                    selected.priceProperty().set(Double.parseDouble(priceField.getText()));
                }

                if (!floorField.getText().isEmpty()) {
                    selected.floorProperty().set(Integer.parseInt(floorField.getText()));
                }

                if (statusBox.getValue() != null) {
                    selected.setStatus(statusBox.getValue());
                }

               
                table.refresh();
                showSuccess("Room updated!");

            } catch (Exception ex) {
                showAlert("Invalid input: Please make sure numbers are formatted correctly.");
            }
        });

      
        deleteBtn.setOnAction(e -> {
            Room selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Select a room to delete!");
                return;
            }
            DataStore.getRooms().remove(selected);
            showSuccess("Room deleted!");
        });

        clearBtn.setOnAction(e -> clearFormFields());

        formBox.getChildren().addAll(
                formTitle,
                createStyledLabel("Room Number"), roomNoField,
                createStyledLabel("Room Type"), typeBox,
                createStyledLabel("Price per Day"), priceField,
                createStyledLabel("Floor"), floorField,
                createStyledLabel("Status"), statusBox,
                new Region(),
                addBtn, editBtn, deleteBtn, clearBtn);

        return formBox;
    }

    private void clearFormFields() {
        roomNoField.clear();
        priceField.clear();
        floorField.clear();
        typeBox.setValue(null);
        statusBox.setValue("Available");
    }

    private TextField searchField = new TextField();

    private VBox createRightPanel() {
        VBox rightPanel = new VBox(20);

       
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("All Rooms");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");

        styleField(searchField, "Search by room number or type");
        searchField.setPrefWidth(250);
        searchField.setStyle(
                "-fx-background-color: #1e293b; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-background-radius: 20; -fx-padding: 8 15;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

       
        HBox filtersBox = new HBox(10);
        ToggleButton btnAll = createFilterButton("All", true);
        ToggleButton btnAvail = createFilterButton("Available", false);
        ToggleButton btnOcc = createFilterButton("Occupied", false);
        ToggleButton btnClean = createFilterButton("Cleaning", false);
        ToggleButton btnMaint = createFilterButton("Maintenance", false);

        ToggleGroup group = new ToggleGroup();
        btnAll.setToggleGroup(group);
        btnAvail.setToggleGroup(group);
        btnOcc.setToggleGroup(group);
        btnClean.setToggleGroup(group);
        btnMaint.setToggleGroup(group);

        
        filteredList = new FilteredList<>(DataStore.getRooms());

      
        Runnable applyFilter = () -> {
            filteredList.setPredicate(room -> {
                String val = searchField.getText();
                boolean matchesSearch = (val == null || val.isEmpty() ||
                        String.valueOf(room.getRoomNumber()).contains(val.toLowerCase()) ||
                        room.getType().toLowerCase().contains(val.toLowerCase()));

                boolean matchesStatus = true;
                ToggleButton selected = (ToggleButton) group.getSelectedToggle();
                if (selected != null && selected != btnAll) {
                    matchesStatus = room.getStatus().equals(selected.getText());
                }

                return matchesSearch && matchesStatus;
            });
        };

        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter.run());
        group.selectedToggleProperty().addListener((obs, oldV, newV) -> {
            if (newV == null)
                btnAll.setSelected(true);
            applyFilter.run();
        });

        filtersBox.getChildren().addAll(btnAll, btnAvail, btnOcc, btnClean, btnMaint);
        headerBox.getChildren().addAll(title, spacer, searchField, filtersBox);

       
        table = new TableView<>();
        table.setPrefHeight(250);
        table.setStyle(
                "-fx-background-color: #1e293b; -fx-base: #1e293b; -fx-control-inner-background: #1e293b; -fx-text-fill: white;");

        TableColumn<Room, Number> col1 = new TableColumn<>("Room No");
        col1.setCellValueFactory(data -> data.getValue().roomNumberProperty());
        col1.setPrefWidth(100);

        TableColumn<Room, String> col2 = new TableColumn<>("Type");
        col2.setCellValueFactory(data -> data.getValue().typeProperty());
        col2.setPrefWidth(120);

        TableColumn<Room, Number> col3 = new TableColumn<>("Price");
        col3.setCellValueFactory(data -> data.getValue().priceProperty());
        col3.setPrefWidth(100);

        TableColumn<Room, Number> colFloor = new TableColumn<>("Floor");
        colFloor.setCellValueFactory(data -> data.getValue().floorProperty());
        colFloor.setPrefWidth(80);

        TableColumn<Room, String> col4 = new TableColumn<>("Status");
        col4.setCellValueFactory(data -> data.getValue().statusProperty());
        col4.setPrefWidth(150);

        
        col4.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setStyle(
                            "-fx-padding: 3 10; -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                    switch (status) {
                        case "Available":
                            badge.setStyle(badge.getStyle() + "-fx-background-color: #14532d; -fx-text-fill: #4ade80;");
                            break;
                        case "Occupied":
                            badge.setStyle(badge.getStyle() + "-fx-background-color: #7f1d1d; -fx-text-fill: #f87171;");
                            break;
                        case "Cleaning":
                            badge.setStyle(badge.getStyle() + "-fx-background-color: #78350f; -fx-text-fill: #fbbf24;");
                            break;
                        case "Maintenance":
                            badge.setStyle(badge.getStyle() + "-fx-background-color: #7c2d12; -fx-text-fill: #fb923c;");
                            break;
                    }
                    setGraphic(badge);
                }
            }
        });

        table.getColumns().addAll(col1, col2, colFloor, col3, col4);
        table.setItems(filteredList);

        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                roomNoField.setText(String.valueOf(newVal.getRoomNumber()));
                typeBox.setValue(newVal.getType());
                priceField.setText(String.valueOf(newVal.getPrice()));
                floorField.setText(String.valueOf(newVal.getFloor()));
                statusBox.setValue(newVal.getStatus());
            }
        });

        table.setRowFactory(tv -> {
            TableRow<Room> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Room room = row.getItem();
                    if (room.getStatus().equals("Available")) {
                        room.setStatus("Occupied");
                    } else {
                        room.setStatus("Available");
                    }
                    table.getItems().set(table.getItems().indexOf(room), room); 
                }
            });
            return row;
        });

        
        VBox previewSection = new VBox(15);
        Label previewTitle = new Label("Card View Preview");
        previewTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        cardPreviewContainer = new FlowPane(15, 15);
        cardPreviewContainer.setAlignment(Pos.TOP_LEFT);

        ScrollPane previewScroll = new ScrollPane(cardPreviewContainer);
        previewScroll.setFitToWidth(true);
        previewScroll.setPrefHeight(200);
        previewScroll.setStyle("-fx-background: #0f172a; -fx-border-color: transparent;");

        previewSection.getChildren().addAll(previewTitle, previewScroll);

        rightPanel.getChildren().addAll(headerBox, table, previewSection);
        return rightPanel;
    }

    private void setupDataBindings() {
        Runnable updateStats = () -> {
            int total = DataStore.getRooms().size();
            int available = 0, occupied = 0, cleaning = 0, maintenance = 0;

            for (Room r : DataStore.getRooms()) {
                switch (r.getStatus()) {
                    case "Available":
                        available++;
                        break;
                    case "Occupied":
                        occupied++;
                        break;
                    case "Cleaning":
                        cleaning++;
                        break;
                    case "Maintenance":
                        maintenance++;
                        break;
                }
            }
            totalLabel.setText(String.valueOf(total));
            availableLabel.setText(String.valueOf(available));
            occupiedLabel.setText(String.valueOf(occupied));
            cleaningLabel.setText(String.valueOf(cleaning));
            maintenanceLabel.setText(String.valueOf(maintenance));

            rebuildCardsAndGrid();
            table.refresh();
        };

      
        for (Room r : DataStore.getRooms()) {
            r.statusProperty().addListener((obs, oldVal, newVal) -> updateStats.run());
        }

       
        DataStore.getRooms().addListener((ListChangeListener.Change<? extends Room> c) -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    for (Room newRoom : c.getAddedSubList()) {
                        newRoom.statusProperty().addListener((obs, oldVal, newVal) -> updateStats.run());
                    }
                }
            }
            updateStats.run();
        });

        updateStats.run();
    }

    private void rebuildCardsAndGrid() {
       
        cardPreviewContainer.getChildren().clear();
        for (Room room : filteredList) {
            cardPreviewContainer.getChildren().add(createRoomCard(room));
        }

       
        roomStatusGrid.getChildren().clear();
        for (Room room : DataStore.getRooms()) {
            VBox box = new VBox(5);
            box.setAlignment(Pos.CENTER);
            box.setPrefSize(70, 70);
            box.setStyle(
                    "-fx-background-color: transparent; -fx-border-radius: 8; -fx-border-width: 2; -fx-cursor: hand;");

            Label numLbl = new Label(String.valueOf(room.getRoomNumber()));
            numLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");

            String borderColor = "#94a3b8";
            switch (room.getStatus()) {
                case "Available":
                    borderColor = "#22c55e";
                    break;
                case "Occupied":
                    borderColor = "#ef4444";
                    break; 
                case "Cleaning":
                    borderColor = "#facc15";
                    break; 
                case "Maintenance":
                    borderColor = "#f97316";
                    break; 
            }
            box.setStyle(box.getStyle() + "-fx-border-color: " + borderColor + ";");

            box.setOnMouseClicked(e -> {
                room.setStatus("Available");
                table.getItems().set(table.getItems().indexOf(room), room);
                rebuildCardsAndGrid();
                table.refresh();
            });

            box.getChildren().add(numLbl);
            roomStatusGrid.getChildren().add(box);
        }
    }

    private VBox createGridSection() {
        VBox gridSection = new VBox(15);
        gridSection.setStyle(
                "-fx-background-color: #1e293b; -fx-padding: 20; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);");

        Label gridTitle = new Label("Room Status Grid");
        gridTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

        roomStatusGrid = new FlowPane(10, 10);
        roomStatusGrid.setAlignment(Pos.TOP_LEFT);

        
        HBox legend = new HBox(20);
        legend.setAlignment(Pos.CENTER_LEFT);
        legend.getChildren().addAll(
                createLegendItem("Available", "#22c55e"),
                createLegendItem("Occupied", "#ef4444"),
                createLegendItem("Cleaning", "#facc15"),
                createLegendItem("Maintenance", "#f97316"));

        gridSection.getChildren().addAll(gridTitle, roomStatusGrid, legend);
        return gridSection;
    }

    private HBox createLegendItem(String text, String color) {
        HBox box = new HBox(5);
        box.setAlignment(Pos.CENTER_LEFT);
        Region dot = new Region();
        dot.setPrefSize(12, 12);
        dot.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50%;");
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #94a3b8;");
        box.getChildren().addAll(dot, lbl);
        return box;
    }

    private VBox createRoomCard(Room room) {
        VBox card = new VBox(10);
        card.setPrefWidth(220);
        card.setPadding(new Insets(15));
        card.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12;");

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        Label rmNum = new Label("Room " + room.getRoomNumber());
        rmNum.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: white;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge = new Label(room.getStatus());
        badge.setStyle("-fx-padding: 3 8; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 10px;");
        switch (room.getStatus()) {
            case "Available":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #14532d; -fx-text-fill: #4ade80;");
                break;
            case "Occupied":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #7f1d1d; -fx-text-fill: #f87171;");
                break;
            case "Cleaning":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #78350f; -fx-text-fill: #fbbf24;");
                break;
            case "Maintenance":
                badge.setStyle(badge.getStyle() + "-fx-background-color: #7c2d12; -fx-text-fill: #fb923c;");
                break;
        }

        topRow.getChildren().addAll(rmNum, spacer, badge);

        VBox details = new VBox(5);
        details.getChildren().addAll(
                createDetailRow("Type:", room.getType()),
                createDetailRow("Floor:", String.valueOf(room.getFloor())),
                createDetailRow("Price:", "$" + room.getPrice() + " / day"));

        card.getChildren().addAll(topRow, new Separator(), details);
        return card;
    }

    private HBox createDetailRow(String label, String val) {
        HBox row = new HBox(10);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
        Label value = new Label(val);
        value.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
        row.getChildren().addAll(lbl, value);
        return row;
    }

    private void styleField(TextField field, String prompt) {
        field.setPromptText(prompt);
        field.setStyle(
                "-fx-background-color: #334155; -fx-text-fill: white; -fx-prompt-text-fill: #94a3b8; -fx-background-radius: 6; -fx-padding: 8;");
    }

    private Label createStyledLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px; -fx-font-weight: bold;");
        VBox.setMargin(lbl, new Insets(5, 0, -10, 0));
        return lbl;
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

    private void stylePrimaryButton(Button btn, String bgColor, String txColor) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: " + txColor
                + "; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
    }

    private void styleSecondaryButton(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-border-color: #475569; -fx-border-radius: 8; -fx-padding: 10; -fx-cursor: hand;");
    }

    private void styleDangerButton(Button btn) {
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(
                "-fx-background-color: #7f1d1d; -fx-text-fill: #f87171; -fx-background-radius: 8; -fx-padding: 10; -fx-cursor: hand; -fx-font-weight: bold;");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setHeaderText(null);
        alert.show();
    }

    private void showSuccess(String msg) {
        Label successMsg = new Label(msg);
        successMsg.setStyle("-fx-text-fill: #4ade80; -fx-font-weight: bold; -fx-font-size: 14px;");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(successMsg);
        alert.show();
    }

    public ScrollPane getView() {
        return root;
    }
}