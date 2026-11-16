package com.busreservation.view;

import com.busreservation.model.*;
import com.busreservation.service.*;
import com.busreservation.view.component.NavbarComponent;
import com.busreservation.view.component.ToastNotification;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Central View Manager and Page Router.
 * Coordinates screen transitions, maintains application workflow state,
 * and manages global UI elements like the Navigation Bar and Toast Notifications.
 */
public class ViewManager {

    private final Stage primaryStage;
    private final BorderPane rootLayout;
    private final StackPane centerContentArea;
    private final NavbarComponent navbar;
    private final ToastNotification toast;

    // Services
    private final BusService busService;
    private final SeatService seatService;
    private final FareService fareService;
    private final BookingService bookingService;

    // State parameters across flow
    private String lastSource;
    private String lastDestination;
    private LocalDate lastJourneyDate = LocalDate.now().plusDays(1);

    public ViewManager(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.busService = new BusService();
        this.seatService = new SeatService();
        this.fareService = new FareService();
        this.bookingService = new BookingService();

        this.rootLayout = new BorderPane();
        this.navbar = new NavbarComponent(this);
        this.toast = new ToastNotification();

        this.centerContentArea = new StackPane();
        this.rootLayout.setTop(navbar);

        StackPane mainOverlay = new StackPane(centerContentArea, toast);
        StackPane.setAlignment(toast, javafx.geometry.Pos.BOTTOM_CENTER);
        this.rootLayout.setCenter(mainOverlay);

        Scene scene = new Scene(rootLayout, 1180, 800);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("SwiftBus - Enterprise Bus Reservation System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);
    }

    public void start() {
        showDashboard();
        primaryStage.show();
    }

    public void showDashboard() {
        navbar.setActiveTab("dashboard");
        DashboardView view = new DashboardView(this, busService, bookingService);
        centerContentArea.getChildren().setAll(view);
    }

    public void showBusSearch(String source, String destination, LocalDate journeyDate) {
        navbar.setActiveTab("search");
        if (source != null) this.lastSource = source;
        if (destination != null) this.lastDestination = destination;
        if (journeyDate != null) this.lastJourneyDate = journeyDate;

        BusSearchView view = new BusSearchView(this, busService, seatService, lastSource, lastDestination, lastJourneyDate);
        centerContentArea.getChildren().setAll(view);
    }

    public void showSeatSelection(Bus bus, LocalDate journeyDate) {
        navbar.setActiveTab("search");
        this.lastJourneyDate = journeyDate != null ? journeyDate : LocalDate.now().plusDays(1);
        SeatSelectionView view = new SeatSelectionView(this, bus, this.lastJourneyDate, seatService, fareService);
        centerContentArea.getChildren().setAll(view);
    }

    public void showPassengerDetails(Bus bus, LocalDate journeyDate, List<Seat> selectedSeats) {
        navbar.setActiveTab("search");
        PassengerDetailsView view = new PassengerDetailsView(this, bus, journeyDate, selectedSeats, fareService);
        centerContentArea.getChildren().setAll(view);
    }

    public void showBookingSummary(Bus bus, LocalDate journeyDate, List<Seat> selectedSeats,
                                   List<Passenger> passengers, String contactPhone, String contactEmail) {
        navbar.setActiveTab("search");
        BookingSummaryView view = new BookingSummaryView(this, bus, journeyDate, selectedSeats,
                passengers, contactPhone, contactEmail, fareService, bookingService);
        centerContentArea.getChildren().setAll(view);
    }

    public void showTicket(Booking booking) {
        navbar.setActiveTab("dashboard");
        TicketView view = new TicketView(this, booking);
        centerContentArea.getChildren().setAll(view);
    }

    public void showMyBookings() {
        navbar.setActiveTab("bookings");
        MyBookingsView view = new MyBookingsView(this, bookingService);
        centerContentArea.getChildren().setAll(view);
    }

    public void showLoginModal(Runnable onSuccess) {
        LoginView loginView = new LoginView(this, () -> {
            navbar.updateUserAuthUI();
            if (onSuccess != null) {
                onSuccess.run();
            }
        });
        centerContentArea.getChildren().add(loginView);
    }

    public NavbarComponent getNavbar() {
        return navbar;
    }

    public void showToast(String message, boolean isError) {
        toast.show(message, isError);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public BusService getBusService() {
        return busService;
    }

    public SeatService getSeatService() {
        return seatService;
    }

    public FareService getFareService() {
        return fareService;
    }

    public BookingService getBookingService() {
        return bookingService;
    }
}
