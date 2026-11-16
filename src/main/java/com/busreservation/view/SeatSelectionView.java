package com.busreservation.view;

import com.busreservation.model.Bus;
import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;
import com.busreservation.service.FareService;
import com.busreservation.service.SeatService;
import com.busreservation.util.DateTimeUtil;
import com.busreservation.view.component.BusLayoutPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interactive Bus Seat Selection Screen.
 * Implements the realistic top-view bus floor plan with reactive seat selection,
 * color legends, live fare calculation, and validation.
 */
public class SeatSelectionView extends ScrollPane {

    private static final int MAX_SEATS_PER_BOOKING = 6;

    private final ViewManager viewManager;
    private final Bus bus;
    private final LocalDate journeyDate;
    private final SeatService seatService;
    private final FareService fareService;

    private final List<Seat> selectedSeats = new ArrayList<>();

    // Reactive UI Controls
    private Label selectedSeatsCountLabel;
    private Label selectedSeatsListLabel;
    private Label baseFareLabel;
    private Label gstLabel;
    private Label totalFareLabel;
    private Button btnProceed;

    public SeatSelectionView(ViewManager viewManager, Bus bus, LocalDate journeyDate,
                             SeatService seatService, FareService fareService) {
        this.viewManager = viewManager;
        this.bus = bus;
        this.journeyDate = journeyDate;
        this.seatService = seatService;
        this.fareService = fareService;

        setFitToWidth(true);
        setContent(buildContent());
    }

    private VBox buildContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24, 36, 36, 36));

        // 1. TOP HEADER & BREADCRUMBS
        HBox headerBox = createHeaderBox();

        // 2. MAIN WORKSPACE: BUS FLOOR PLAN (LEFT/CENTER) + SEAT SUMMARY & FARE (RIGHT)
        HBox workspace = new HBox(28);
        workspace.setAlignment(Pos.TOP_CENTER);

        // Left Side: Interactive Top-View Bus Frame & Legend
        VBox busSection = createBusSection();

        // Right Side: Booking Summary & Live Fare Card
        VBox summarySidebar = createSummarySidebar();

        HBox.setHgrow(busSection, Priority.ALWAYS);
        HBox.setHgrow(summarySidebar, Priority.NEVER);

        workspace.getChildren().addAll(busSection, summarySidebar);

        root.getChildren().addAll(headerBox, workspace);
        return root;
    }

    private HBox createHeaderBox() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("card");
        header.setPadding(new Insets(16, 22, 16, 22));

        Button btnBack = new Button("\u2190 Back to Buses");
        btnBack.getStyleClass().add("btn-secondary");
        btnBack.setOnAction(e -> viewManager.showBusSearch(bus.getSource(), bus.getDestination(), journeyDate));

        VBox titleBox = new VBox(2);
        Label routeLabel = new Label(bus.getSource() + " \u2192 " + bus.getDestination());
        routeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label busMeta = new Label(bus.getBusName() + " (" + bus.getBusNumber() + ") \u2022 " +
                bus.getBusType() + " \u2022 Departs: " + DateTimeUtil.formatTime(bus.getDepartureTime()) +
                " \u2022 Date: " + DateTimeUtil.formatDate(journeyDate));
        busMeta.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

        titleBox.getChildren().addAll(routeLabel, busMeta);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(btnBack, titleBox, spacer);
        return header;
    }

    private VBox createBusSection() {
        VBox box = new VBox(16);
        box.setAlignment(Pos.TOP_CENTER);
        box.getStyleClass().add("card");
        box.setPadding(new Insets(20));

        // Legend Row
        HBox legendRow = createLegendRow();

        // Top-View Bus Interior Canvas
        List<Seat> seats = seatService.getSeatsForBus(bus.getBusId());
        BusLayoutPane busLayout = new BusLayoutPane(bus, seats, this::handleSeatToggled);

        box.getChildren().addAll(legendRow, new Separator(), busLayout);
        return box;
    }

    private HBox createLegendRow() {
        HBox legend = new HBox(24);
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(4, 0, 8, 0));

        legend.getChildren().addAll(
                createLegendItem("Available", "#22C55E", "#DCFCE7"),
                createLegendItem("Already Booked", "#94A3B8", "#E2E8F0"),
                createLegendItem("Selected (You)", "#1D4ED8", "#3B82F6")
        );

        return legend;
    }

    private HBox createLegendItem(String label, String strokeColor, String fillColor) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER);

        Rectangle sample = new Rectangle(20, 20);
        sample.setArcWidth(6);
        sample.setArcHeight(6);
        sample.setFill(Color.web(fillColor));
        sample.setStroke(Color.web(strokeColor));
        sample.setStrokeWidth(2.0);

        Label text = new Label(label);
        text.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #334155;");

        item.getChildren().addAll(sample, text);
        return item;
    }

    private VBox createSummarySidebar() {
        VBox sidebar = new VBox(18);
        sidebar.getStyleClass().add("card");
        sidebar.setPrefWidth(340);
        sidebar.setMinWidth(320);

        Label title = new Label("Seat Selection Summary");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        // Selected Seats Info Card
        VBox seatInfoBox = new VBox(8);
        seatInfoBox.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 14px; -fx-background-radius: 8px; -fx-border-color: #E2E8F0; -fx-border-radius: 8px;");

        HBox countRow = new HBox();
        Label countTitle = new Label("Seats Selected:");
        countTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        selectedSeatsCountLabel = new Label("0 (Max " + MAX_SEATS_PER_BOOKING + ")");
        selectedSeatsCountLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);
        countRow.getChildren().addAll(countTitle, sp1, selectedSeatsCountLabel);

        HBox listRow = new HBox();
        Label listTitle = new Label("Seat Numbers:");
        listTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        selectedSeatsListLabel = new Label("None selected");
        selectedSeatsListLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);
        listRow.getChildren().addAll(listTitle, sp2, selectedSeatsListLabel);

        seatInfoBox.getChildren().addAll(countRow, listRow);

        // Fare Breakdown
        VBox fareBox = new VBox(8);
        fareBox.setStyle("-fx-padding: 6px 0;");

        Label fareHeader = new Label("Fare Breakdown");
        fareHeader.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #334155;");

        HBox baseRow = createFareLine("Base Seat Fare:", baseFareLabel = new Label("\u20B90.00"));
        HBox gstRow = createFareLine("GST & Taxes (5%):", gstLabel = new Label("\u20B90.00"));

        Separator sep = new Separator();

        HBox totalRow = new HBox();
        Label totalText = new Label("Total Payable:");
        totalText.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");
        totalFareLabel = new Label("\u20B90.00");
        totalFareLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        Region spTotal = new Region();
        HBox.setHgrow(spTotal, Priority.ALWAYS);
        totalRow.getChildren().addAll(totalText, spTotal, totalFareLabel);

        fareBox.getChildren().addAll(fareHeader, baseRow, gstRow, sep, totalRow);

        // Proceed Button
        btnProceed = new Button("Proceed to Passenger Details \u2192");
        btnProceed.getStyleClass().add("btn-action-orange");
        btnProceed.setMaxWidth(Double.MAX_VALUE);
        btnProceed.setDisable(true); // Disabled until at least 1 seat is picked
        btnProceed.setOnAction(e -> handleProceed());

        Label hint = new Label("Click on any green seat in the bus layout to reserve.");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #94A3B8;");
        hint.setWrapText(true);

        sidebar.getChildren().addAll(title, seatInfoBox, fareBox, btnProceed, hint);
        return sidebar;
    }

    private HBox createFareLine(String title, Label valLabel) {
        HBox row = new HBox();
        Label t = new Label(title);
        t.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        valLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1E293B;");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        row.getChildren().addAll(t, sp, valLabel);
        return row;
    }

    private void handleSeatToggled(Seat seat) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            viewManager.showToast("Seat " + seat.getSeatNumber() + " deselected.", false);
        } else {
            if (selectedSeats.size() >= MAX_SEATS_PER_BOOKING) {
                viewManager.showToast("You can select up to " + MAX_SEATS_PER_BOOKING + " seats per booking.", true);
                return;
            }
            selectedSeats.add(seat);
            viewManager.showToast("Seat " + seat.getSeatNumber() + " selected.", false);
        }

        updateLiveSummary();
    }

    private void updateLiveSummary() {
        int count = selectedSeats.size();
        selectedSeatsCountLabel.setText(count + " (Max " + MAX_SEATS_PER_BOOKING + ")");

        if (count == 0) {
            selectedSeatsListLabel.setText("None selected");
            baseFareLabel.setText("\u20B90.00");
            gstLabel.setText("\u20B90.00");
            totalFareLabel.setText("\u20B90.00");
            btnProceed.setDisable(true);
        } else {
            String seatList = selectedSeats.stream()
                    .map(Seat::getSeatNumber)
                    .collect(Collectors.joining(", "));
            selectedSeatsListLabel.setText(seatList);

            double base = fareService.calculateBaseTotal(bus, selectedSeats);
            double gst = fareService.calculateGst(base);
            double grandTotal = fareService.calculateGrandTotal(bus, selectedSeats);

            baseFareLabel.setText(String.format("\u20B9%.2f", base));
            gstLabel.setText(String.format("\u20B9%.2f", gst));
            totalFareLabel.setText(String.format("\u20B9%.2f", grandTotal));

            btnProceed.setDisable(false);
        }
    }

    private void handleProceed() {
        if (selectedSeats.isEmpty()) {
            viewManager.showToast("Please select at least one available seat to proceed.", true);
            return;
        }

        // REQUIRE USER TO BE LOGGED IN BEFORE BOOKING
        if (!com.busreservation.service.AuthService.isLoggedIn()) {
            viewManager.showToast("Please log in or create an account before continuing with your booking.", false);
            viewManager.showLoginModal(() -> {
                viewManager.showPassengerDetails(bus, journeyDate, new ArrayList<>(selectedSeats));
            });
            return;
        }

        viewManager.showPassengerDetails(bus, journeyDate, new ArrayList<>(selectedSeats));
    }
}
