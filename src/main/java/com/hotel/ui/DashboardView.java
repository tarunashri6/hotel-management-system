package com.hotel.ui;

import com.hotel.data.DataStore;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView {

    private ScrollPane rootScroll;
    private VBox root;

    private Label lblTotalRooms = new Label("0");
    private Label lblAvailable = new Label("0");
    private Label lblOccupied = new Label("0");
    private Label lblRevenue = new Label("₹0");
    private Label lblCheckoutsDue = new Label("0");

    private FlowPane roomGrid = new FlowPane(15, 15);
    private VBox activeGuestsBox = new VBox(10);
    private VBox revenueBreakdownBox = new VBox(15);

    private Label lblOccupancyPct = new Label("0%");
    private Label lblAvailStatus = new Label("0 Available");
    private Label lblOccStatus = new Label("0 Occupied");

    public DashboardView() {
        root = new VBox(25);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #0f172a;");

        root.getChildren().add(createTopNav());

        HBox statsRow = new HBox(20);
        statsRow.getChildren().addAll(
                createStatCard("Total Rooms", lblTotalRooms, "#3b82f6"),
                createStatCard("Available", lblAvailable, "#22c55e"),
                createStatCard("Occupied", lblOccupied, "#ef4444"),
                createStatCard("Today Revenue", lblRevenue, "#facc15"),
                createStatCard("Checkouts Due", lblCheckoutsDue, "#a855f7"));
        for (Node n : statsRow.getChildren()) {
            HBox.setHgrow(n, Priority.ALWAYS);
        }
        root.getChildren().add(statsRow);

        HBox mainContent = new HBox(25);
        VBox leftCol = new VBox(25);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
        VBox rightCol = new VBox(25);
        rightCol.setPrefWidth(350);

        leftCol.getChildren().addAll(
                createSectionCard("Room Status Grid", roomGrid),
                createSectionCard("Active Guests", activeGuestsBox));

        rightCol.getChildren().addAll(
                createQuickActions(),
                createSidebarStatus(),
                createSectionCard("Revenue Breakdown", revenueBreakdownBox),
                createActivityFeed());

        mainContent.getChildren().addAll(leftCol, rightCol);
        root.getChildren().add(mainContent);

        setupListeners();
        updateDashboard();

        rootScroll = new ScrollPane(root);
        rootScroll.setFitToWidth(true);
        rootScroll.setStyle("-fx-background: #0f172a; -fx-background-color: #0f172a; -fx-border-color: transparent;");
    }

    private void setupListeners() {
        DataStore.getRooms().addListener((ListChangeListener<Room>) c -> updateDashboard());
        DataStore.getCustomers().addListener((ListChangeListener<Customer>) c -> updateDashboard());
    }

    private void updateDashboard() {
        ObservableList<Room> rooms = DataStore.getRooms();
        ObservableList<Customer> customers = DataStore.getCustomers();

        int totalRooms = rooms.size();
        int available = (int) rooms.stream().filter(r -> "Available".equals(r.getStatus())).count();
        int occupied = (int) rooms.stream().filter(r -> "Occupied".equals(r.getStatus())).count();

        int checkoutsDue = 0;
        double currentRevenue = 0;
        double revSingle = 0, revDouble = 0, revDeluxe = 0;

        activeGuestsBox.getChildren().clear();

        for (Customer c : customers) {
            if ("Active".equals(c.getStatus()) || (c.getRoom() != null && "Occupied".equals(c.getRoom().getStatus()))) {
                double total = 0;
                if (c.getRoom() != null) {
                    total = c.getRoom().getPrice() * c.getDays();
                    currentRevenue += total;

                    if ("Single".equalsIgnoreCase(c.getRoom().getType()))
                        revSingle += total;
                    else if ("Double".equalsIgnoreCase(c.getRoom().getType()))
                        revDouble += total;
                    else
                        revDeluxe += total;
                }

                if (activeGuestsBox.getChildren().size() < 6 && c.getRoom() != null) {
                    HBox row = new HBox(15);
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setStyle("-fx-background-color: #334155; -fx-padding: 12; -fx-background-radius: 8;");

                    Circle avatar = new Circle(15, Color.web("#facc15"));
                    VBox nameRoom = new VBox(4,
                            createLabel(c.getName(), "#ffffff", 14, true),
                            createLabel("Room " + c.getRoom().getRoomNumber() + " • " + c.getRoom().getType(),
                                    "#9ca3af", 11, false));
                    Region sp = new Region();
                    HBox.setHgrow(sp, Priority.ALWAYS);
                    VBox daysPrice = new VBox(4,
                            createLabel("₹" + total, "#facc15", 14, true),
                            createLabel(c.getDays() + " Days", "#9ca3af", 11, false));
                    daysPrice.setAlignment(Pos.CENTER_RIGHT);
                    row.getChildren().addAll(avatar, nameRoom, sp, daysPrice);
                    activeGuestsBox.getChildren().add(row);
                }

                try {
                    LocalDate ci;
                    try {
                        ci = LocalDate.parse(c.getCheckInDate());
                    } catch (Exception ex) {
                        ci = LocalDate.parse(c.getCheckInDate(), DateTimeFormatter.ofPattern("M/d/yyyy"));
                    }
                    LocalDate co = ci.plusDays(c.getDays());
                    if (co.equals(LocalDate.now())) {
                        checkoutsDue++;
                    }
                } catch (Exception ignored) {
                }
            }
        }

        if (activeGuestsBox.getChildren().isEmpty()) {
            activeGuestsBox.getChildren().add(createLabel("No active guests staying currently.", "#64748b", 13, false));
        }

        lblTotalRooms.setText(String.valueOf(totalRooms));
        lblAvailable.setText(String.valueOf(available));
        lblOccupied.setText(String.valueOf(occupied));
        lblRevenue.setText("₹" + currentRevenue);
        lblCheckoutsDue.setText(String.valueOf(checkoutsDue));

        int occPct = totalRooms == 0 ? 0 : (int) ((occupied * 100.0) / totalRooms);
        lblOccupancyPct.setText(occPct + "%");
        lblAvailStatus.setText(available + " Available");
        lblOccStatus.setText(occupied + " Occupied");

        roomGrid.getChildren().clear();
        Map<Integer, List<Room>> grouped = rooms.stream().collect(Collectors.groupingBy(Room::getFloor));
        for (Integer floor : grouped.keySet().stream().sorted().collect(Collectors.toList())) {
            VBox floorBox = new VBox(8);
            Label flbl = createLabel("Floor " + floor, "#9ca3af", 12, true);
            FlowPane fp = new FlowPane(10, 10);
            for (Room r : grouped.get(floor)) {
                Button btn = new Button(String.valueOf(r.getRoomNumber()));
                btn.setPrefWidth(60);
                btn.setPrefHeight(45);
                String col = "#22c55e";
                if ("Occupied".equals(r.getStatus()))
                    col = "#ef4444";
                if ("Maintenance".equals(r.getStatus()))
                    col = "#eab308";
                btn.setStyle("-fx-background-color: " + col + "30; -fx-border-color: " + col
                        + "; -fx-border-radius: 5; -fx-background-radius: 5; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
                btn.setOnAction(e -> navigateToTab(btn, "Room Management"));
                fp.getChildren().add(btn);
            }
            floorBox.getChildren().addAll(flbl, fp);
            roomGrid.getChildren().add(floorBox);
        }

        revenueBreakdownBox.getChildren().clear();
        double maxRev = Math.max(1, Math.max(revSingle, Math.max(revDouble, revDeluxe)));
        revenueBreakdownBox.getChildren().addAll(
                createRevBar("Single Rooms", revSingle, maxRev, "#3b82f6"),
                createRevBar("Double Rooms", revDouble, maxRev, "#a855f7"),
                createRevBar("Deluxe Rooms", revDeluxe, maxRev, "#facc15"));
    }

    private VBox createRevBar(String title, double val, double max, String color) {
        VBox box = new VBox(5);
        HBox top = new HBox(createLabel(title, "#cbd5e1", 13, false), new Region() {
            {
                HBox.setHgrow(this, Priority.ALWAYS);
            }
        }, createLabel("₹" + val, "#ffffff", 13, true));
        StackPane barBg = new StackPane();
        barBg.setStyle("-fx-background-color: #334155; -fx-background-radius: 4;");
        barBg.setPrefHeight(8);
        barBg.setAlignment(Pos.CENTER_LEFT);
        Region bar = new Region();
        bar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 4;");
        bar.setPrefHeight(8);
        bar.maxWidthProperty().bind(barBg.widthProperty().multiply(val / max));
        barBg.getChildren().add(bar);
        box.getChildren().addAll(top, barBg);
        return box;
    }

    private String getGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour >= 5 && hour < 12)
            return "Good morning";
        if (hour >= 12 && hour < 17)
            return "Good afternoon";
        if (hour >= 17 && hour < 21)
            return "Good evening";
        return "Good night";
    }

    private HBox createTopNav() {
        HBox nav = new HBox(20);
        nav.setAlignment(Pos.CENTER_LEFT);
        Label title = createLabel("Dashboard — " + getGreeting() + ", Manager", "#ffffff", 24, true);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label clock = createLabel(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")), "#facc15", 18,
                true);
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(1),
                e -> clock.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")))));
        t.setCycleCount(Animation.INDEFINITE);
        t.play();
        nav.getChildren().addAll(title, sp, clock);
        return nav;
    }

    private HBox createStatCard(String title, Label valLbl, String color) {
        valLbl.setStyle("-fx-text-fill: white; -fx-font-size: 28px; -fx-font-weight: bold;");
        VBox box = new VBox(5, createLabel(title, color, 14, true), valLbl);
        box.setPadding(new Insets(20));
        box.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #334155; -fx-border-radius: 12; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);");
        HBox container = new HBox(box);
        HBox.setHgrow(container, Priority.ALWAYS);
        HBox.setHgrow(box, Priority.ALWAYS);
        return container;
    }

    private VBox createSectionCard(String title, Node content) {
        VBox card = new VBox(15, createLabel(title, "#ffffff", 18, true), new Separator(), content);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        return card;
    }

    private VBox createQuickActions() {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");
        card.getChildren().addAll(createLabel("Quick Actions", "#ffffff", 16, true), new Separator());

        String[] labels = { "Add Room", "New Booking", "Register Guest", "Checkout", "Analytics" };
        String[] targets = { "Room Management", "Bookings", "Customers", "Checkout", "Reports" };
        String[] colors = { "#3b82f6", "#22c55e", "#a855f7", "#ef4444", "#facc15" };

        for (int i = 0; i < labels.length; i++) {
            Button b = new Button(labels[i]);
            b.setMaxWidth(Double.MAX_VALUE);
            b.setStyle("-fx-background-color: " + colors[i] + "20; -fx-text-fill: " + colors[i]
                    + "; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 10; -fx-background-radius: 8; -fx-cursor: hand;");
            int finalI = i;
            b.setOnAction(e -> navigateToTab(b, targets[finalI]));
            card.getChildren().add(b);
        }
        return card;
    }

    private VBox createSidebarStatus() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: #1e293b; -fx-background-radius: 12; -fx-border-color: #facc15; -fx-border-width: 2; -fx-border-radius: 12;");

        lblOccupancyPct.setStyle("-fx-text-fill: #facc15; -fx-font-size: 36px; -fx-font-weight: bold;");
        VBox occBox = new VBox(0, createLabel("Total Occupancy", "#cbd5e1", 14, false), lblOccupancyPct);
        occBox.setAlignment(Pos.CENTER);

        HBox split = new HBox(20);
        split.setAlignment(Pos.CENTER);
        lblAvailStatus.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: bold; -fx-font-size: 14px;");
        lblOccStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold; -fx-font-size: 14px;");
        split.getChildren().addAll(lblAvailStatus, createLabel("|", "#64748b", 14, false), lblOccStatus);

        card.getChildren().addAll(occBox, split);
        return card;
    }

    private VBox createActivityFeed() {
        VBox box = new VBox(12);
        ObservableList<String> mockLogs = FXCollections.observableArrayList(
                "+ Booking confirmed for John Doe",
                "✓ Checkout completed for Emily Clark",
                "★ Room 102 added to system",
                "✓ Checkout completed for Guest XYZ");
        for (String log : mockLogs) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            Circle dot = new Circle(4, log.startsWith("+") ? Color.web("#22c55e")
                    : (log.startsWith("✓") ? Color.web("#facc15") : Color.web("#3b82f6")));
            row.getChildren().addAll(dot, createLabel(log, "#cbd5e1", 13, false));
            box.getChildren().add(row);
        }
        return createSectionCard("Recent Activity", box);
    }

    private Label createLabel(String text, String color, int size, boolean bold) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "px;"
                + (bold ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    private void navigateToTab(Node source, String tabName) {
        if (source.getScene() != null) {
            TabPane tabPane = (TabPane) source.getScene().lookup(".tab-pane");
            if (tabPane != null) {
                for (Tab t : tabPane.getTabs()) {
                    if (tabName.equals(t.getText())) {
                        tabPane.getSelectionModel().select(t);
                        break;
                    }
                }
            }
        }
    }

    public ScrollPane getView() {
        return rootScroll;
    }
}
