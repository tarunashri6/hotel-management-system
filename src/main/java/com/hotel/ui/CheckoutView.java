package com.hotel.ui;

import com.hotel.model.*;
import com.hotel.data.DataStore;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CheckoutView {

    private VBox root;
    private String selectedPayment = null;
    private Customer selectedCustomer;

    private String invNumber;
    private String invDate;
    private String invGuestName;
    private int invRoom;
    private double invTotal;

    public CheckoutView() {

        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #0f172a;");

        HBox layout = new HBox(20);
        layout.setAlignment(Pos.TOP_LEFT);

        VBox left = new VBox(15);
        left.setPrefWidth(350);
        HBox.setHgrow(left, Priority.NEVER);

        Label title = new Label("Active Guests");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");

        ListView<Customer> activeList = new ListView<>();
        activeList.setStyle(
                "-fx-background-color: transparent; -fx-control-inner-background: #0f172a; -fx-control-inner-background-alt: #0f172a; -fx-border-color: transparent;");
        activeList.setPrefHeight(600);

        activeList.setCellFactory(new Callback<ListView<Customer>, ListCell<Customer>>() {
            @Override
            public ListCell<Customer> call(ListView<Customer> param) {
                return new ListCell<Customer>() {
                    @Override
                    protected void updateItem(Customer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setGraphic(null);
                            setStyle("-fx-background-color: transparent;");
                        } else {
                            VBox card = new VBox(10);
                            card.setPadding(new Insets(15));
                            card.setStyle(
                                    "-fx-background-color: #1e293b; -fx-background-radius: 12px; -fx-cursor: hand;");

                            if (isSelected()) {
                                card.setStyle(
                                        "-fx-background-color: #1e293b; -fx-background-radius: 12px; -fx-border-color: #facc15; -fx-border-radius: 12px; -fx-border-width: 2px; -fx-cursor: hand;");
                            }

                            HBox topRow = new HBox(12);
                            topRow.setAlignment(Pos.CENTER_LEFT);

                            StackPane avatarInfo = new StackPane();
                            Circle avatarBg = new Circle(20, Color.web("#334155"));
                            String initials = item.getName().length() > 0 ? item.getName().substring(0, 1).toUpperCase()
                                    : "?";
                            if (item.getName().contains(" ")) {
                                String[] parts = item.getName().split(" ");
                                if (parts.length > 1 && parts[1].length() > 0) {
                                    initials = parts[0].substring(0, 1).toUpperCase()
                                            + parts[1].substring(0, 1).toUpperCase();
                                }
                            }
                            Text avatarText = new Text(initials);
                            avatarText.setFill(Color.WHITE);
                            avatarText.setFont(Font.font("System", FontWeight.BOLD, 14));
                            avatarInfo.getChildren().addAll(avatarBg, avatarText);

                            VBox namePhoneBox = new VBox(4);
                            Label nameLbl = new Label(item.getName());
                            nameLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
                            Label phoneLbl = new Label(item.getPhone() != null ? item.getPhone() : "N/A");
                            phoneLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 12px;");
                            namePhoneBox.getChildren().addAll(nameLbl, phoneLbl);

                            Region spacer = new Region();
                            HBox.setHgrow(spacer, Priority.ALWAYS);

                            Label statusBadge = new Label("Active");
                            statusBadge.setStyle(
                                    "-fx-background-color: #166534; -fx-text-fill: #4ade80; -fx-padding: 4 8; -fx-background-radius: 6; -fx-font-size: 10px; -fx-font-weight: bold;");

                            topRow.getChildren().addAll(avatarInfo, namePhoneBox, spacer, statusBadge);

                            HBox bottomRow = new HBox(15);

                            VBox roomInfo = new VBox(2);
                            Label rTitle = new Label("Room");
                            rTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
                            Label rVal = new Label(item.getRoom().getRoomNumber() + " - " + item.getRoom().getType());
                            rVal.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
                            roomInfo.getChildren().addAll(rTitle, rVal);

                            VBox stayInfo = new VBox(2);
                            Label sTitle = new Label("Nights");
                            sTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
                            Label sVal = new Label(item.getDays() + " (" + item.getCheckInDate() + ")");
                            sVal.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold;");
                            stayInfo.getChildren().addAll(sTitle, sVal);

                            VBox amtInfo = new VBox(2);
                            Label aTitle = new Label("Due");
                            aTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 10px;");
                            double due = item.getRoom().getPrice() * item.getDays();
                            Label aVal = new Label("₹" + due);
                            aVal.setStyle("-fx-text-fill: #facc15; -fx-font-size: 12px; -fx-font-weight: bold;");
                            amtInfo.getChildren().addAll(aTitle, aVal);

                            bottomRow.getChildren().addAll(roomInfo, stayInfo, amtInfo);

                            card.getChildren().addAll(topRow, new Separator(), bottomRow);

                            setGraphic(card);
                            setStyle("-fx-background-color: transparent; -fx-padding: 0 0 10 0;");
                        }
                    }
                };
            }
        });

        left.getChildren().addAll(title, activeList);

        VBox center = new VBox(20);
        center.setPrefWidth(400);
        HBox.setHgrow(center, Priority.ALWAYS);

        VBox billBox = new VBox(15);
        billBox.setPadding(new Insets(25));
        billBox.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12px;");

        HBox billHeader = new HBox();
        billHeader.setAlignment(Pos.CENTER_LEFT);
        Label billTitle = new Label("Bill Summary");
        billTitle.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label statusLbl = new Label("Checking Out");
        statusLbl.setStyle(
                "-fx-background-color: #7f1d1d; -fx-text-fill: #fca5a5; -fx-padding: 4 8; -fx-background-radius: 6; -fx-font-size: 11px; -fx-font-weight: bold;");
        statusLbl.setVisible(false);
        billHeader.getChildren().addAll(billTitle, spacer1, statusLbl);

        Label cNameLbl = new Label("");
        cNameLbl.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Label cPhoneLbl = new Label("");
        cPhoneLbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        VBox custInfoBox = new VBox(3, cNameLbl, cPhoneLbl);
        custInfoBox.setVisible(false);
        custInfoBox.setManaged(false);

        GridPane detailGrid = new GridPane();
        detailGrid.setVgap(12);
        detailGrid.setHgap(20);

        Label rNumT = createMutedLabel("Room Number");
        Label rNumV = createWhiteLabel("--");
        Label rTypeT = createMutedLabel("Room Type");
        Label rTypeV = createWhiteLabel("--");
        Label cInT = createMutedLabel("Check-in Date");
        Label cInV = createWhiteLabel("--");
        Label cOutT = createMutedLabel("Check-out Date");
        Label cOutV = createWhiteLabel("--");
        Label durT = createMutedLabel("Duration");
        Label durV = createWhiteLabel("--");
        Label rateT = createMutedLabel("Rate per Night");
        Label rateV = createWhiteLabel("--");

        detailGrid.addRow(0, rNumT, rNumV, rTypeT, rTypeV);
        detailGrid.addRow(1, cInT, cInV, cOutT, cOutV);
        detailGrid.addRow(2, durT, durV, rateT, rateV);

        Label nameLabel = new Label();
        Label roomLabel = new Label();
        Label daysLabel = new Label();
        Label totalLabel = new Label();

        nameLabel.setVisible(false);
        nameLabel.setManaged(false);
        roomLabel.setVisible(false);
        roomLabel.setManaged(false);
        daysLabel.setVisible(false);
        daysLabel.setManaged(false);

        totalLabel.setStyle("-fx-text-fill: #facc15; -fx-font-size: 22px; -fx-font-weight: bold;");

        HBox totalBox = new HBox(15);
        totalBox.setAlignment(Pos.CENTER_LEFT);
        Label totalTitle = new Label("Total Amount");
        totalTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        totalBox.getChildren().addAll(totalTitle, spacer2, totalLabel);

        billBox.getChildren().addAll(
                billHeader,
                custInfoBox,
                new Separator(),
                detailGrid,
                new Separator(),
                totalBox,
                nameLabel, roomLabel, daysLabel);

        VBox payBox = new VBox(10);
        Label payTitle = new Label("Payment Method");
        payTitle.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 14px; -fx-font-weight: bold;");

        HBox toggleBox = new HBox(10);
        Button cashBtn = new Button("Cash");
        Button cardBtn = new Button("Card");
        Button upiBtn = new Button("UPI");

        Button[] payBtns = { cashBtn, cardBtn, upiBtn };

        for (Button btn : payBtns) {
            btn.setStyle(
                    "-fx-background-color: transparent; -fx-border-color: #334155; -fx-border-radius: 6; -fx-text-fill: #cbd5e1; -fx-cursor: hand; -fx-padding: 8 16;");
            btn.setOnAction(e -> {

                for (Button b : payBtns) {
                    b.setStyle(
                            "-fx-background-color: transparent; -fx-border-color: #334155; -fx-border-radius: 6; -fx-text-fill: #cbd5e1; -fx-cursor: hand; -fx-padding: 8 16;");
                }

                btn.setStyle(
                        "-fx-background-color: #334155; -fx-border-color: #facc15; -fx-border-radius: 6; -fx-text-fill: #facc15; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 16;");
                selectedPayment = btn.getText();
            });
        }

        toggleBox.getChildren().addAll(cashBtn, cardBtn, upiBtn);
        payBox.getChildren().addAll(payTitle, toggleBox);

        Button checkoutBtn = new Button("Confirm Checkout & Release Room");
        checkoutBtn.setStyle(
                "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12 20; -fx-background-radius: 8; -fx-cursor: hand;");
        checkoutBtn.setMaxWidth(Double.MAX_VALUE);
        checkoutBtn.setDisable(true);

        Label successLabel = new Label();
        successLabel.setStyle(
                "-fx-text-fill: #10b981; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-color: #064e3b; -fx-padding: 10; -fx-background-radius: 5;");
        successLabel.setMaxWidth(Double.MAX_VALUE);
        successLabel.setAlignment(Pos.CENTER);
        successLabel.setVisible(false);
        successLabel.setManaged(false);

        VBox successBox = new VBox(8);
        successBox.setStyle(
                "-fx-background-color: #064e3b; -fx-border-color: #10b981; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 15;");
        successBox.setVisible(false);
        successBox.setManaged(false);
        Label sIcon = new Label("✓");
        sIcon.setStyle("-fx-text-fill: #34d399; -fx-font-size: 20px; -fx-font-weight: bold;");
        Label sMsgTitle = new Label("Checkout Successful");
        sMsgTitle.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold; -fx-font-size: 14px;");
        Label sMsgDesc = new Label("Room is now available. Receipt generated.");
        sMsgDesc.setStyle("-fx-text-fill: #a7f3d0; -fx-font-size: 12px;");
        successBox.getChildren().addAll(new HBox(10, sIcon, sMsgTitle), sMsgDesc);

        center.getChildren().addAll(billBox, payBox, checkoutBtn, successLabel, successBox);

        VBox right = new VBox(15);
        right.setPrefWidth(350);
        HBox.setHgrow(right, Priority.NEVER);

        VBox recCard = new VBox(8);
        recCard.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5); -fx-padding: 25;");
        recCard.setAlignment(Pos.CENTER);

        Label rH1 = new Label("GRAND HOTEL");
        rH1.setStyle("-fx-font-family: 'serif'; -fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label rH2 = new Label("123 Luxury Ave, City Center");
        rH2.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        Label rH3 = new Label("RECEIPT");
        rH3.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a; -fx-padding: 15 0 10 0;");

        HBox rMetaBox = new HBox();
        Label iNumT = new Label("Invoice No: INV-" + (int) (Math.random() * 10000));
        iNumT.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        Label iDateT = new Label("Date: " + LocalDate.now().toString());
        iDateT.setStyle("-fx-font-size: 11px; -fx-text-fill: #334155;");
        rMetaBox.getChildren().addAll(iNumT, spacer3, iDateT);

        VBox custInvBox = new VBox(2);
        custInvBox.setAlignment(Pos.CENTER_LEFT);
        Label invGuestT = new Label("Guest: --");
        invGuestT.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Label invContactT = new Label("Contact: --");
        invContactT.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
        custInvBox.getChildren().addAll(invGuestT, invContactT);

        GridPane rData = new GridPane();
        rData.setVgap(8);
        rData.setHgap(30);
        rData.addRow(0, createDarkMutedLabel("Room"), createDarkLabel("--"));
        rData.addRow(1, createDarkMutedLabel("Duration"), createDarkLabel("--"));
        rData.addRow(2, createDarkMutedLabel("Rate"), createDarkLabel("--"));
        rData.addRow(3, createDarkMutedLabel("Taxes"), createDarkLabel("0%"));
        Separator rSep = new Separator();

        HBox grandTotalBox = new HBox();
        Label gtT = new Label("TOTAL PAID");
        gtT.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        Region spacer4 = new Region();
        HBox.setHgrow(spacer4, Priority.ALWAYS);
        Label gtV = new Label("₹0.0");
        gtV.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        grandTotalBox.getChildren().addAll(gtT, spacer4, gtV);

        Label thanksLbl = new Label("Thank you for staying with us!");
        thanksLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-padding: 20 0 0 0;");

        recCard.getChildren().addAll(
                rH1, rH2, rH3, rMetaBox, new Separator(),
                custInvBox, new Separator(), rData, rSep, grandTotalBox, thanksLbl);

        HBox printBox = new HBox(10);
        Button printBtn = new Button("Print Invoice");
        printBtn.setStyle(
                "-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
        Button savePdfBtn = new Button("Save PDF");
        savePdfBtn.setStyle(
                "-fx-background-color: #475569; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 6; -fx-cursor: hand;");
        printBox.getChildren().addAll(printBtn, savePdfBtn);

        right.getChildren().addAll(recCard, printBox);

        VBox bottomBox = new VBox(10);
        Label logTitle = new Label("Today's Checkouts");
        logTitle.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        ObservableList<String> checkoutLog = FXCollections.observableArrayList();
        ListView<String> logList = new ListView<>(checkoutLog);
        logList.setPrefHeight(120);
        logList.setStyle(
                "-fx-background-color: transparent; -fx-control-inner-background: #1e293b; -fx-control-inner-background-alt: #1e293b; -fx-border-color: transparent; -fx-background-radius: 8;");

        logList.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("-fx-background-color: transparent;");
                } else {
                    setText("• " + item);
                    setStyle(
                            "-fx-background-color: transparent; -fx-text-fill: #94a3b8; -fx-font-size: 13px; -fx-padding: 5 10;");
                }
            }
        });

        bottomBox.getChildren().addAll(logTitle, logList);

        layout.getChildren().addAll(left, center, right);
        root.getChildren().addAll(layout, bottomBox);

        activeList.setItems(DataStore.getCustomers().filtered(
                c -> c.getRoom() != null && c.getRoom().getStatus().equals("Occupied")));

        DataStore.sharedSelectedCustomerProperty().addListener((obs, oldVal, selected) -> {
            if (selected != null && activeList.getItems().contains(selected)) {
                activeList.getSelectionModel().select(selected);
                activeList.scrollTo(selected);

                selectedCustomer = selected;

                Room r = selected.getRoom();
                double total = r.getPrice() * selected.getDays();

                nameLabel.setText("Name: " + selected.getName());
                roomLabel.setText("Room: " + r.getRoomNumber());
                daysLabel.setText("Days: " + selected.getDays());
                totalLabel.setText("Total: ₹" + total);
            }
        });

        Customer initialSelected = DataStore.getSelectedCustomer();
        if (initialSelected != null && activeList.getItems().contains(initialSelected)) {
            activeList.getSelectionModel().select(initialSelected);
            activeList.scrollTo(initialSelected);

            selectedCustomer = initialSelected;

            Room r = initialSelected.getRoom();
            double total = r.getPrice() * initialSelected.getDays();

            nameLabel.setText("Name: " + initialSelected.getName());
            roomLabel.setText("Room: " + r.getRoomNumber());
            daysLabel.setText("Days: " + initialSelected.getDays());
            totalLabel.setText("Total: ₹" + total);
        }

        activeList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                checkoutBtn.setDisable(false);
                successBox.setVisible(false);
                successBox.setManaged(false);

                statusLbl.setVisible(true);
                custInfoBox.setVisible(true);
                custInfoBox.setManaged(true);
                billTitle.setText("Bill Summary — " + newSelection.getName());
                cNameLbl.setText(newSelection.getName());
                cPhoneLbl.setText(newSelection.getPhone());

                Room r = newSelection.getRoom();
                rNumV.setText(String.valueOf(r.getRoomNumber()));
                rTypeV.setText(r.getType());
                cInV.setText(newSelection.getCheckInDate());

                try {
                    LocalDate inDate = LocalDate.parse(newSelection.getCheckInDate(),
                            DateTimeFormatter.ofPattern("M/d/yyyy"));
                    cOutV.setText(
                            inDate.plusDays(newSelection.getDays()).format(DateTimeFormatter.ofPattern("M/d/yyyy")));
                } catch (Exception ex) {
                    cOutV.setText("N/A");
                }

                durV.setText(newSelection.getDays() + " Nights");
                rateV.setText("₹" + r.getPrice());

                invGuestT.setText("Guest: " + newSelection.getName());
                invContactT.setText("Contact: " + newSelection.getPhone());
                ((Label) rData.getChildren().get(1)).setText("Room " + r.getRoomNumber() + " (" + r.getType() + ")");
                ((Label) rData.getChildren().get(3)).setText(newSelection.getDays() + " Nights");
                ((Label) rData.getChildren().get(5)).setText("₹" + r.getPrice() + " /night");
                double tot = r.getPrice() * newSelection.getDays();
                gtV.setText("₹" + tot);
            } else {
                checkoutBtn.setDisable(true);
            }
        });

        totalLabel.textProperty().addListener((obs, oldVal, newVal) -> {
            if ("Checkout Complete!".equals(newVal)) {
                successBox.setVisible(true);
                successBox.setManaged(true);
                statusLbl.setVisible(false);
                custInfoBox.setVisible(false);
                custInfoBox.setManaged(false);
                billTitle.setText("Bill Summary");
                rNumV.setText("--");
                rTypeV.setText("--");
                cInV.setText("--");
                cOutV.setText("--");
                durV.setText("--");
                rateV.setText("--");
                totalLabel.setText("₹0.0");
                checkoutBtn.setDisable(true);
                gtV.setText("₹0.0");
                invGuestT.setText("Guest: --");
                invContactT.setText("Contact: --");
            }
        });

        activeList.setOnMouseClicked(e -> {
            selectedCustomer = activeList.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null)
                return;

            Room r = selectedCustomer.getRoom();

            double total = r.getPrice() * selectedCustomer.getDays();

            nameLabel.setText("Name: " + selectedCustomer.getName());
            roomLabel.setText("Room: " + r.getRoomNumber());
            daysLabel.setText("Days: " + selectedCustomer.getDays());
            totalLabel.setText("Total: ₹" + total);
        });

        checkoutBtn.setOnAction(e -> {
            Customer c = selectedCustomer;
            if (c == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Select a guest first");
                alert.showAndWait();
                return;
            }

            if (selectedPayment == null || selectedPayment.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Select payment method");
                alert.showAndWait();
                return;
            }

            Room r = c.getRoom();

            r.setStatus("Available");

            c.setStatus("Checked Out");

            activeList.setItems(DataStore.getCustomers().filtered(
                    cust -> cust.getRoom() != null && cust.getRoom().getStatus().equals("Occupied")));

            String timeStr = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            checkoutLog.add(0, c.getName() + " — Room " + r.getRoomNumber() + " released at " + timeStr);

            invNumber = "GH-" + LocalDate.now().getYear() + "-" + (int) (Math.random() * 10000);
            invDate = LocalDate.now().toString();
            invGuestName = c.getName();
            invRoom = r.getRoomNumber();
            invTotal = r.getPrice() * c.getDays();

            nameLabel.setText("");
            roomLabel.setText("");
            daysLabel.setText("");
            totalLabel.setText("Checkout Complete!");

            successLabel.setText("✓ Room " + r.getRoomNumber() + " is now available. Receipt generated.");
            successLabel.setVisible(true);
            successLabel.setManaged(true);

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(ev -> {
                successLabel.setVisible(false);
                successLabel.setManaged(false);
            });
            pause.play();
        });
    }

    private Label createMutedLabel(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        return l;
    }

    private Label createWhiteLabel(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        return l;
    }

    private Label createDarkMutedLabel(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
        return l;
    }

    private Label createDarkLabel(String txt) {
        Label l = new Label(txt);
        l.setStyle("-fx-text-fill: #0f172a; -fx-font-size: 12px; -fx-font-weight: bold;");
        return l;
    }

    public VBox getView() {
        return root;
    }
}