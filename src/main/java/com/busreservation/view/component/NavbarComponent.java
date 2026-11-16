package com.busreservation.view.component;

import com.busreservation.config.AppConfig;
import com.busreservation.model.User;
import com.busreservation.service.AuthService;
import com.busreservation.view.ViewManager;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Top Global Navigation Bar component with User Session controls.
 */
public class NavbarComponent extends HBox {

    private final ViewManager viewManager;
    private final Button btnDashboard;
    private final Button btnSearch;
    private final Button btnMyBookings;
    private final HBox userBox;

    public NavbarComponent(ViewManager viewManager) {
        this.viewManager = viewManager;

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(18);
        getStyleClass().add("navbar");

        // Brand Logo & Title
        HBox brandBox = new HBox(10);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Circle logoIcon = new Circle(14, Color.web("#2563EB"));
        Label brandName = new Label("SwiftBus");
        brandName.getStyleClass().add("brand-title");

        Label badge = new Label("MCA Mini-Project");
        badge.getStyleClass().add("brand-badge");

        brandBox.getChildren().addAll(logoIcon, brandName, badge);

        // Navigation links
        btnDashboard = new Button("Dashboard");
        btnDashboard.getStyleClass().add("nav-button");
        btnDashboard.setOnAction(e -> viewManager.showDashboard());

        btnSearch = new Button("Search Buses");
        btnSearch.getStyleClass().add("nav-button");
        btnSearch.setOnAction(e -> viewManager.showBusSearch(null, null, null));

        btnMyBookings = new Button("My Bookings");
        btnMyBookings.getStyleClass().add("nav-button");
        btnMyBookings.setOnAction(e -> viewManager.showMyBookings());

        HBox navLinks = new HBox(6, btnDashboard, btnSearch, btnMyBookings);
        navLinks.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // User Auth Box
        userBox = new HBox(10);
        userBox.setAlignment(Pos.CENTER_RIGHT);
        updateUserAuthUI();

        // Database Mode Status Indicator
        boolean isFallback = AppConfig.isInMemoryFallback();
        Circle statusDot = new Circle(4, isFallback ? Color.web("#F59E0B") : Color.web("#10B981"));
        Label statusLabel = new Label(isFallback ? "Mode: Standalone In-Memory" : "Mode: PostgreSQL Connected");
        statusLabel.setStyle(String.format(
                "-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: %s; -fx-background-color: %s; -fx-padding: 4px 10px; -fx-background-radius: 20px;",
                isFallback ? "#B45309" : "#065F46",
                isFallback ? "#FEF3C7" : "#D1FAE5"
        ));
        HBox statusBox = new HBox(6, statusDot, statusLabel);
        statusBox.setAlignment(Pos.CENTER_RIGHT);

        getChildren().addAll(brandBox, navLinks, spacer, userBox, statusBox);
    }

    public void updateUserAuthUI() {
        userBox.getChildren().clear();

        if (AuthService.isLoggedIn()) {
            User user = AuthService.getCurrentUser();

            Label userIcon = new Label("\uD83D\uDC64");
            userIcon.setStyle("-fx-font-size: 14px;");

            Label nameLabel = new Label(user.getFullName());
            nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

            Button btnLogout = new Button("Logout");
            btnLogout.getStyleClass().add("btn-secondary");
            btnLogout.setStyle("-fx-font-size: 11px; -fx-padding: 4px 10px; -fx-border-color: #CBD5E1;");
            btnLogout.setOnAction(e -> {
                new AuthService().logout();
                viewManager.showToast("Logged out successfully.", false);
                updateUserAuthUI();
                viewManager.showDashboard();
            });

            HBox profilePill = new HBox(6, userIcon, nameLabel);
            profilePill.setAlignment(Pos.CENTER);
            profilePill.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 4px 10px; -fx-background-radius: 20px;");

            userBox.getChildren().addAll(profilePill, btnLogout);

        } else {
            Button btnLogin = new Button("\uD83D\uDC64 Login / Register");
            btnLogin.getStyleClass().add("btn-secondary");
            btnLogin.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 6px 14px;");
            btnLogin.setOnAction(e -> viewManager.showLoginModal(null));

            userBox.getChildren().add(btnLogin);
        }
    }

    public void setActiveTab(String tabName) {
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnSearch.getStyleClass().remove("nav-button-active");
        btnMyBookings.getStyleClass().remove("nav-button-active");

        if ("dashboard".equalsIgnoreCase(tabName)) {
            btnDashboard.getStyleClass().add("nav-button-active");
        } else if ("search".equalsIgnoreCase(tabName)) {
            btnSearch.getStyleClass().add("nav-button-active");
        } else if ("bookings".equalsIgnoreCase(tabName)) {
            btnMyBookings.getStyleClass().add("nav-button-active");
        }
    }
}
