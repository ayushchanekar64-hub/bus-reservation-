package com.busreservation.view.component;

import com.busreservation.model.Seat;
import com.busreservation.model.SeatStatus;
import com.busreservation.model.SeatType;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.function.Consumer;

/**
 * Custom JavaFX Component representing a realistic top-view bus passenger seat.
 * Features:
 * - Top-view seat aesthetics (Headrest + Cushion)
 * - Color indicator: Green (Available), Grey (Booked), Blue Outline (Selected)
 * - Tooltip with seat number, type, and fare
 * - Reactive click toggle
 */
public class SeatButton extends StackPane {

    private final Seat seat;
    private final double fare;
    private final Consumer<Seat> onToggle;
    private boolean selected = false;

    private final Rectangle seatBody;
    private final Rectangle headrest;
    private final Label seatLabel;

    public SeatButton(Seat seat, double fare, Consumer<Seat> onToggle) {
        this.seat = seat;
        this.fare = fare;
        this.onToggle = onToggle;

        boolean isSleeper = seat.getSeatType() == SeatType.SLEEPER_LOWER ||
                            seat.getSeatType() == SeatType.SLEEPER_UPPER ||
                            seat.getSeatNumber().endsWith("S");

        double width = isSleeper ? 64 : 44;
        double height = isSleeper ? 54 : 44;

        setPrefSize(width, height);
        setMaxSize(width, height);
        setMinSize(width, height);

        // Main Seat Cushion Body
        seatBody = new Rectangle(width, height);
        seatBody.setArcWidth(10);
        seatBody.setArcHeight(10);

        // Top Headrest Cushion
        headrest = new Rectangle(width * 0.75, 8);
        headrest.setArcWidth(4);
        headrest.setArcHeight(4);

        // Seat Number Label
        seatLabel = new Label(seat.getSeatNumber());
        seatLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold;");

        VBox contentBox = new VBox(2, headrest, seatLabel);
        contentBox.setAlignment(Pos.CENTER);

        getChildren().addAll(seatBody, contentBox);

        // Tooltip
        String tooltipText = String.format("Seat %s (%s)\nFare: \u20B9%.2f\nStatus: %s",
                seat.getSeatNumber(),
                seat.getSeatType().getDisplayName(),
                fare,
                seat.getSeatStatus().getLabel());
        Tooltip.install(this, new Tooltip(tooltipText));

        applyStatusStyling();

        // Interaction
        if (seat.getSeatStatus() == SeatStatus.AVAILABLE) {
            setCursor(javafx.scene.Cursor.HAND);
            setOnMouseClicked(e -> toggleSelection());
        } else {
            setCursor(javafx.scene.Cursor.DEFAULT);
            setDisable(true);
        }
    }

    public void toggleSelection() {
        if (seat.getSeatStatus() != SeatStatus.AVAILABLE && !selected) {
            return;
        }

        selected = !selected;
        applyStatusStyling();

        if (onToggle != null) {
            onToggle.accept(seat);
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean sel) {
        this.selected = sel;
        applyStatusStyling();
    }

    public Seat getSeat() {
        return seat;
    }

    private void applyStatusStyling() {
        if (selected) {
            // BLUE OUTLINE & HIGHLIGHT (Selected)
            seatBody.setFill(Color.web("#3B82F6")); // Vibrant Royal Blue
            seatBody.setStroke(Color.web("#1D4ED8"));
            seatBody.setStrokeWidth(2.5);

            headrest.setFill(Color.web("#1D4ED8"));
            seatLabel.setTextFill(Color.WHITE);

            DropShadow glow = new DropShadow(8, Color.web("#3B82F6", 0.5));
            setEffect(glow);

        } else if (seat.getSeatStatus() == SeatStatus.BOOKED) {
            // GREY (Already booked)
            seatBody.setFill(Color.web("#E2E8F0"));
            seatBody.setStroke(Color.web("#CBD5E1"));
            seatBody.setStrokeWidth(1.0);

            headrest.setFill(Color.web("#94A3B8"));
            seatLabel.setTextFill(Color.web("#64748B"));
            setEffect(null);

        } else {
            // GREEN (Available)
            seatBody.setFill(Color.web("#DCFCE7")); // Soft green fill
            seatBody.setStroke(Color.web("#16A34A")); // Bold green outline
            seatBody.setStrokeWidth(1.5);

            headrest.setFill(Color.web("#22C55E"));
            seatLabel.setTextFill(Color.web("#15803D"));
            setEffect(null);
        }
    }
}
