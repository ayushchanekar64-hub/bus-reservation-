package com.busreservation.view;

import com.busreservation.model.Booking;
import com.busreservation.model.Bus;
import com.busreservation.service.BookingService;
import com.busreservation.service.BusService;
import com.busreservation.util.DateTimeUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard & Welcome Screen.
 * Features:
 * - Hero welcome banner
 * - Quick search panel with Source, Destination, Date Picker, and Swap action
 * - System Key Metrics (Active Buses, Available Routes, Recent Bookings)
 * - Quick Route selector pills
 * - Recent Bookings feed with direct ticket view
 */
public class DashboardView extends ScrollPane {

    private final ViewManager viewManager;
    private final BusService busService;
    private final BookingService bookingService;

    private ComboBox<String> sourceCombo;
    private ComboBox<String> destCombo;
    private DatePicker datePicker;

    public DashboardView(ViewManager viewManager, BusService busService, BookingService bookingService) {
        this.viewManager = viewManager;
        this.busService = busService;
        this.bookingService = bookingService;

        setFitToWidth(true);
        setContent(buildContent());
    }

    private VBox buildContent() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(28, 36, 36, 36));

        // 1. HERO BANNER
        VBox heroBanner = createHeroBanner();

        // 2. SEARCH BOX
        VBox searchCard = createSearchCard();

        // 3. STATS TILES
        HBox statsRow = createStatsRow();

        // 4. RECENT BOOKINGS & POPULAR ROUTES SECTION
        HBox bottomSection = new HBox(24);
        bottomSection.setAlignment(Pos.TOP_LEFT);

        VBox popularRoutesCard = createPopularRoutesCard();
        VBox recentBookingsCard = createRecentBookingsCard();

        HBox.setHgrow(popularRoutesCard, Priority.NEVER);
        HBox.setHgrow(recentBookingsCard, Priority.ALWAYS);

        bottomSection.getChildren().addAll(popularRoutesCard, recentBookingsCard);

        root.getChildren().addAll(heroBanner, searchCard, statsRow, bottomSection);
        return root;
    }

    private VBox createHeroBanner() {
        VBox banner = new VBox(8);
        banner.getStyleClass().add("hero-banner");

        Label title = new Label("Where would you like to travel?");
        title.getStyleClass().add("hero-title");

        Label subtitle = new Label("Reserve premium AC, Sleeper, and Volvo multi-axle bus seats with live interactive seat selection.");
        subtitle.getStyleClass().add("hero-subtitle");

        banner.getChildren().addAll(title, subtitle);
        return banner;
    }

    private VBox createSearchCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");

        Label header = new Label("Search Available Buses");
        header.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        HBox formRow = new HBox(14);
        formRow.setAlignment(Pos.BOTTOM_LEFT);

        // Source Dropdown
        VBox srcBox = new VBox(6);
        Label srcLabel = new Label("From (Source)");
        srcLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        sourceCombo = new ComboBox<>();
        sourceCombo.getItems().addAll(busService.getAvailableSources());
        if (!sourceCombo.getItems().isEmpty()) {
            sourceCombo.setValue(sourceCombo.getItems().get(0));
        }
        sourceCombo.setPrefWidth(220);
        srcBox.getChildren().addAll(srcLabel, sourceCombo);

        // Swap Button
        Button btnSwap = new Button("\u21C4");
        btnSwap.getStyleClass().add("btn-secondary");
        btnSwap.setStyle("-fx-font-size: 16px; -fx-padding: 6px 12px;");
        btnSwap.setTooltip(new Tooltip("Swap Source and Destination"));
        btnSwap.setOnAction(e -> {
            String s = sourceCombo.getValue();
            String d = destCombo.getValue();
            sourceCombo.setValue(d);
            destCombo.setValue(s);
        });

        // Destination Dropdown
        VBox destBox = new VBox(6);
        Label destLabel = new Label("To (Destination)");
        destLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        destCombo = new ComboBox<>();
        destCombo.getItems().addAll(busService.getAvailableDestinations());
        if (destCombo.getItems().size() > 1) {
            destCombo.setValue(destCombo.getItems().get(1));
        }
        destCombo.setPrefWidth(220);
        destBox.getChildren().addAll(destLabel, destCombo);

        // Journey Date Picker
        VBox dateBox = new VBox(6);
        Label dateLabel = new Label("Journey Date");
        dateLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #F1F5F9; -fx-text-fill: #CBD5E1;");
                }
            }
        });
        datePicker.setPrefWidth(180);
        dateBox.getChildren().addAll(dateLabel, datePicker);

        // Search Button
        Button btnSearch = new Button("Search Buses \u2192");
        btnSearch.getStyleClass().add("btn-primary");
        btnSearch.setPrefHeight(38);
        btnSearch.setOnAction(e -> handleSearch());

        formRow.getChildren().addAll(srcBox, btnSwap, destBox, dateBox, btnSearch);
        card.getChildren().addAll(header, formRow);
        return card;
    }

    private HBox createStatsRow() {
        HBox row = new HBox(18);
        row.setAlignment(Pos.CENTER);

        List<Bus> buses = busService.getAllActiveBuses();
        List<Booking> bookings = bookingService.getAllBookings();

        row.getChildren().addAll(
                createStatCard("Active Fleet", String.valueOf(buses.size()), "Multi-Axle & Sleeper", "#2563EB", "#EFF6FF"),
                createStatCard("Covered Routes", String.valueOf(busService.getAvailableSources().size() * 2), "Intercity Corridors", "#059669", "#ECFDF5"),
                createStatCard("Total Bookings", String.valueOf(bookings.size()), "Confirmed Trips", "#7C3AED", "#F5F3FF"),
                createStatCard("On-Time Rating", "98.4%", "Punctual Operations", "#D97706", "#FFFBEB")
        );

        for (var node : row.getChildren()) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }

        return row;
    }

    private VBox createStatCard(String label, String value, String sub, String primaryHex, String bgHex) {
        VBox card = new VBox(6);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(16, 20, 16, 20));

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #64748B;");

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + primaryHex + ";");

        Label subText = new Label(sub);
        subText.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");

        card.getChildren().addAll(lbl, val, subText);
        return card;
    }

    private VBox createPopularRoutesCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");
        card.setPrefWidth(320);

        Label title = new Label("Popular Routes");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        VBox routesList = new VBox(8);

        routesList.getChildren().addAll(
                createRouteButton("Nagpur", "Pune", "12h 30m \u2022 From \u20B9950"),
                createRouteButton("Nagpur", "Mumbai", "11h 00m \u2022 Samruddhi Express"),
                createRouteButton("Nagpur", "Hyderabad", "9h 00m \u2022 From \u20B9850"),
                createRouteButton("Nagpur", "Raipur", "5h 30m \u2022 From \u20B9450"),
                createRouteButton("Delhi", "Jaipur", "5h 30m \u2022 From \u20B9550"),
                createRouteButton("Mumbai", "Pune", "3h 30m \u2022 From \u20B9450"),
                createRouteButton("Bangalore", "Goa", "11h 45m \u2022 From \u20B91250")
        );

        card.getChildren().addAll(title, routesList);
        return card;
    }

    private Button createRouteButton(String src, String dest, String meta) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 8px 12px; -fx-cursor: hand;");

        HBox box = new HBox(8);
        box.setAlignment(Pos.CENTER_LEFT);

        VBox text = new VBox(2);
        Label routeLabel = new Label(src + " \u2192 " + dest);
        routeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label metaLabel = new Label(meta);
        metaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        text.getChildren().addAll(routeLabel, metaLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label arrow = new Label("\u203A");
        arrow.setStyle("-fx-font-size: 18px; -fx-text-fill: #94A3B8; -fx-font-weight: bold;");

        box.getChildren().addAll(text, spacer, arrow);
        btn.setGraphic(box);

        btn.setOnAction(e -> {
            sourceCombo.setValue(src);
            destCombo.setValue(dest);
            handleSearch();
        });

        return btn;
    }

    private VBox createRecentBookingsCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Recent Bookings");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnViewAll = new Button("View All \u2192");
        btnViewAll.setStyle("-fx-background-color: transparent; -fx-text-fill: #2563EB; -fx-font-size: 12px; -fx-font-weight: bold; -fx-cursor: hand;");
        btnViewAll.setOnAction(e -> viewManager.showMyBookings());

        top.getChildren().addAll(title, spacer, btnViewAll);

        VBox bookingsList = new VBox(8);
        if (!com.busreservation.service.AuthService.isLoggedIn()) {
            Label empty = new Label("Log in to view your personal trip history and active reservations.");
            empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-padding: 12px 0;");
            Button btnLogin = new Button("Sign In / Register \u2192");
            btnLogin.getStyleClass().add("btn-secondary");
            btnLogin.setOnAction(e -> viewManager.showLoginModal(() -> setContent(buildContent())));
            bookingsList.getChildren().addAll(empty, btnLogin);
        } else {
            com.busreservation.model.User user = com.busreservation.service.AuthService.getCurrentUser();
            List<Booking> recent = bookingService.getBookingsForUser(user.getUserId());

            if (recent.isEmpty()) {
                Label empty = new Label("No bookings yet for " + user.getFullName() + ". Start by searching for a bus!");
                empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #94A3B8; -fx-padding: 16px 0;");
                bookingsList.getChildren().add(empty);
            } else {
                for (Booking b : recent) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 10px 14px; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

                VBox pnrBox = new VBox(2);
                Label pnrLabel = new Label(b.getPnrNumber());
                pnrLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
                Label dateLabel = new Label("Journey: " + DateTimeUtil.formatDate(b.getJourneyDate()));
                dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
                pnrBox.getChildren().addAll(pnrLabel, dateLabel);

                Region rSpacer = new Region();
                HBox.setHgrow(rSpacer, Priority.ALWAYS);

                VBox busInfo = new VBox(2);
                busInfo.setAlignment(Pos.CENTER_RIGHT);
                Label busName = new Label(b.getBus() != null ? b.getBus().getBusName() : "Bus");
                busName.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #334155;");
                Label seats = new Label("Seats: " + b.getSeatNumbersFormatted());
                seats.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
                busInfo.getChildren().addAll(busName, seats);

                Label statusBadge = new Label(b.getBookingStatus().name());
                statusBadge.getStyleClass().add(b.isConfirmed() ? "badge-confirmed" : "badge-cancelled");

                Button btnTicket = new Button("View Ticket");
                btnTicket.getStyleClass().add("btn-secondary");
                btnTicket.setStyle("-fx-font-size: 11px; -fx-padding: 5px 12px;");
                btnTicket.setOnAction(e -> viewManager.showTicket(b));

                row.getChildren().addAll(pnrBox, rSpacer, busInfo, statusBadge, btnTicket);
                bookingsList.getChildren().add(row);
            }
        }
    }

        card.getChildren().addAll(top, bookingsList);
        return card;
    }

    private void handleSearch() {
        String src = sourceCombo.getValue();
        String dest = destCombo.getValue();
        LocalDate date = datePicker.getValue();

        if (src == null || dest == null) {
            viewManager.showToast("Please choose both source and destination.", true);
            return;
        }

        if (src.equalsIgnoreCase(dest)) {
            viewManager.showToast("Source and destination cannot be the same city.", true);
            return;
        }

        if (date == null || date.isBefore(LocalDate.now())) {
            viewManager.showToast("Please select a valid future journey date.", true);
            return;
        }

        viewManager.showBusSearch(src, dest, date);
    }
}
