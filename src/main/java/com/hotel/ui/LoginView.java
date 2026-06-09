package com.hotel.ui;

import com.hotel.model.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;

public class LoginView {

    private HBox root;
    private Stage stage;

    private TextField emailField;
    private ComboBox<String> roleCombo;
    private PasswordField passField;
    private CheckBox rememberMe;

    private Label emailError;
    private Label roleError;
    private Label passError;

    private Label formStatusLabel;

    private Button chipManager;
    private Button chipDesk;
    private Button chipStaff;

    private Preferences prefs;

    public LoginView(Stage stage) {
        this.stage = stage;
        prefs = Preferences.userNodeForPackage(LoginView.class);

        root = new HBox();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setPrefSize(1200, 700);

        VBox leftPanel = createLeftPanel();
        HBox.setHgrow(leftPanel, Priority.ALWAYS);

        VBox rightPanel = createRightPanel();
        HBox.setHgrow(rightPanel, Priority.ALWAYS);

        leftPanel.setPrefWidth(600);
        rightPanel.setPrefWidth(600);

        root.getChildren().addAll(leftPanel, rightPanel);

        loadPreferences();
    }

    private VBox createLeftPanel() {
        VBox panel = new VBox(30);
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.setPadding(new Insets(50, 80, 50, 80));

        Stop[] stops = new Stop[] { new Stop(0, Color.web("#0f172a")), new Stop(1, Color.web("#1e293b")) };
        LinearGradient bgGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops);
        panel.setBackground(new Background(new BackgroundFill(bgGradient, CornerRadii.EMPTY, Insets.EMPTY)));

        Label logoH = new Label("H");
        logoH.setMinSize(100, 100);
        logoH.setMaxSize(100, 100);
        logoH.setStyle(
                "-fx-background-color: #facc15; -fx-background-radius: 20; -fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        logoH.setAlignment(Pos.CENTER);

        VBox titleBox = new VBox(5);
        Label title = new Label("GRAND HOTEL");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label subtitle = new Label("MANAGEMENT SYSTEM");
        subtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #9ca3af; -fx-letter-spacing: 2px;");
        titleBox.getChildren().addAll(title, subtitle);

        HBox topLogoBox = new HBox(20);
        topLogoBox.setAlignment(Pos.CENTER_LEFT);
        topLogoBox.getChildren().addAll(logoH, titleBox);

        Region spacer = new Region();
        spacer.setMinHeight(40);

        Label staffPortalLabel = new Label("Staff Portal");
        staffPortalLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descriptionText = new Label(
                "Access and manage hotel operations efficiently with our comprehensive toolset designed for hotel staff.");
        descriptionText.setStyle("-fx-font-size: 14px; -fx-text-fill: #9ca3af; -fx-wrap-text: true;");
        descriptionText.setMaxWidth(400);

        VBox featuresBox = new VBox(15);
        featuresBox.getChildren().addAll(
                createFeatureCard("🛏", "Room Management", "View and update room statuses in real-time."),
                createFeatureCard("📅", "Bookings & Checkout", "Handle guest reservations and quick checkouts."),
                createFeatureCard("📊", "Live Dashboard", "Monitor daily operations with live metrics."));

        panel.getChildren().addAll(topLogoBox, spacer, staffPortalLabel, descriptionText, featuresBox);
        return panel;
    }

    private HBox createFeatureCard(String iconStr, String titleStr, String descStr) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15, 20, 15, 20));
        card.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 12;");

        Label icon = new Label(iconStr);
        icon.setStyle("-fx-font-size: 24px; -fx-text-fill: #facc15;");

        VBox textBox = new VBox(5);
        Label title = new Label(titleStr);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label desc = new Label(descStr);
        desc.setStyle("-fx-font-size: 12px; -fx-text-fill: #9ca3af;");
        textBox.getChildren().addAll(title, desc);

        card.getChildren().addAll(icon, textBox);
        return card;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(20);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(30, 100, 30, 100));
        panel.setStyle("-fx-background-color: #0f172a;");

        HBox topBar = new HBox();
        topBar.setAlignment(Pos.TOP_RIGHT);
        Label clockLbl = new Label();
        clockLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #facc15;");
        Timeline clockTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            clockLbl.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
        }));
        clockTimer.setCycleCount(Animation.INDEFINITE);
        clockTimer.play();
        clockLbl.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
        topBar.getChildren().add(clockLbl);

        VBox loginBox = new VBox(20);
        loginBox.setAlignment(Pos.CENTER_LEFT);
        loginBox.setMaxWidth(400);

        VBox titleBox = new VBox(5);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label welcomeTitle = new Label("Welcome back");
        welcomeTitle.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label welcomeSubtitle = new Label("Sign in to your staff account to continue");
        welcomeSubtitle.setStyle("-fx-font-size: 16px; -fx-text-fill: #9ca3af;");
        titleBox.getChildren().addAll(welcomeTitle, welcomeSubtitle);

        HBox quickSelect = new HBox(10);
        chipManager = createChipButton("MG", "Manager");
        chipDesk = createChipButton("FD", "Front Desk");
        chipStaff = createChipButton("ST", "Staff");

        chipManager
                .setOnAction(e -> applyChipSelection(chipManager, "Manager", "manager@grandhotel.com", "manager123"));

        chipDesk.setOnAction(e -> applyChipSelection(chipDesk, "Front Desk", "desk@grandhotel.com", "desk123"));

        chipStaff.setOnAction(e -> applyChipSelection(chipStaff, "Staff", "staff@grandhotel.com", "staff123"));

        quickSelect.getChildren().addAll(chipManager, chipDesk, chipStaff);

        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle(getFieldStyle(false));
        emailError = createErrorLabel();
        VBox emailBox = new VBox(5, createFieldLabel("Email Address"), emailField, emailError);

        roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Manager", "Front Desk", "Staff");
        roleCombo.setMaxWidth(Double.MAX_VALUE);
        roleCombo.setStyle(getFieldStyle(false));
        roleError = createErrorLabel();
        VBox roleBox = new VBox(5, createFieldLabel("Role"), roleCombo, roleError);

        passField = new PasswordField();
        passField.setPromptText("Enter your password");
        passField.setStyle(getFieldStyle(false));
        passError = createErrorLabel();
        VBox passBox = new VBox(5, createFieldLabel("Password"), passField, passError);

        emailField.textProperty().addListener((o, old, nw) -> {
            emailError.setText("");
            emailField.setStyle(getFieldStyle(false));
        });
        roleCombo.valueProperty().addListener((o, old, nw) -> {
            roleError.setText("");
            roleCombo.setStyle(getFieldStyle(false));
        });
        passField.textProperty().addListener((o, old, nw) -> {
            passError.setText("");
            passField.setStyle(getFieldStyle(false));
        });

        VBox formBox = new VBox(10, emailBox, roleBox, passBox);

        HBox optionsBox = new HBox();
        optionsBox.setAlignment(Pos.CENTER_LEFT);
        rememberMe = new CheckBox("Remember me");
        rememberMe.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 14px;");
        Region opSpacer = new Region();
        HBox.setHgrow(opSpacer, Priority.ALWAYS);
        Label forgotPass = new Label("Forgot password?");
        forgotPass.setStyle("-fx-text-fill: #facc15; -fx-font-size: 14px; -fx-cursor: hand;");
        optionsBox.getChildren().addAll(rememberMe, opSpacer, forgotPass);

        Button signInBtn = new Button("Sign In to Dashboard");
        signInBtn.setMaxWidth(Double.MAX_VALUE);
        signInBtn.setStyle(
                "-fx-background-color: #facc15; -fx-text-fill: #0f172a; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 15; -fx-background-radius: 8; -fx-cursor: hand;");

        formStatusLabel = new Label();
        formStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        formStatusLabel.setMaxWidth(Double.MAX_VALUE);
        formStatusLabel.setAlignment(Pos.CENTER);
        formStatusLabel.setPadding(new Insets(5));

        VBox actionBox = new VBox(10, signInBtn, formStatusLabel);

        signInBtn.setOnAction(e -> attemptLogin(signInBtn));
        passField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                attemptLogin(signInBtn);
            }
        });

        VBox bottomBox = new VBox(15);
        bottomBox.setAlignment(Pos.CENTER);

        HBox dividerBox = new HBox(10);
        dividerBox.setAlignment(Pos.CENTER);
        Separator sep1 = new Separator();
        sep1.setPrefWidth(100);
        Label orLbl = new Label("or quick login as");
        orLbl.setStyle("-fx-text-fill: #64748b;");
        Separator sep2 = new Separator();
        sep2.setPrefWidth(100);
        dividerBox.getChildren().addAll(sep1, orLbl, sep2);

        HBox altLoginBox = new HBox(15);
        altLoginBox.setAlignment(Pos.CENTER);
        Button altManager = createAltLoginBtn("Manager");
        Button altDesk = createAltLoginBtn("Front Desk");

        altManager.setOnAction(e -> doQuickLogin("Manager", "manager@grandhotel.com", "manager123", signInBtn));
        altDesk.setOnAction(e -> doQuickLogin("Front Desk", "desk@grandhotel.com", "desk123", signInBtn));

        altLoginBox.getChildren().addAll(altManager, altDesk);
        bottomBox.getChildren().addAll(dividerBox, altLoginBox);

        loginBox.getChildren().addAll(titleBox, quickSelect, formBox, optionsBox, actionBox, bottomBox);

        VBox outerRight = new VBox();
        outerRight.setAlignment(Pos.CENTER);
        VBox.setVgrow(loginBox, Priority.ALWAYS);
        outerRight.getChildren().add(loginBox);

        Region btmSpacer = new Region();
        VBox.setVgrow(btmSpacer, Priority.ALWAYS);
        Label footer = new Label("Grand Hotel Management System v1.0");
        footer.setStyle("-fx-text-fill: #475569; -fx-font-size: 12px;");

        panel.getChildren().addAll(topBar, btmSpacer, outerRight, footer);

        applyChipSelection(chipManager, "Manager", "", "");

        return panel;
    }

    private Button createChipButton(String initials, String tooltipText) {
        Button btn = new Button(initials);
        btn.setStyle(getChipStyle(false));
        Tooltip t = new Tooltip(tooltipText);
        btn.setTooltip(t);
        return btn;
    }

    private void applyChipSelection(Button selectedBtn, String role, String emailHint, String password) {

        chipManager.setStyle(getChipStyle(false));
        chipDesk.setStyle(getChipStyle(false));
        chipStaff.setStyle(getChipStyle(false));

        selectedBtn.setStyle(getChipStyle(true));

        roleCombo.setValue(role);

        if (!emailHint.isEmpty()) {
            emailField.setText(emailHint);
        }

        if (password != null && !password.isEmpty()) {
            passField.setText(password);
        }

        passField.requestFocus();
    }

    private String getChipStyle(boolean active) {
        if (active)
            return "-fx-background-color: rgba(250, 204, 21, 0.2); -fx-text-fill: #facc15; -fx-border-color: #facc15; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;";
        return "-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-border-color: #334155; -fx-border-radius: 20; -fx-background-radius: 20; -fx-padding: 8 16; -fx-font-weight: bold; -fx-cursor: hand;";
    }

    private String getFieldStyle(boolean hasError) {
        String base = "-fx-background-color: #1e293b; -fx-text-fill: white; -fx-prompt-text-fill: #64748b; -fx-border-radius: 8; -fx-background-radius: 8; -fx-padding: 10;";
        if (hasError)
            return base + " -fx-border-color: #ef4444; -fx-border-width: 2;";
        return base + " -fx-border-color: #334155; -fx-border-width: 1;";
    }

    private Label createFieldLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 13px; -fx-font-weight: bold;");
        return l;
    }

    private Label createErrorLabel() {
        Label l = new Label("");
        l.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 12px;");
        return l;
    }

    private Button createAltLoginBtn(String text) {
        Button btn = new Button(text);
        btn.setStyle(
                "-fx-background-color: transparent; -fx-border-color: #334155; -fx-text-fill: #cbd5e1; -fx-border-radius: 8; -fx-padding: 6 15; -fx-cursor: hand;");
        return btn;
    }

    private void attemptLogin(Button signInBtn) {

        emailError.setText("");
        emailField.setStyle(getFieldStyle(false));
        roleError.setText("");
        roleCombo.setStyle(getFieldStyle(false));
        passError.setText("");
        passField.setStyle(getFieldStyle(false));
        formStatusLabel.setText("");

        boolean hasError = false;

        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            emailError.setText("Email address is required");
            emailField.setStyle(getFieldStyle(true));
            hasError = true;
        } else if (!email.contains("@")) {
            emailError.setText("Enter a valid email address");
            emailField.setStyle(getFieldStyle(true));
            hasError = true;
        }

        String role = roleCombo.getValue();
        if (role == null || role.isEmpty()) {
            roleError.setText("Role must be selected");
            roleCombo.setStyle(getFieldStyle(true));
            hasError = true;
        }

        String pass = passField.getText();
        if (pass.isEmpty()) {
            passError.setText("Password is required");
            passField.setStyle(getFieldStyle(true));
            hasError = true;
        } else if (pass.length() < 6) {
            passError.setText("Password too short");
            passField.setStyle(getFieldStyle(true));
            hasError = true;
        }

        if (hasError)
            return;

        String expectedPass = checkCredentials(email, role);
        if (expectedPass == null || !expectedPass.equals(pass)) {
            passError.setText("Incorrect password or unmatched role/email");
            passField.setStyle(getFieldStyle(true));
            return;
        }

        if (rememberMe.isSelected()) {
            prefs.put("saved_email", email);
            prefs.put("saved_role", role);
            prefs.putBoolean("remember", true);
        } else {
            prefs.remove("saved_email");
            prefs.remove("saved_role");
            prefs.putBoolean("remember", false);
        }

        Session.staffEmail = email;
        Session.staffRole = role;
        Session.staffName = deriveName(email);

        signInBtn.setDisable(true);
        formStatusLabel.setText("✓ Login successful! Redirecting...");
        formStatusLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 14px; -fx-font-weight: bold;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(e -> {
            MainApp app = new MainApp();
            Scene dashboardScene = new Scene(app.getRoot(), 1200, 700);
            try {
                if (getClass().getResource("/style.css") != null) {
                    dashboardScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
                }
            } catch (Exception ex) {
            }
            Stage window = (Stage) signInBtn.getScene().getWindow();
            window.setTitle("Grand Hotel System - Dashboard");
            window.setScene(dashboardScene);
        });
        pause.play();
    }

    private void doQuickLogin(String role, String email, String pass, Button signInBtn) {
        roleCombo.setValue(role);
        emailField.setText(email);
        passField.setText(pass);
        attemptLogin(signInBtn);
    }

    private String checkCredentials(String email, String role) {
        if ("Manager".equalsIgnoreCase(role) && "manager@grandhotel.com".equals(email))
            return "manager123";
        if ("Front Desk".equalsIgnoreCase(role) && "desk@grandhotel.com".equals(email))
            return "desk123";
        if ("Staff".equalsIgnoreCase(role) && "staff@grandhotel.com".equals(email))
            return "staff123";
        return null;
    }

    private String deriveName(String email) {
        String base = email.split("@")[0];
        return base.substring(0, 1).toUpperCase() + base.substring(1);
    }

    private void loadPreferences() {
        boolean rem = prefs.getBoolean("remember", false);
        rememberMe.setSelected(rem);
        if (rem) {
            String savedEmail = prefs.get("saved_email", "");
            String savedRole = prefs.get("saved_role", "");
            if (!savedEmail.isEmpty())
                emailField.setText(savedEmail);
            if (!savedRole.isEmpty())
                roleCombo.setValue(savedRole);

            if ("Manager".equals(savedRole))
                applyChipSelection(chipManager, savedRole, savedEmail, "manager123");

            else if ("Front Desk".equals(savedRole))
                applyChipSelection(chipDesk, savedRole, savedEmail, "desk123");

            else if ("Staff".equals(savedRole))
                applyChipSelection(chipStaff, savedRole, savedEmail, "staff123");
        }
    }

    public HBox getView() {
        return root;
    }
}
