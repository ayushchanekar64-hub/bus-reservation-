package com.busreservation.view;

import com.busreservation.model.Booking;
import com.busreservation.service.BookingService;
import com.busreservation.util.DateTimeUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Screen displaying previous bookings, PNR search, ticket re-generation, and booking cancellation.
 */
public class MyBookingsView extends ScrollPane {

    private final ViewManager viewManager;
    private final BookingService bookingService;

    private TextField searchField;
    private VBox bookingsContainer;

    public MyBookingsView(ViewManager viewManager, BookingService bookingService) {
        this.viewManager = viewManager;
        this.bookingService = bookingService;

        setFitToWidth(true);
        setContent(buildContent());

        loadBookings(null);
    }

    private VBox buildContent() {
        VBox root = new VBox(22);
        root.setPadding(new Insets(24, 36, 36, 36));

        // 1. HEADER & SEARCH BAR
        HBox topBar = createTopBar();

        // 2. BOOKINGS LIST CONTAINER
        bookingsContainer = new VBox(14);

        root.getChildren().addAll(topBar, bookingsContainer);
        return root;
    }

    private HBox createTopBar() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("card");
        bar.setPadding(new Insets(16, 22, 16, 22));

        VBox titleBox = new VBox(2);
        Label title = new Label("My Bookings");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label subtitle = new Label("Manage your reservations, view electronic tickets, or cancel trips.");
        subtitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // PNR Search input
        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        searchField = new TextField();
        searchField.setPromptText("Search by PNR (e.g. PNR-8849201)");
        searchField.setPrefWidth(240);
        searchField.setOnAction(e -> handleSearch());

        Button btnSearch = new Button("Search");
        btnSearch.getStyleClass().add("btn-primary");
        btnSearch.setOnAction(e -> handleSearch());

        Button btnReset = new Button("View All");
        btnReset.getStyleClass().add("btn-secondary");
        btnReset.setOnAction(e -> {
            searchField.clear();
            loadBookings(null);
        });

        searchBox.getChildren().addAll(searchField, btnSearch, btnReset);

        bar.getChildren().addAll(titleBox, spacer, searchBox);
        return bar;
    }

    private void handleSearch() {
        String query = searchField.getText() != null ? searchField.getText().trim() : "";
        if (query.isEmpty()) {
            loadBookings(null);
        } else {
            loadBookings(query);
        }
    }

    private void loadBookings(String pnrQuery) {
        bookingsContainer.getChildren().clear();

        // Check if user is logged in
        if (!com.busreservation.service.AuthService.isLoggedIn()) {
            VBox notLoggedIn = new VBox(14);
            notLoggedIn.setAlignment(Pos.CENTER);
            notLoggedIn.getStyleClass().add("card");
            notLoggedIn.setPadding(new Insets(40));

            Label lockEmoji = new Label("\uD83D\uDD12");
            lockEmoji.setStyle("-fx-font-size: 36px;");

            Label msg = new Label("Please Log In to View Your Bookings");
            msg.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

            Label sub = new Label("For data privacy and security, only your own reserved tickets are visible in your personal account.");
            sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

            Button btnLogin = new Button("Log In / Create Account \u2192");
            btnLogin.getStyleClass().add("btn-primary");
            btnLogin.setOnAction(e -> viewManager.showLoginModal(() -> loadBookings(null)));

            notLoggedIn.getChildren().addAll(lockEmoji, msg, sub, btnLogin);
            bookingsContainer.getChildren().add(notLoggedIn);
            return;
        }

        com.busreservation.model.User currentUser = com.busreservation.service.AuthService.getCurrentUser();
        List<Booking> userBookingsList = bookingService.getBookingsForUser(currentUser.getUserId());

        List<Booking> bookings;
        if (pnrQuery != null && !pnrQuery.isBlank()) {
            bookings = userBookingsList.stream()
                    .filter(b -> b.getPnrNumber().equalsIgnoreCase(pnrQuery.trim()))
                    .toList();
        } else {
            bookings = userBookingsList;
        }

        if (bookings.isEmpty()) {
            VBox empty = new VBox(12);
            empty.setAlignment(Pos.CENTER);
            empty.getStyleClass().add("card");
            empty.setPadding(new Insets(40));

            Label emoji = new Label("\uD83C\uDFAB");
            emoji.setStyle("-fx-font-size: 36px;");

            Label msg = new Label("No Personal Bookings Found" + (pnrQuery != null ? " matching '" + pnrQuery + "'" : ""));
            msg.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

            Label sub = new Label("Hello " + currentUser.getFullName() + "! You have not booked any bus tickets yet.");
            sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

            Button btnBook = new Button("Book a Bus Now \u2192");
            btnBook.getStyleClass().add("btn-primary");
            btnBook.setOnAction(e -> viewManager.showDashboard());

            empty.getChildren().addAll(emoji, msg, sub, btnBook);
            bookingsContainer.getChildren().add(empty);

        } else {
            for (Booking b : bookings) {
                bookingsContainer.getChildren().add(createBookingCard(b));
            }
        }
    }

    private VBox createBookingCard(Booking b) {
        VBox card = new VBox(12);
        card.getStyleClass().addAll("card", "card-hover");
        card.setPadding(new Insets(18, 22, 18, 22));

        // Top Row: PNR, Status badge, Fare
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label pnrLbl = new Label("PNR: " + b.getPnrNumber());
        pnrLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label statusBadge = new Label(b.getBookingStatus().name());
        statusBadge.getStyleClass().add(b.isConfirmed() ? "badge-confirmed" : "badge-cancelled");

        Region spTop = new Region();
        HBox.setHgrow(spTop, Priority.ALWAYS);

        Label fareLbl = new Label(String.format("Total: \u20B9%.2f", b.getTotalFare()));
        fareLbl.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");

        topRow.getChildren().addAll(pnrLbl, statusBadge, spTop, fareLbl);

        // Middle Row: Bus info, Journey Date, Seats
        HBox midRow = new HBox(20);
        midRow.setAlignment(Pos.CENTER_LEFT);
        midRow.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 10px 16px; -fx-background-radius: 8px;");

        VBox routeInfo = new VBox(2);
        Label route = new Label(b.getBus() != null ? b.getBus().getSource() + " \u2192 " + b.getBus().getDestination() : "Route");
        route.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label busName = new Label(b.getBus() != null ? b.getBus().getBusName() + " (" + b.getBus().getBusNumber() + ")" : "");
        busName.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        routeInfo.getChildren().addAll(route, busName);

        Region spMid = new Region();
        HBox.setHgrow(spMid, Priority.ALWAYS);

        VBox jDateBox = new VBox(2);
        Label dateLbl = new Label("Journey Date: " + DateTimeUtil.formatDate(b.getJourneyDate()));
        dateLbl.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #334155;");
        Label depTime = new Label(b.getBus() != null ? "Departure: " + DateTimeUtil.formatTime(b.getBus().getDepartureTime()) : "");
        depTime.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        jDateBox.getChildren().addAll(dateLbl, depTime);

        VBox seatBox = new VBox(2);
        seatBox.setAlignment(Pos.CENTER_RIGHT);
        Label seats = new Label("Seats: " + b.getSeatNumbersFormatted());
        seats.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label passCount = new Label(b.getSeatCount() + " Passenger(s)");
        passCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        seatBox.getChildren().addAll(seats, passCount);

        midRow.getChildren().addAll(routeInfo, spMid, jDateBox, seatBox);

        // Bottom Row: Actions
        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_RIGHT);

        Button btnView = new Button("View E-Ticket");
        btnView.getStyleClass().add("btn-secondary");
        btnView.setOnAction(e -> viewManager.showTicket(b));

        Button btnCancel = new Button("Cancel Booking");
        btnCancel.getStyleClass().add("btn-danger");
        if (b.isCancelled()) {
            btnCancel.setDisable(true);
            btnCancel.setText("Cancelled");
        } else {
            btnCancel.setOnAction(e -> handleCancelBooking(b));
        }

        bottomRow.getChildren().addAll(btnView, btnCancel);

        card.getChildren().addAll(topRow, midRow, bottomRow);
        return card;
    }

    private void handleCancelBooking(Booking b) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setHeaderText("Cancel Booking " + b.getPnrNumber() + "?");
        confirm.setContentText("Are you sure you want to cancel this reservation? All booked seats (" +
                b.getSeatNumbersFormatted() + ") will be released immediately back to available status.");

        confirm.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                boolean ok = bookingService.cancelBooking(b.getPnrNumber());
                if (ok) {
                    viewManager.showToast("Booking " + b.getPnrNumber() + " cancelled successfully. Seats released.", false);
                    loadBookings(searchField.getText());
                } else {
                    viewManager.showToast("Failed to cancel booking. It may already be cancelled.", true);
                }
            }
        });
    }
}
