package com.busreservation.view;

import com.busreservation.model.User;
import com.busreservation.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.function.Consumer;

/**
 * Modern Login and Account Registration Card/View.
 * Ensures user logs in before booking and isolates user-specific bookings.
 */
public class LoginView extends StackPane {

    private final ViewManager viewManager;
    private final AuthService authService;
    private final Runnable onLoginSuccess;

    private boolean isRegisterMode = false;

    private TextField nameField;
    private TextField emailOrPhoneField;
    private TextField registerEmailField;
    private TextField registerPhoneField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    private Label titleLabel;
    private Label subtitleLabel;
    private Button btnSubmit;
    private Button btnSwitchMode;
    private VBox formBox;

    public LoginView(ViewManager viewManager, Runnable onLoginSuccess) {
        this.viewManager = viewManager;
        this.authService = new AuthService();
        this.onLoginSuccess = onLoginSuccess;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: rgba(15, 23, 42, 0.4);"); // Modal backdrop

        buildUI();
    }

    private void buildUI() {
        VBox card = new VBox(18);
        card.getStyleClass().add("card");
        card.setMaxWidth(440);
        card.setMinWidth(400);
        card.setPadding(new Insets(30, 36, 30, 36));

        // Header
        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_RIGHT);
        Button btnClose = new Button("\u2715");
        btnClose.setStyle("-fx-background-color: transparent; -fx-text-fill: #94A3B8; -fx-font-size: 16px; -fx-cursor: hand;");
        btnClose.setOnAction(e -> setVisible(false));
        topRow.getChildren().add(btnClose);

        titleLabel = new Label("Welcome to SwiftBus");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        subtitleLabel = new Label("Please log in to manage bookings and secure your seat.");
        subtitleLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");
        subtitleLabel.setWrapText(true);

        formBox = new VBox(14);
        renderLoginForm();

        btnSubmit = new Button("Sign In \u2192");
        btnSubmit.getStyleClass().add("btn-primary");
        btnSubmit.setMaxWidth(Double.MAX_VALUE);
        btnSubmit.setPrefHeight(42);
        btnSubmit.setOnAction(e -> handleSubmit());

        HBox switchRow = new HBox(6);
        switchRow.setAlignment(Pos.CENTER);
        Label switchPrompt = new Label("Don't have an account?");
        switchPrompt.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        btnSwitchMode = new Button("Create an account");
        btnSwitchMode.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563EB; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 0;");
        btnSwitchMode.setOnAction(e -> toggleMode());

        switchRow.getChildren().addAll(switchPrompt, btnSwitchMode);

        card.getChildren().addAll(topRow, titleLabel, subtitleLabel, formBox, btnSubmit, switchRow);
        getChildren().add(card);
    }

    private void renderLoginForm() {
        formBox.getChildren().clear();

        VBox emailBox = new VBox(4);
        Label l1 = new Label("Email or Mobile Number");
        l1.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        emailOrPhoneField = new TextField();
        emailOrPhoneField.setPromptText("e.g. aarav@example.com or 9876543210");
        emailBox.getChildren().addAll(l1, emailOrPhoneField);

        VBox passBox = new VBox(4);
        Label l2 = new Label("Password");
        l2.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passBox.getChildren().addAll(l2, passwordField);

        // Pre-fill demo credentials hint
        Label hint = new Label("Default demo accounts: aarav@example.com / password123, rajesh@example.com / password123");
        hint.setStyle("-fx-font-size: 10px; -fx-text-fill: #94A3B8;");
        hint.setWrapText(true);

        formBox.getChildren().addAll(emailBox, passBox, hint);
    }

    private void renderRegisterForm() {
        formBox.getChildren().clear();

        VBox nameBox = new VBox(4);
        Label l0 = new Label("Full Legal Name");
        l0.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        nameField = new TextField();
        nameField.setPromptText("e.g. Rahul Verma");
        nameBox.getChildren().addAll(l0, nameField);

        VBox emailBox = new VBox(4);
        Label l1 = new Label("Email Address");
        l1.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        registerEmailField = new TextField();
        registerEmailField.setPromptText("e.g. rahul@example.com");
        emailBox.getChildren().addAll(l1, registerEmailField);

        VBox phoneBox = new VBox(4);
        Label l2 = new Label("10-Digit Mobile Number");
        l2.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        registerPhoneField = new TextField();
        registerPhoneField.setPromptText("e.g. 9876543210");
        phoneBox.getChildren().addAll(l2, registerPhoneField);

        VBox passBox = new VBox(4);
        Label l3 = new Label("Choose Password");
        l3.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Min 4 characters");
        passBox.getChildren().addAll(l3, confirmPasswordField);

        formBox.getChildren().addAll(nameBox, emailBox, phoneBox, passBox);
    }

    private void toggleMode() {
        isRegisterMode = !isRegisterMode;
        if (isRegisterMode) {
            titleLabel.setText("Create New Account");
            subtitleLabel.setText("Register to book seats and track all your personal tickets.");
            btnSubmit.setText("Register & Sign In \u2192");
            btnSwitchMode.setText("Sign In instead");
            renderRegisterForm();
        } else {
            titleLabel.setText("Welcome Back");
            subtitleLabel.setText("Sign in to your account to continue.");
            btnSubmit.setText("Sign In \u2192");
            btnSwitchMode.setText("Create an account");
            renderLoginForm();
        }
    }

    private void handleSubmit() {
        try {
            if (isRegisterMode) {
                String name = nameField.getText();
                String email = registerEmailField.getText();
                String phone = registerPhoneField.getText();
                String pass = confirmPasswordField.getText();

                User user = authService.register(name, email, phone, pass);
                viewManager.showToast("Account created successfully! Welcome, " + user.getFullName(), false);
                setVisible(false);
                if (onLoginSuccess != null) onLoginSuccess.run();

            } else {
                String emailOrPhone = emailOrPhoneField.getText();
                String pass = passwordField.getText();

                User user = authService.login(emailOrPhone, pass);
                if (user != null) {
                    viewManager.showToast("Logged in successfully as " + user.getFullName(), false);
                    setVisible(false);
                    if (onLoginSuccess != null) onLoginSuccess.run();
                } else {
                    viewManager.showToast("Invalid credentials. Please check your email/mobile and password.", true);
                }
            }
        } catch (Exception ex) {
            viewManager.showToast(ex.getMessage(), true);
        }
    }
}
