package com.hotel;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.hotel.ui.LoginView;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        com.hotel.data.DataStore.loadRoomsFromFile();
        com.hotel.data.DataStore.loadCustomersFromFile();

        LoginView loginView = new LoginView(stage);

        Scene scene = new Scene(loginView.getView(), 1200, 700);
        try {
            if (getClass().getResource("/style.css") != null) {
                scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Could not load style.css");
        }

        stage.setTitle("Grand Hotel - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}