package com.busreservation.view;

import com.busreservation.model.Booking;
import com.busreservation.model.Bus;
import com.busreservation.model.Passenger;
import com.busreservation.model.Seat;
import com.busreservation.service.BookingService;
import com.busreservation.service.FareService;
import com.busreservation.util.DateTimeUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Screen presenting a pre-confirmation booking review and fare breakdown.
 */
public class BookingSummaryView extends ScrollPane {

    private final ViewManager viewManager;
    private final Bus bus;
    private final LocalDate journeyDate;
    private final List<Seat> selectedSeats;
    private final List<Passenger> passengers;
    private final String contactPhone;
    private final String contactEmail;
    private final FareService fareService;
    private final BookingService bookingService;

    public BookingSummaryView(ViewManager viewManager, Bus bus, LocalDate journeyDate,
                              List<Seat> selectedSeats, List<Passenger> passengers,
                              String contactPhone, String contactEmail,
                              FareService fareService, BookingService bookingService) {
        this.viewManager = viewManager;
        this.bus = bus;
        this.journeyDate = journeyDate;
        this.selectedSeats = selectedSeats;
        this.passengers = passengers;
        this.contactPhone = contactPhone;
        this.contactEmail = contactEmail;
        this.fareService = fareService;
        this.bookingService = bookingService;

        setFitToWidth(true);
        setContent(buildContent());
    }

    private VBox buildContent() {
        VBox root = new VBox(22);
        root.setPadding(new Insets(24, 36, 36, 36));

        // Header
        HBox header = createHeader();

        // Left: Journey Details & Passengers / Right: Payment & Confirmation
        HBox mainBox = new HBox(24);
        mainBox.setAlignment(Pos.TOP_CENTER);

        VBox leftDetails = new VBox(18);
        leftDetails.getChildren().addAll(createJourneyCard(), createPassengerSummaryCard());

        VBox rightFareCard = createFareAndConfirmCard();

        HBox.setHgrow(leftDetails, Priority.ALWAYS);
        HBox.setHgrow(rightFareCard, Priority.NEVER);

        mainBox.getChildren().addAll(leftDetails, rightFareCard);

        root.getChildren().addAll(header, mainBox);
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("card");
        header.setPadding(new Insets(16, 22, 16, 22));

        Button btnBack = new Button("\u2190 Modify Details");
        btnBack.getStyleClass().add("btn-secondary");
        btnBack.setOnAction(e -> viewManager.showPassengerDetails(bus, journeyDate, selectedSeats));

        VBox info = new VBox(2);
        Label title = new Label("Review Booking Summary");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label sub = new Label("Please verify your trip and passenger information before confirming reservation.");
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        info.getChildren().addAll(title, sub);

        header.getChildren().addAll(btnBack, info);
        return header;
    }

    private VBox createJourneyCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");

        Label title = new Label("Journey & Bus Details");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        GridPane grid = new GridPane();
        grid.setHgap(36);
        grid.setVgap(12);

        addRow(grid, 0, "Route:", bus.getSource() + " \u2192 " + bus.getDestination());
        addRow(grid, 1, "Bus Operator:", bus.getBusName() + " (" + bus.getBusNumber() + ")");
        addRow(grid, 2, "Bus Type:", bus.getBusType());
        addRow(grid, 3, "Journey Date:", DateTimeUtil.formatDate(journeyDate));
        addRow(grid, 4, "Departure Time:", DateTimeUtil.formatTime(bus.getDepartureTime()));
        addRow(grid, 5, "Est. Arrival:", DateTimeUtil.formatTime(bus.getArrivalTime()) + " (" + bus.getJourneyDuration() + ")");

        card.getChildren().addAll(title, grid);
        return card;
    }

    private VBox createPassengerSummaryCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("card");

        Label title = new Label("Passengers & Allocated Seats");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        VBox list = new VBox(8);
        for (int i = 0; i < passengers.size(); i++) {
            Passenger p = passengers.get(i);
            Seat s = selectedSeats.get(i);
            double seatFare = fareService.calculateSeatFare(bus, s);

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 8px 14px; -fx-background-radius: 6px;");

            Label name = new Label((i + 1) + ". " + p.getFullName() + " (" + p.getAge() + " yrs, " + p.getGender() + ")");
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #334155;");

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label seat = new Label("Seat " + s.getSeatNumber());
            seat.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 4px;");

            Label fare = new Label(String.format("\u20B9%.2f", seatFare));
            fare.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

            row.getChildren().addAll(name, sp, seat, fare);
            list.getChildren().add(row);
        }

        // Contact info
        Label contactLabel = new Label("Contact: " + contactPhone + (contactEmail != null && !contactEmail.isBlank() ? " \u2022 " + contactEmail : ""));
        contactLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B; -fx-padding: 6px 0 0 0;");

        card.getChildren().addAll(title, list, contactLabel);
        return card;
    }

    private VBox createFareAndConfirmCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPrefWidth(340);
        card.setMinWidth(320);

        Label title = new Label("Fare & Payment");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        double base = fareService.calculateBaseTotal(bus, selectedSeats);
        double gst = fareService.calculateGst(base);
        double total = fareService.calculateGrandTotal(bus, selectedSeats);

        VBox fareBox = new VBox(8);
        fareBox.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 14px; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        fareBox.getChildren().addAll(
                createLine("Seats Count:", selectedSeats.size() + " Seat(s)"),
                createLine("Base Subtotal:", String.format("\u20B9%.2f", base)),
                createLine("GST (5%):", String.format("\u20B9%.2f", gst)),
                new Separator(),
                createTotalLine("Grand Total:", String.format("\u20B9%.2f", total))
        );

        Button btnConfirm = new Button("Confirm Booking & Issue Ticket \u2192");
        btnConfirm.getStyleClass().add("btn-action-orange");
        btnConfirm.setMaxWidth(Double.MAX_VALUE);
        btnConfirm.setOnAction(e -> handleConfirmBooking());

        Label securityNote = new Label("\uD83D\uDD12 Transaction Safe: Real-time seat locking guarantees no double-booking.");
        securityNote.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        securityNote.setWrapText(true);

        card.getChildren().addAll(title, fareBox, btnConfirm, securityNote);
        return card;
    }

    private void handleConfirmBooking() {
        try {
            Booking booking = bookingService.processBooking(
                    bus, journeyDate, passengers, selectedSeats, contactPhone, contactEmail
            );

            viewManager.showToast("Booking Successful! PNR: " + booking.getPnrNumber(), false);
            viewManager.showTicket(booking);

        } catch (Exception e) {
            viewManager.showToast("Booking Error: " + e.getMessage(), true);
        }
    }

    private void addRow(GridPane grid, int row, String label, String val) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 13px; -fx-text-fill: #1E293B;");
        grid.add(lbl, 0, row);
        grid.add(v, 1, row);
    }

    private HBox createLine(String left, String right) {
        HBox box = new HBox();
        Label l = new Label(left);
        l.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        Label r = new Label(right);
        r.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1E293B;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        box.getChildren().addAll(l, sp, r);
        return box;
    }

    private HBox createTotalLine(String left, String right) {
        HBox box = new HBox();
        Label l = new Label(left);
        l.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Label r = new Label(right);
        r.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        box.getChildren().addAll(l, sp, r);
        return box;
    }
}
