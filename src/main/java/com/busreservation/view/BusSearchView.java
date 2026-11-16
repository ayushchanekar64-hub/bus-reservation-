package com.busreservation.view;

import com.busreservation.model.Bus;
import com.busreservation.service.BusService;
import com.busreservation.service.SeatService;
import com.busreservation.util.DateTimeUtil;
import com.busreservation.view.component.BusCardComponent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Screen displaying available buses for the selected route and journey date.
 */
public class BusSearchView extends ScrollPane {

    private final ViewManager viewManager;
    private final BusService busService;
    private final SeatService seatService;

    private ComboBox<String> sourceCombo;
    private ComboBox<String> destCombo;
    private DatePicker datePicker;
    private VBox busListContainer;
    private Label resultsCountLabel;

    public BusSearchView(ViewManager viewManager, BusService busService, SeatService seatService,
                         String initialSource, String initialDest, LocalDate initialDate) {
        this.viewManager = viewManager;
        this.busService = busService;
        this.seatService = seatService;

        setFitToWidth(true);
        setContent(buildContent(initialSource, initialDest, initialDate));

        executeSearch(initialSource, initialDest, initialDate != null ? initialDate : LocalDate.now().plusDays(1));
    }

    private VBox buildContent(String initialSource, String initialDest, LocalDate initialDate) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28, 36, 36, 36));

        // 1. FILTER BAR
        HBox filterBar = createFilterBar(initialSource, initialDest, initialDate);

        // 2. RESULTS HEADER
        HBox headerRow = new HBox(12);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        resultsCountLabel = new Label("Searching available buses...");
        resultsCountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnShowAll = new Button("View All Fleet");
        btnShowAll.getStyleClass().add("btn-secondary");
        btnShowAll.setStyle("-fx-font-size: 12px; -fx-padding: 6px 14px;");
        btnShowAll.setOnAction(e -> {
            sourceCombo.setValue(null);
            destCombo.setValue(null);
            showAllBuses();
        });

        headerRow.getChildren().addAll(resultsCountLabel, spacer, btnShowAll);

        // 3. BUS CARDS CONTAINER
        busListContainer = new VBox(16);

        root.getChildren().addAll(filterBar, headerRow, busListContainer);
        return root;
    }

    private HBox createFilterBar(String initialSource, String initialDest, LocalDate initialDate) {
        HBox bar = new HBox(12);
        bar.setAlignment(Pos.BOTTOM_LEFT);
        bar.getStyleClass().add("card");
        bar.setPadding(new Insets(16, 20, 16, 20));

        // Source
        VBox srcBox = new VBox(4);
        Label srcLabel = new Label("From");
        srcLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        sourceCombo = new ComboBox<>();
        sourceCombo.getItems().addAll(busService.getAvailableSources());
        if (initialSource != null) sourceCombo.setValue(initialSource);
        sourceCombo.setPrefWidth(200);
        srcBox.getChildren().addAll(srcLabel, sourceCombo);

        // Swap
        Button btnSwap = new Button("\u21C4");
        btnSwap.getStyleClass().add("btn-secondary");
        btnSwap.setStyle("-fx-font-size: 14px; -fx-padding: 7px 10px;");
        btnSwap.setOnAction(e -> {
            String s = sourceCombo.getValue();
            String d = destCombo.getValue();
            sourceCombo.setValue(d);
            destCombo.setValue(s);
        });

        // Destination
        VBox destBox = new VBox(4);
        Label destLabel = new Label("To");
        destLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        destCombo = new ComboBox<>();
        destCombo.getItems().addAll(busService.getAvailableDestinations());
        if (initialDest != null) destCombo.setValue(initialDest);
        destCombo.setPrefWidth(200);
        destBox.getChildren().addAll(destLabel, destCombo);

        // Date
        VBox dateBox = new VBox(4);
        Label dateLabel = new Label("Date of Journey");
        dateLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        datePicker = new DatePicker(initialDate != null ? initialDate : LocalDate.now().plusDays(1));
        datePicker.setPrefWidth(160);
        dateBox.getChildren().addAll(dateLabel, datePicker);

        // Update Button
        Button btnUpdate = new Button("Modify Search");
        btnUpdate.getStyleClass().add("btn-primary");
        btnUpdate.setPrefHeight(36);
        btnUpdate.setOnAction(e -> {
            String s = sourceCombo.getValue();
            String d = destCombo.getValue();
            LocalDate dt = datePicker.getValue();

            if (s == null || d == null) {
                viewManager.showToast("Please choose both source and destination.", true);
                return;
            }
            if (s.equalsIgnoreCase(d)) {
                viewManager.showToast("Source and destination cannot be identical.", true);
                return;
            }
            executeSearch(s, d, dt);
        });

        bar.getChildren().addAll(srcBox, btnSwap, destBox, dateBox, btnUpdate);
        return bar;
    }

    private void executeSearch(String source, String destination, LocalDate date) {
        busListContainer.getChildren().clear();

        if (source == null || destination == null) {
            showAllBuses();
            return;
        }

        List<Bus> matching = busService.searchBuses(source, destination, date);

        if (matching.isEmpty()) {
            resultsCountLabel.setText("No buses found for " + source + " \u2192 " + destination + " on " + DateTimeUtil.formatDate(date));
            VBox emptyBox = createEmptyState(source, destination);
            busListContainer.getChildren().add(emptyBox);
        } else {
            resultsCountLabel.setText(matching.size() + " Buses Found from " + source + " to " + destination + " (" + DateTimeUtil.formatDate(date) + ")");
            for (Bus bus : matching) {
                BusCardComponent card = new BusCardComponent(bus, seatService, selectedBus -> {
                    viewManager.showSeatSelection(selectedBus, date);
                });
                busListContainer.getChildren().add(card);
            }
        }
    }

    private void showAllBuses() {
        busListContainer.getChildren().clear();
        List<Bus> allBuses = busService.getAllActiveBuses();
        resultsCountLabel.setText("All Active Intercity Buses (" + allBuses.size() + " total)");
        LocalDate date = datePicker.getValue() != null ? datePicker.getValue() : LocalDate.now().plusDays(1);

        for (Bus bus : allBuses) {
            BusCardComponent card = new BusCardComponent(bus, seatService, selectedBus -> {
                viewManager.showSeatSelection(selectedBus, date);
            });
            busListContainer.getChildren().add(card);
        }
    }

    private VBox createEmptyState(String src, String dest) {
        VBox empty = new VBox(12);
        empty.setAlignment(Pos.CENTER);
        empty.getStyleClass().add("card");
        empty.setPadding(new Insets(36));

        Label emoji = new Label("\uD83D\uDE8C");
        emoji.setStyle("-fx-font-size: 36px;");

        Label msg = new Label("No Direct Buses Scheduled for this route today");
        msg.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        Label hint = new Label("Try choosing alternative popular routes such as Delhi \u2192 Jaipur or Mumbai \u2192 Pune.");
        hint.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748B;");

        Button btnReset = new Button("View Available Fleet");
        btnReset.getStyleClass().add("btn-primary");
        btnReset.setOnAction(e -> showAllBuses());

        empty.getChildren().addAll(emoji, msg, hint, btnReset);
        return empty;
    }
}
