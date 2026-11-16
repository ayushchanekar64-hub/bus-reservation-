package com.busreservation.view;

import com.busreservation.model.Bus;
import com.busreservation.model.Passenger;
import com.busreservation.model.Seat;
import com.busreservation.service.FareService;
import com.busreservation.util.DateTimeUtil;
import com.busreservation.util.ValidationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Screen for collecting passenger information for each reserved seat.
 */
public class PassengerDetailsView extends ScrollPane {

    private final ViewManager viewManager;
    private final Bus bus;
    private final LocalDate journeyDate;
    private final List<Seat> selectedSeats;
    private final FareService fareService;

    // Contact Details
    private TextField contactPhoneField;
    private TextField contactEmailField;

    // List of Passenger Form Rows
    private final List<PassengerRow> passengerRows = new ArrayList<>();

    public PassengerDetailsView(ViewManager viewManager, Bus bus, LocalDate journeyDate,
                                List<Seat> selectedSeats, FareService fareService) {
        this.viewManager = viewManager;
        this.bus = bus;
        this.journeyDate = journeyDate;
        this.selectedSeats = selectedSeats;
        this.fareService = fareService;

        setFitToWidth(true);
        setContent(buildContent());
    }

    private VBox buildContent() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(24, 36, 36, 36));

        // 1. HEADER
        HBox header = createHeader();

        // 2. CONTACT DETAILS CARD
        VBox contactCard = createContactCard();

        // 3. PASSENGERS FORM CONTAINER
        VBox passengersContainer = new VBox(16);
        Label pHeader = new Label("Passenger Information (" + selectedSeats.size() + " Passenger" + (selectedSeats.size() > 1 ? "s" : "") + ")");
        pHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        passengersContainer.getChildren().add(pHeader);

        int index = 1;
        for (Seat seat : selectedSeats) {
            PassengerRow row = new PassengerRow(index++, seat);
            passengerRows.add(row);
            passengersContainer.getChildren().add(row.getCard());
        }

        // 4. ACTION BAR (Back & Proceed)
        HBox actionBar = createActionBar();

        root.getChildren().addAll(header, contactCard, passengersContainer, actionBar);
        return root;
    }

    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("card");
        header.setPadding(new Insets(16, 22, 16, 22));

        Button btnBack = new Button("\u2190 Back to Seats");
        btnBack.getStyleClass().add("btn-secondary");
        btnBack.setOnAction(e -> viewManager.showSeatSelection(bus, journeyDate));

        VBox info = new VBox(2);
        Label route = new Label("Passenger Details \u2022 " + bus.getSource() + " \u2192 " + bus.getDestination());
        route.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label meta = new Label(bus.getBusName() + " \u2022 Date: " + DateTimeUtil.formatDate(journeyDate) +
                " \u2022 " + selectedSeats.size() + " Seats Reserved");
        meta.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        info.getChildren().addAll(route, meta);

        header.getChildren().addAll(btnBack, info);
        return header;
    }

    private VBox createContactCard() {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");

        Label title = new Label("Contact Details (For E-Ticket & SMS confirmation)");
        title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

        HBox fields = new HBox(18);
        fields.setAlignment(Pos.CENTER_LEFT);

        VBox phoneBox = new VBox(4);
        Label phoneLbl = new Label("Mobile Number *");
        phoneLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        contactPhoneField = new TextField();
        contactPhoneField.setPromptText("10-digit mobile number");
        contactPhoneField.setPrefWidth(220);
        phoneBox.getChildren().addAll(phoneLbl, contactPhoneField);

        VBox emailBox = new VBox(4);
        Label emailLbl = new Label("Email Address (Optional)");
        emailLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        contactEmailField = new TextField();
        contactEmailField.setPromptText("e.g. user@example.com");
        contactEmailField.setPrefWidth(260);
        emailBox.getChildren().addAll(emailLbl, contactEmailField);

        fields.getChildren().addAll(phoneBox, emailBox);
        card.getChildren().addAll(title, fields);
        return card;
    }

    private HBox createActionBar() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(10, 0, 20, 0));

        Button btnCancel = new Button("Back to Seat Selection");
        btnCancel.getStyleClass().add("btn-secondary");
        btnCancel.setOnAction(e -> viewManager.showSeatSelection(bus, journeyDate));

        Button btnContinue = new Button("Review & Confirm Booking \u2192");
        btnContinue.getStyleClass().add("btn-primary");
        btnContinue.setOnAction(e -> handleValidationAndProceed());

        bar.getChildren().addAll(btnCancel, btnContinue);
        return bar;
    }

    private void handleValidationAndProceed() {
        String phone = contactPhoneField.getText() != null ? contactPhoneField.getText().trim() : "";
        String email = contactEmailField.getText() != null ? contactEmailField.getText().trim() : "";

        if (!ValidationUtil.isValidMobile(phone)) {
            viewManager.showToast("Please enter a valid 10-digit contact mobile number.", true);
            contactPhoneField.requestFocus();
            return;
        }

        if (!email.isEmpty() && !ValidationUtil.isValidEmail(email)) {
            viewManager.showToast("Please enter a valid email address.", true);
            contactEmailField.requestFocus();
            return;
        }

        List<Passenger> passengers = new ArrayList<>();
        for (PassengerRow row : passengerRows) {
            String name = row.nameField.getText() != null ? row.nameField.getText().trim() : "";
            String ageStr = row.ageField.getText() != null ? row.ageField.getText().trim() : "";
            String gender = row.genderCombo.getValue();

            if (!ValidationUtil.isValidFullName(name)) {
                viewManager.showToast("Passenger " + row.index + ": Please enter a valid name (letters only).", true);
                row.nameField.requestFocus();
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (!ValidationUtil.isValidAge(age)) {
                    viewManager.showToast("Passenger " + row.index + ": Age must be between 1 and 120.", true);
                    row.ageField.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                viewManager.showToast("Passenger " + row.index + ": Please enter a valid numerical age.", true);
                row.ageField.requestFocus();
                return;
            }

            if (gender == null || gender.isBlank()) {
                viewManager.showToast("Passenger " + row.index + ": Please select a gender.", true);
                return;
            }

            Passenger p = new Passenger(name, age, gender, phone);
            p.setEmail(email);
            passengers.add(p);
        }

        viewManager.showBookingSummary(bus, journeyDate, selectedSeats, passengers, phone, email);
    }

    private static class PassengerRow {
        final int index;
        final Seat seat;
        final TextField nameField;
        final TextField ageField;
        final ComboBox<String> genderCombo;
        final VBox card;

        PassengerRow(int index, Seat seat) {
            this.index = index;
            this.seat = seat;

            card = new VBox(12);
            card.getStyleClass().add("card");
            card.setPadding(new Insets(16, 20, 16, 20));

            HBox titleRow = new HBox(12);
            titleRow.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label("Passenger " + index);
            title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");

            Label seatBadge = new Label("Seat: " + seat.getSeatNumber() + " (" + seat.getSeatType().getDisplayName() + ")");
            seatBadge.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 6px;");

            titleRow.getChildren().addAll(title, seatBadge);

            HBox formFields = new HBox(16);
            formFields.setAlignment(Pos.CENTER_LEFT);

            // Name
            VBox nameBox = new VBox(4);
            Label nLbl = new Label("Full Name *");
            nLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            nameField = new TextField();
            nameField.setPromptText("e.g. John Doe");
            nameField.setPrefWidth(240);
            nameBox.getChildren().addAll(nLbl, nameField);

            // Age
            VBox ageBox = new VBox(4);
            Label aLbl = new Label("Age *");
            aLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            ageField = new TextField();
            ageField.setPromptText("Age");
            ageField.setPrefWidth(80);
            ageBox.getChildren().addAll(aLbl, ageField);

            // Gender
            VBox genderBox = new VBox(4);
            Label gLbl = new Label("Gender *");
            gLbl.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
            genderCombo = new ComboBox<>();
            genderCombo.getItems().addAll("Male", "Female", "Other");
            genderCombo.setValue("Male");
            genderCombo.setPrefWidth(120);
            genderBox.getChildren().addAll(gLbl, genderCombo);

            formFields.getChildren().addAll(nameBox, ageBox, genderBox);
            card.getChildren().addAll(titleRow, formFields);
        }

        VBox getCard() {
            return card;
        }
    }
}
