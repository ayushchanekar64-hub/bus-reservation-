package com.busreservation.view.component;

import com.busreservation.model.Bus;
import com.busreservation.service.SeatService;
import com.busreservation.util.DateTimeUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.function.Consumer;

/**
 * Modern Card UI Component representing an available bus listing.
 */
public class BusCardComponent extends VBox {

    public BusCardComponent(Bus bus, SeatService seatService, Consumer<Bus> onSelectSeats) {
        getStyleClass().addAll("card", "card-hover");
        setSpacing(14);
        setPadding(new Insets(18, 22, 18, 22));

        int availableSeats = seatService.getAvailableSeatCount(bus.getBusId());

        // TOP ROW: Bus Name, Type Badge, and Price
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label nameLabel = new Label(bus.getBusName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

        Label numberLabel = new Label(bus.getBusNumber());
        numberLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        titleBox.getChildren().addAll(nameLabel, numberLabel);

        Label typeBadge = new Label(bus.getBusType());
        typeBadge.getStyleClass().add(bus.getBusType().toLowerCase().contains("sleeper") ? "badge-sleeper" : "badge-ac");

        Region spacerTop = new Region();
        HBox.setHgrow(spacerTop, Priority.ALWAYS);

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER_RIGHT);
        Label startsFromLabel = new Label("Starts from");
        startsFromLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");

        Label priceLabel = new Label(String.format("\u20B9%.0f", bus.getBaseFare()));
        priceLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        priceBox.getChildren().addAll(startsFromLabel, priceLabel);

        topRow.getChildren().addAll(titleBox, typeBadge, spacerTop, priceBox);

        // MIDDLE ROW: Schedule (Departure, Duration, Arrival)
        HBox scheduleRow = new HBox(20);
        scheduleRow.setAlignment(Pos.CENTER_LEFT);
        scheduleRow.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 12px 16px; -fx-background-radius: 8px;");

        // Departure
        VBox depBox = new VBox(2);
        Label depTime = new Label(DateTimeUtil.formatTime(bus.getDepartureTime()));
        depTime.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label depCity = new Label(bus.getSource());
        depCity.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        depBox.getChildren().addAll(depTime, depCity);

        // Duration Badge
        VBox durationBox = new VBox(2);
        durationBox.setAlignment(Pos.CENTER);
        Label durationText = new Label(bus.getJourneyDuration());
        durationText.setStyle("-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #475569;");
        Label arrowText = new Label("\u2500\u2500\u2500\u25B6");
        arrowText.setStyle("-fx-font-size: 12px; -fx-text-fill: #94A3B8;");
        durationBox.getChildren().addAll(durationText, arrowText);

        // Arrival
        VBox arrBox = new VBox(2);
        Label arrTime = new Label(DateTimeUtil.formatTime(bus.getArrivalTime()));
        arrTime.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label arrCity = new Label(bus.getDestination());
        arrCity.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        arrBox.getChildren().addAll(arrTime, arrCity);

        Region scheduleSpacer = new Region();
        HBox.setHgrow(scheduleSpacer, Priority.ALWAYS);

        // Available seats indicator
        VBox seatCountBox = new VBox(2);
        seatCountBox.setAlignment(Pos.CENTER_RIGHT);
        Label seatCount = new Label(availableSeats + " Seats Available");
        seatCount.setStyle(String.format(
                "-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: %s;",
                availableSeats > 10 ? "#16A34A" : (availableSeats > 0 ? "#D97706" : "#DC2626")
        ));
        Label layoutType = new Label(bus.getSeatingCategory());
        layoutType.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        seatCountBox.getChildren().addAll(seatCount, layoutType);

        scheduleRow.getChildren().addAll(depBox, durationBox, arrBox, scheduleSpacer, seatCountBox);

        // BOTTOM ROW: Amenities chips + "Select Seats" CTA button
        HBox bottomRow = new HBox(12);
        bottomRow.setAlignment(Pos.CENTER_LEFT);

        HBox amenitiesBox = new HBox(6);
        amenitiesBox.setAlignment(Pos.CENTER_LEFT);
        for (String amenity : bus.getAmenities()) {
            Label chip = new Label("\u2713 " + amenity);
            chip.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569; -fx-background-color: #F1F5F9; -fx-padding: 3px 8px; -fx-background-radius: 6px;");
            amenitiesBox.getChildren().add(chip);
        }

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        Button btnSelect = new Button("Select Seats \u2192");
        btnSelect.getStyleClass().add("btn-primary");
        btnSelect.setOnAction(e -> {
            if (onSelectSeats != null) {
                onSelectSeats.accept(bus);
            }
        });

        bottomRow.getChildren().addAll(amenitiesBox, bottomSpacer, btnSelect);

        getChildren().addAll(topRow, scheduleRow, bottomRow);
    }
}
