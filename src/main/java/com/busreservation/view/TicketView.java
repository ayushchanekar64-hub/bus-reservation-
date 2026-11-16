package com.busreservation.view;

import com.busreservation.model.Booking;
import com.busreservation.model.BookingPassenger;
import com.busreservation.util.DateTimeUtil;
import com.busreservation.util.PDFGenerator;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Screen presenting the finalized Boarding Pass / E-Ticket with PDF generation and printing.
 */
public class TicketView extends ScrollPane {

    private final ViewManager viewManager;
    private final Booking booking;
    private VBox ticketCard;

    public TicketView(ViewManager viewManager, Booking booking) {
        this.viewManager = viewManager;
        this.booking = booking;

        setFitToWidth(true);
        setContent(buildContent());
    }

    private VBox buildContent() {
        VBox root = new VBox(24);
        root.setPadding(new Insets(24, 36, 36, 36));
        root.setAlignment(Pos.TOP_CENTER);

        // 1. TOP SUCCESS ALERT
        HBox successAlert = createSuccessAlert();

        // 2. BOARDING PASS TICKET CARD
        ticketCard = createTicketCard();

        // 3. ACTION BUTTONS (Save PDF, Print, Return to Dashboard)
        HBox actionsRow = createActionsRow();

        root.getChildren().addAll(successAlert, ticketCard, actionsRow);
        return root;
    }

    private HBox createSuccessAlert() {
        HBox alert = new HBox(12);
        alert.setAlignment(Pos.CENTER);
        alert.setMaxWidth(720);
        alert.setStyle("-fx-background-color: #DCFCE7; -fx-padding: 14px 24px; -fx-background-radius: 12px; -fx-border-color: #86EFAC; -fx-border-radius: 12px;");

        Label checkIcon = new Label("\u2705");
        checkIcon.setStyle("-fx-font-size: 20px;");

        VBox text = new VBox(2);
        Label title = new Label("Reservation Confirmed Successfully!");
        title.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #15803D;");
        Label sub = new Label("Your electronic ticket has been generated. PNR: " + booking.getPnrNumber());
        sub.setStyle("-fx-font-size: 12px; -fx-text-fill: #166534;");
        text.getChildren().addAll(title, sub);

        alert.getChildren().addAll(checkIcon, text);
        return alert;
    }

    private VBox createTicketCard() {
        VBox card = new VBox(0);
        card.setMaxWidth(720);
        card.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 16px; -fx-border-color: #E2E8F0; -fx-border-radius: 16px; -fx-effect: dropshadow(gaussian, rgba(15, 23, 42, 0.08), 16, 0, 0, 6);");

        // Top Royal Blue Banner
        HBox topBanner = new HBox(16);
        topBanner.setAlignment(Pos.CENTER_LEFT);
        topBanner.setStyle("-fx-background-color: linear-gradient(to right, #1E3A8A, #2563EB); -fx-padding: 18px 24px; -fx-background-radius: 15px 15px 0 0;");

        Label appLogo = new Label("SwiftBus");
        appLogo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");

        Label docType = new Label("OFFICIAL ELECTRONIC TICKET / BOARDING PASS");
        docType.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #BFDBFE; -fx-background-color: rgba(255,255,255,0.15); -fx-padding: 4px 10px; -fx-background-radius: 20px;");

        Region spTop = new Region();
        HBox.setHgrow(spTop, Priority.ALWAYS);

        VBox pnrBox = new VBox(2);
        pnrBox.setAlignment(Pos.CENTER_RIGHT);
        Label pnrHead = new Label("PNR NUMBER");
        pnrHead.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #93C5FD;");
        Label pnrVal = new Label(booking.getPnrNumber());
        pnrVal.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFFFFF;");
        pnrBox.getChildren().addAll(pnrHead, pnrVal);

        topBanner.getChildren().addAll(appLogo, docType, spTop, pnrBox);

        // 24x7 Support Ribbon
        HBox supportBar = new HBox(12);
        supportBar.setAlignment(Pos.CENTER_LEFT);
        supportBar.setStyle("-fx-background-color: #F1F5F9; -fx-padding: 8px 24px; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1px 0;");
        Label helpText = new Label("📞 24x7 Helpline: 1800-102-8899 (Toll Free)  |  ✉ support@swiftbus.in");
        helpText.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        Region spSup = new Region();
        HBox.setHgrow(spSup, Priority.ALWAYS);
        Label confPill = new Label("● CONFIRMED");
        confPill.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #15803D;");
        supportBar.getChildren().addAll(helpText, spSup, confPill);

        // Ticket Body
        VBox body = new VBox(18);
        body.setPadding(new Insets(24));

        // Route & Bus Overview
        HBox routeOverview = new HBox(20);
        routeOverview.setAlignment(Pos.CENTER_LEFT);
        routeOverview.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 14px 18px; -fx-background-radius: 10px;");

        VBox origin = new VBox(2);
        Label origCity = new Label(booking.getBus() != null ? booking.getBus().getSource() : "Origin");
        origCity.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label origTime = new Label(booking.getBus() != null ? DateTimeUtil.formatTime(booking.getBus().getDepartureTime()) : "");
        origTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        origin.getChildren().addAll(origCity, origTime);

        Label arrow = new Label("\u2500\u2500\u2500\u25B6");
        arrow.setStyle("-fx-font-size: 16px; -fx-text-fill: #2563EB; -fx-font-weight: bold;");

        VBox destination = new VBox(2);
        Label destCity = new Label(booking.getBus() != null ? booking.getBus().getDestination() : "Destination");
        destCity.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        Label destTime = new Label(booking.getBus() != null ? DateTimeUtil.formatTime(booking.getBus().getArrivalTime()) : "");
        destTime.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");
        destination.getChildren().addAll(destCity, destTime);

        Region spRoute = new Region();
        HBox.setHgrow(spRoute, Priority.ALWAYS);

        VBox dateBox = new VBox(2);
        dateBox.setAlignment(Pos.CENTER_RIGHT);
        Label jDateLbl = new Label("Journey Date");
        jDateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        Label jDateVal = new Label(DateTimeUtil.formatDate(booking.getJourneyDate()));
        jDateVal.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        dateBox.getChildren().addAll(jDateLbl, jDateVal);

        routeOverview.getChildren().addAll(origin, arrow, destination, spRoute, dateBox);

        // Bus Specifications Grid
        GridPane busGrid = new GridPane();
        busGrid.setHgap(32);
        busGrid.setVgap(10);

        addGridInfo(busGrid, 0, 0, "Bus Operator:", booking.getBus() != null ? booking.getBus().getBusName() : "N/A");
        addGridInfo(busGrid, 1, 0, "Bus Number:", booking.getBus() != null ? booking.getBus().getBusNumber() : "N/A");
        addGridInfo(busGrid, 0, 1, "Bus Category:", booking.getBus() != null ? booking.getBus().getBusType() : "N/A");
        addGridInfo(busGrid, 1, 1, "Status:", booking.getBookingStatus().name());

        // Passenger Table
        VBox passTable = new VBox(8);
        Label passHeader = new Label("PASSENGER DETAILS & SEAT ALLOCATION");
        passHeader.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        VBox tableContent = new VBox(6);
        for (BookingPassenger bp : booking.getBookingPassengers()) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 0 0 1px 0; -fx-padding: 6px 0;");

            Label name = new Label(bp.getPassenger() != null ? bp.getPassenger().getFullName() : "N/A");
            name.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #1E293B;");

            Label meta = new Label(bp.getPassenger() != null ? "(" + bp.getPassenger().getAge() + " yrs, " + bp.getPassenger().getGender() + ")" : "");
            meta.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748B;");

            Region sp = new Region();
            HBox.setHgrow(sp, Priority.ALWAYS);

            Label seatTag = new Label("Seat " + (bp.getSeat() != null ? bp.getSeat().getSeatNumber() : "N/A"));
            seatTag.setStyle("-fx-background-color: #EFF6FF; -fx-text-fill: #2563EB; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 3px 8px; -fx-background-radius: 4px;");

            Label fare = new Label(String.format("\u20B9%.2f", bp.getSeatFare()));
            fare.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #0F172A;");

            row.getChildren().addAll(name, meta, sp, seatTag, fare);
            tableContent.getChildren().add(row);
        }
        passTable.getChildren().addAll(passHeader, tableContent);

        // Bottom Perforated Divider
        Line dashed = new Line(0, 0, 670, 0);
        dashed.setStroke(Color.web("#CBD5E1"));
        dashed.getStrokeDashArray().addAll(6.0, 6.0);

        // Bottom Total & Barcode simulation
        HBox bottomMeta = new HBox(20);
        bottomMeta.setAlignment(Pos.CENTER_LEFT);

        VBox totalBox = new VBox(2);
        Label totalHead = new Label("TOTAL AMOUNT PAID (INCL. GST)");
        totalHead.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #64748B;");
        Label totalVal = new Label(String.format("\u20B9%.2f", booking.getTotalFare()));
        totalVal.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #16A34A;");
        totalBox.getChildren().addAll(totalHead, totalVal);

        Region spBottom = new Region();
        HBox.setHgrow(spBottom, Priority.ALWAYS);

        // Simulated Barcode graphic
        HBox barcodeBox = new HBox(3);
        barcodeBox.setAlignment(Pos.CENTER_RIGHT);
        int[] barWidths = {2, 1, 3, 1, 4, 2, 1, 3, 2, 4, 1, 2, 3, 1, 2, 4, 1, 3};
        for (int w : barWidths) {
            Rectangle bar = new Rectangle(w * 1.5, 34, Color.web("#334155"));
            barcodeBox.getChildren().add(bar);
        }

        bottomMeta.getChildren().addAll(totalBox, spBottom, barcodeBox);

        // Perforated Conductor Boarding Slip
        VBox conductorSlip = new VBox(8);
        conductorSlip.setStyle("-fx-background-color: #F8FAFC; -fx-padding: 12px 18px; -fx-background-radius: 8px; -fx-border-color: #CBD5E1; -fx-border-style: dashed;");
        
        HBox stubHead = new HBox(12);
        stubHead.setAlignment(Pos.CENTER_LEFT);
        Label stubTitle = new Label("\u2702 CONDUCTOR BOARDING SLIP (OPERATOR AUDIT COPY)");
        stubTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        Region spStub = new Region();
        HBox.setHgrow(spStub, Priority.ALWAYS);
        Label stubPnr = new Label("PNR: " + booking.getPnrNumber());
        stubPnr.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #1E293B;");
        stubHead.getChildren().addAll(stubTitle, spStub, stubPnr);

        HBox stubBody = new HBox(16);
        stubBody.setAlignment(Pos.CENTER_LEFT);
        Label stubBus = new Label("Bus: " + (booking.getBus() != null ? booking.getBus().getBusNumber() : "N/A"));
        stubBus.setStyle("-fx-font-size: 11px; -fx-text-fill: #475569;");
        Label stubSeats = new Label("Seats: " + booking.getSeatNumbersFormatted());
        stubSeats.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #2563EB;");
        Region spSign = new Region();
        HBox.setHgrow(spSign, Priority.ALWAYS);
        Label signLine = new Label("Conductor Signature: [___________________]");
        signLine.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748B;");
        stubBody.getChildren().addAll(stubBus, stubSeats, spSign, signLine);

        conductorSlip.getChildren().addAll(stubHead, stubBody);

        // Passenger Guidelines Box
        VBox guidelinesBox = new VBox(4);
        guidelinesBox.setStyle("-fx-background-color: #FEF3C7; -fx-padding: 10px 14px; -fx-background-radius: 8px; -fx-border-color: #FDE68A;");
        Label gTitle = new Label("\u26A0 PASSENGER BOARDING GUIDELINES:");
        gTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #92400E;");
        Label g1 = new Label("\u2022 Photo ID: Original Government ID (Aadhaar / Voter ID / DL / PAN) is mandatory during boarding.");
        g1.setStyle("-fx-font-size: 10.5px; -fx-text-fill: #92400E;");
        Label g2 = new Label("\u2022 Reporting: Please report at the boarding point 15-30 minutes prior to scheduled departure.");
        g2.setStyle("-fx-font-size: 10.5px; -fx-text-fill: #92400E;");
        Label g3 = new Label("\u2022 Digital Ticket: This digital boarding pass is valid. Physical paper printout is optional.");
        g3.setStyle("-fx-font-size: 10.5px; -fx-text-fill: #92400E;");
        guidelinesBox.getChildren().addAll(gTitle, g1, g2, g3);

        body.getChildren().addAll(routeOverview, busGrid, passTable, dashed, bottomMeta, conductorSlip, guidelinesBox);
        card.getChildren().addAll(topBanner, supportBar, body);
        return card;
    }

    private void addGridInfo(GridPane grid, int col, int row, String lbl, String val) {
        VBox box = new VBox(2);
        Label l = new Label(lbl);
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748B;");
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #1E293B;");
        box.getChildren().addAll(l, v);
        grid.add(box, col, row);
    }

    private HBox createActionsRow() {
        HBox bar = new HBox(16);
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 0, 20, 0));

        Button btnDashboard = new Button("\uD83C\uDFE0 Return to Dashboard");
        btnDashboard.getStyleClass().add("btn-secondary");
        btnDashboard.setOnAction(e -> viewManager.showDashboard());

        Button btnPrint = new Button("\uD83D\uDDA8 Print Ticket");
        btnPrint.getStyleClass().add("btn-secondary");
        btnPrint.setOnAction(e -> handlePrint());

        Button btnPdf = new Button("\uD83D\uDCE5 Download Ticket (PDF)");
        btnPdf.getStyleClass().add("btn-primary");
        btnPdf.setOnAction(e -> handleDownloadPdf());

        bar.getChildren().addAll(btnDashboard, btnPrint, btnPdf);
        return bar;
    }

    private void handleDownloadPdf() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Ticket PDF");
        chooser.setInitialFileName("Ticket_" + booking.getPnrNumber() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf"));

        File file = chooser.showSaveDialog(viewManager.getPrimaryStage());
        if (file != null) {
            try {
                PDFGenerator.generatePdfTicket(booking, file);
                viewManager.showToast("Ticket successfully saved to: " + file.getName(), false);
            } catch (Exception ex) {
                // If PDF generation encounters an issue, fallback to receipt text
                try {
                    File textFile = new File(file.getParentFile(), "Ticket_" + booking.getPnrNumber() + ".txt");
                    PDFGenerator.generateTextReceipt(booking, textFile);
                    viewManager.showToast("Ticket text receipt saved to: " + textFile.getName(), false);
                } catch (Exception e) {
                    viewManager.showToast("Failed to save ticket: " + e.getMessage(), true);
                }
            }
        }
    }

    private void handlePrint() {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(viewManager.getPrimaryStage());
            if (proceed) {
                boolean success = job.printPage(ticketCard);
                if (success) {
                    job.endJob();
                    viewManager.showToast("Print job dispatched successfully.", false);
                } else {
                    viewManager.showToast("Print job failed to complete.", true);
                }
            }
        } else {
            viewManager.showToast("No active printer detected on your system.", true);
        }
    }
}
