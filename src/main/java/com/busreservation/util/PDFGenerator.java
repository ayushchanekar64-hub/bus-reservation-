package com.busreservation.util;

import com.busreservation.model.Booking;
import com.busreservation.model.BookingPassenger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates electronic ticket PDF documents and printable text receipts.
 */
public final class PDFGenerator {

    private PDFGenerator() {
    }

    /**
     * Generates a PDF E-Ticket using Apache PDFBox.
     */
    public static File generatePdfTicket(Booking booking, File destinationFile) throws IOException {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        if (destinationFile == null) {
            String home = System.getProperty("user.home");
            destinationFile = new File(home, "Ticket_" + booking.getPnrNumber() + ".pdf");
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                float margin = 50;
                float y = page.getMediaBox().getHeight() - margin;

                // Header Banner
                cs.setNonStrokingColor(37 / 255f, 99 / 255f, 235 / 255f); // Royal Blue
                cs.addRect(margin, y - 40, page.getMediaBox().getWidth() - (2 * margin), 45);
                cs.fill();

                // Header Title
                cs.setNonStrokingColor(1, 1, 1); // White
                cs.beginText();
                cs.setFont(fontBold, 18);
                cs.newLineAtOffset(margin + 15, y - 24);
                cs.showText("BUS RESERVATION SYSTEM - OFFICIAL E-TICKET");
                cs.endText();

                y -= 65;

                // PNR & Status Row
                cs.setNonStrokingColor(0.1f, 0.1f, 0.1f);
                cs.beginText();
                cs.setFont(fontBold, 14);
                cs.newLineAtOffset(margin, y);
                cs.showText("PNR: " + booking.getPnrNumber());
                cs.endText();

                cs.beginText();
                cs.setFont(fontBold, 14);
                cs.newLineAtOffset(page.getMediaBox().getWidth() - margin - 150, y);
                cs.showText("Status: " + booking.getBookingStatus().name());
                cs.endText();

                y -= 25;

                // Journey Details Box
                cs.setNonStrokingColor(243 / 255f, 244 / 255f, 246 / 255f); // Light grey box
                cs.addRect(margin, y - 90, page.getMediaBox().getWidth() - (2 * margin), 95);
                cs.fill();

                cs.setNonStrokingColor(0, 0, 0);
                float infoY = y - 20;

                drawText(cs, fontBold, 11, margin + 15, infoY, "Bus Name:");
                drawText(cs, fontRegular, 11, margin + 100, infoY, booking.getBus() != null ? booking.getBus().getBusName() : "N/A");

                drawText(cs, fontBold, 11, margin + 280, infoY, "Bus Number:");
                drawText(cs, fontRegular, 11, margin + 370, infoY, booking.getBus() != null ? booking.getBus().getBusNumber() : "N/A");

                infoY -= 20;
                drawText(cs, fontBold, 11, margin + 15, infoY, "Route:");
                drawText(cs, fontRegular, 11, margin + 100, infoY, (booking.getBus() != null ? booking.getBus().getSource() : "") + " to " + (booking.getBus() != null ? booking.getBus().getDestination() : ""));

                drawText(cs, fontBold, 11, margin + 280, infoY, "Bus Type:");
                drawText(cs, fontRegular, 11, margin + 370, infoY, booking.getBus() != null ? booking.getBus().getBusType() : "N/A");

                infoY -= 20;
                drawText(cs, fontBold, 11, margin + 15, infoY, "Journey Date:");
                drawText(cs, fontRegular, 11, margin + 100, infoY, DateTimeUtil.formatDate(booking.getJourneyDate()));

                drawText(cs, fontBold, 11, margin + 280, infoY, "Departure:");
                drawText(cs, fontRegular, 11, margin + 370, infoY, booking.getBus() != null ? DateTimeUtil.formatTime(booking.getBus().getDepartureTime()) : "N/A");

                y -= 115;

                // Passenger Details Section
                drawText(cs, fontBold, 13, margin, y, "PASSENGER & SEAT ALLOCATION");
                y -= 15;

                // Table Header
                cs.setNonStrokingColor(229 / 255f, 231 / 255f, 235 / 255f);
                cs.addRect(margin, y - 18, page.getMediaBox().getWidth() - (2 * margin), 20);
                cs.fill();

                cs.setNonStrokingColor(0, 0, 0);
                drawText(cs, fontBold, 10, margin + 10, y - 12, "Sr.");
                drawText(cs, fontBold, 10, margin + 40, y - 12, "Passenger Name");
                drawText(cs, fontBold, 10, margin + 200, y - 12, "Age / Gender");
                drawText(cs, fontBold, 10, margin + 300, y - 12, "Seat No.");
                drawText(cs, fontBold, 10, margin + 380, y - 12, "Seat Type");
                drawText(cs, fontBold, 10, margin + 450, y - 12, "Fare (INR)");

                y -= 25;

                int sr = 1;
                for (BookingPassenger bp : booking.getBookingPassengers()) {
                    drawText(cs, fontRegular, 10, margin + 10, y, String.valueOf(sr++));
                    drawText(cs, fontRegular, 10, margin + 40, y, bp.getPassenger() != null ? bp.getPassenger().getFullName() : "N/A");
                    drawText(cs, fontRegular, 10, margin + 200, y, (bp.getPassenger() != null ? bp.getPassenger().getAge() + " yrs / " + bp.getPassenger().getGender() : "N/A"));
                    drawText(cs, fontBold, 10, margin + 300, y, bp.getSeat() != null ? bp.getSeat().getSeatNumber() : "N/A");
                    drawText(cs, fontRegular, 10, margin + 380, y, bp.getSeat() != null ? bp.getSeat().getSeatType().getDisplayName() : "N/A");
                    drawText(cs, fontRegular, 10, margin + 450, y, String.format("%.2f", bp.getSeatFare()));
                    y -= 18;
                }

                y -= 20;

                // Total Fare Summary Card
                cs.setNonStrokingColor(238 / 255f, 242 / 255f, 255 / 255f); // Indigo tint
                cs.addRect(margin + 260, y - 50, 235, 55);
                cs.fill();

                cs.setNonStrokingColor(30 / 255f, 58 / 255f, 138 / 255f);
                drawText(cs, fontBold, 11, margin + 275, y - 18, "Seats Booked: " + booking.getSeatCount());
                drawText(cs, fontBold, 13, margin + 275, y - 38, "TOTAL AMOUNT PAID:  INR " + String.format("%.2f", booking.getTotalFare()));

                y -= 80;

                // Footer & Terms
                cs.setNonStrokingColor(107 / 255f, 114 / 255f, 128 / 255f);
                drawText(cs, fontBold, 10, margin, y, "IMPORTANT INFORMATION & BOARDING GUIDELINES:");
                y -= 14;
                drawText(cs, fontRegular, 9, margin, y, "1. Please report at the boarding point at least 15 minutes before scheduled departure.");
                y -= 12;
                drawText(cs, fontRegular, 9, margin, y, "2. Carry a valid Government Photo ID (Aadhaar / Driving License / Voter ID / Passport).");
                y -= 12;
                drawText(cs, fontRegular, 9, margin, y, "3. For cancellations, visit the 'My Bookings' section with your PNR number.");
                y -= 12;
                drawText(cs, fontRegular, 9, margin, y, "4. Booked on: " + DateTimeUtil.formatDateTime(booking.getBookingDate()));
            }

            document.save(destinationFile);
        }

        return destinationFile;
    }

    private static void drawText(PDPageContentStream cs, PDType1Font font, float fontSize, float x, float y, String text) throws IOException {
        if (text == null) text = "";
        cs.beginText();
        cs.setFont(font, fontSize);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
    }

    /**
     * Fallback text receipt generator.
     */
    public static File generateTextReceipt(Booking booking, File destinationFile) throws IOException {
        if (destinationFile == null) {
            String home = System.getProperty("user.home");
            destinationFile = new File(home, "Ticket_" + booking.getPnrNumber() + ".txt");
        }

        try (FileWriter writer = new FileWriter(destinationFile)) {
            writer.write("=================================================================\n");
            writer.write("            BUS RESERVATION SYSTEM - OFFICIAL E-TICKET           \n");
            writer.write("=================================================================\n");
            writer.write(" PNR NUMBER      : " + booking.getPnrNumber() + "\n");
            writer.write(" BOOKING STATUS  : " + booking.getBookingStatus() + "\n");
            writer.write(" BOOKING DATE    : " + DateTimeUtil.formatDateTime(booking.getBookingDate()) + "\n");
            writer.write("-----------------------------------------------------------------\n");
            writer.write(" BUS OPERATOR    : " + (booking.getBus() != null ? booking.getBus().getBusName() : "N/A") + "\n");
            writer.write(" BUS NUMBER      : " + (booking.getBus() != null ? booking.getBus().getBusNumber() : "N/A") + "\n");
            writer.write(" BUS TYPE        : " + (booking.getBus() != null ? booking.getBus().getBusType() : "N/A") + "\n");
            writer.write(" ROUTE           : " + (booking.getBus() != null ? booking.getBus().getSource() : "") + " -> " + (booking.getBus() != null ? booking.getBus().getDestination() : "") + "\n");
            writer.write(" JOURNEY DATE    : " + DateTimeUtil.formatDate(booking.getJourneyDate()) + "\n");
            writer.write(" DEPARTURE TIME  : " + (booking.getBus() != null ? DateTimeUtil.formatTime(booking.getBus().getDepartureTime()) : "N/A") + "\n");
            writer.write("-----------------------------------------------------------------\n");
            writer.write(" PASSENGERS & SEATS:\n");
            for (BookingPassenger bp : booking.getBookingPassengers()) {
                writer.write("  * " + (bp.getPassenger() != null ? bp.getPassenger().getFullName() : "N/A") +
                        " (" + (bp.getPassenger() != null ? bp.getPassenger().getAge() + "y, " + bp.getPassenger().getGender() : "") + ") " +
                        "- Seat: " + (bp.getSeat() != null ? bp.getSeat().getSeatNumber() : "N/A") +
                        " | Fare: INR " + bp.getSeatFare() + "\n");
            }
            writer.write("-----------------------------------------------------------------\n");
            writer.write(" TOTAL FARE PAID : INR " + String.format("%.2f", booking.getTotalFare()) + "\n");
            writer.write("=================================================================\n");
            writer.write(" Have a safe journey! Powered by Bus Reservation System.\n");
        }

        return destinationFile;
    }
}
