package com.hotel.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.time.LocalDate;

public class MainApp {

    private VBox root;

    public MainApp() {
        root = new VBox();

        TabPane tabPane = new TabPane();
        tabPane.setTabMinHeight(0);
        tabPane.setTabMaxHeight(0);

        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #0f172a;");

        Label logo = new Label("GRAND HOTEL");
        logo.setStyle("-fx-text-fill: #facc15; -fx-font-size: 18px; -fx-font-weight: bold;");

        Button dashboardBtn = new Button("Dashboard");
        Button roomBtn = new Button("Room Management");
        Button customerBtn = new Button("Customers");
        Button bookingBtn = new Button("Bookings");
        Button checkoutBtn = new Button("Checkout");
        Button reportsBtn = new Button("Reports");

        String navStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-cursor: hand;";
        dashboardBtn.setStyle(navStyle);
        roomBtn.setStyle(navStyle);
        customerBtn.setStyle(navStyle);
        bookingBtn.setStyle(navStyle);
        checkoutBtn.setStyle(navStyle);
        reportsBtn.setStyle(navStyle);

        dashboardBtn.setOnAction(e -> tabPane.getSelectionModel().select(0));
        roomBtn.setOnAction(e -> tabPane.getSelectionModel().select(1));
        customerBtn.setOnAction(e -> tabPane.getSelectionModel().select(2));
        bookingBtn.setOnAction(e -> tabPane.getSelectionModel().select(3));
        checkoutBtn.setOnAction(e -> tabPane.getSelectionModel().select(4));
        reportsBtn.setOnAction(e -> tabPane.getSelectionModel().select(5));

        Label dateLabel = new Label("Front Desk · " + LocalDate.now());
        dateLabel.setStyle("-fx-text-fill: #9ca3af;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logo, dashboardBtn, roomBtn, customerBtn, bookingBtn, checkoutBtn, reportsBtn,
                spacer, dateLabel);

        DashboardView dashboardView = new DashboardView();
        Tab dashboardTab = new Tab("Dashboard", dashboardView.getView());

        Tab roomTab = new Tab("Room Management");
        roomTab.setContent(new RoomView().getView());
        roomTab.setClosable(false);

        CustomerView customerView = new CustomerView();
        Tab customerTab = new Tab("Customers", customerView.getView());

        BookingView bookingView = new BookingView();
        Tab bookingTab = new Tab("Bookings", bookingView.getView());

        CheckoutView checkoutView = new CheckoutView();
        Tab checkoutTab = new Tab("Checkout", checkoutView.getView());

        ReportsView reportsView = new ReportsView();
        Tab reportsTab = new Tab("Reports", reportsView.getView());

        tabPane.getTabs().addAll(dashboardTab, roomTab, customerTab, bookingTab, checkoutTab, reportsTab);

        tabPane.getSelectionModel().select(dashboardTab);

        VBox.setVgrow(tabPane, Priority.ALWAYS);
        root.getChildren().addAll(header, tabPane);
    }

    public VBox getRoot() {
        return root;
    }
}