package com.busreservation.view.component;

import com.busreservation.model.Bus;
import com.busreservation.model.Seat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * Realistic Top-View Bus Interior Floor Plan Visualizer.
 * Features:
 * - Aerodynamic bus hull with rounded windshield and side mirrors
 * - Detailed Driver cabin with steering wheel and driver seat
 * - Passenger entrance/exit door and rear emergency exit
 * - Central aisle separating left and right seating columns
 * - Supports 2+2 Seater, 2+1 Luxury, and Sleeper configurations
 */
public class BusLayoutPane extends StackPane {

    private final Bus bus;
    private final List<Seat> seats;
    private final Consumer<Seat> onSeatToggled;
    private final Map<String, SeatButton> seatButtonMap = new HashMap<>();

    public BusLayoutPane(Bus bus, List<Seat> seats, Consumer<Seat> onSeatToggled) {
        this.bus = bus;
        this.seats = seats;
        this.onSeatToggled = onSeatToggled;

        setAlignment(Pos.CENTER);
        setPadding(new Insets(20));

        buildBusTopView();
    }

    private void buildBusTopView() {
        // Outer bus container
        VBox busBody = new VBox();
        busBody.setAlignment(Pos.TOP_CENTER);
        busBody.getStyleClass().add("bus-hull");
        busBody.setMinWidth(360);
        busBody.setMaxWidth(420);
        busBody.setPadding(new Insets(0, 0, 20, 0));

        // 1. FRONT BUMPER & WINDSHIELD (AERODYNAMIC TOP VIEW)
        VBox frontCabin = createFrontCabin();

        // 2. PASSENGER SEAT GRID WITH CENTRAL AISLE
        VBox passengerCompartment = createPassengerCompartment();

        // 3. REAR COMPARTMENT WITH EMERGENCY EXIT
        VBox rearSection = createRearSection();

        busBody.getChildren().addAll(frontCabin, passengerCompartment, rearSection);

        // Add side mirrors for realistic bus outline
        HBox busWithMirrors = new HBox(6);
        busWithMirrors.setAlignment(Pos.TOP_CENTER);

        // Left and Right Side Mirror Shapes
        VBox leftMirror = createSideMirror(true);
        VBox rightMirror = createSideMirror(false);

        busWithMirrors.getChildren().addAll(leftMirror, busBody, rightMirror);

        getChildren().add(busWithMirrors);
    }

    private VBox createFrontCabin() {
        VBox cabin = new VBox(10);
        cabin.getStyleClass().add("driver-cabin");
        cabin.setPadding(new Insets(16, 20, 16, 20));
        cabin.setAlignment(Pos.CENTER);

        // Front Windshield Arc
        Arc windshield = new Arc(0, 0, 120, 25, 0, 180);
        windshield.setType(ArcType.CHORD);
        windshield.setFill(Color.web("#93C5FD", 0.45)); // Soft tinted glass
        windshield.setStroke(Color.web("#60A5FA"));
        windshield.setStrokeWidth(2);

        // Driver Area & Entrance Door Row
        HBox driverDoorRow = new HBox(20);
        driverDoorRow.setAlignment(Pos.CENTER);

        // Front Entrance / Exit Door
        VBox entranceDoor = new VBox(3);
        entranceDoor.setAlignment(Pos.CENTER);
        Rectangle doorGraphic = new Rectangle(50, 14);
        doorGraphic.setArcWidth(4);
        doorGraphic.setArcHeight(4);
        doorGraphic.setFill(Color.web("#FEF08A")); // Yellow entrance step
        doorGraphic.setStroke(Color.web("#EAB308"));
        doorGraphic.setStrokeWidth(1.5);
        Label doorLabel = new Label("ENTRANCE");
        doorLabel.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #854D0E;");
        entranceDoor.getChildren().addAll(doorGraphic, doorLabel);

        // Center Dashboard divider
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Steering Wheel and Driver Seat (Right-Hand Drive as standard in India)
        VBox driverStation = new VBox(4);
        driverStation.setAlignment(Pos.CENTER);

        // Steering wheel graphic
        StackPane steeringWheel = new StackPane();
        Circle outerWheel = new Circle(14);
        outerWheel.setFill(Color.TRANSPARENT);
        outerWheel.setStroke(Color.web("#334155"));
        outerWheel.setStrokeWidth(3);

        Circle innerHub = new Circle(4);
        innerHub.setFill(Color.web("#334155"));

        Line spokeH = new Line(-10, 0, 10, 0);
        spokeH.setStroke(Color.web("#334155"));
        spokeH.setStrokeWidth(2);

        Line spokeV = new Line(0, 0, 0, 10);
        spokeV.setStroke(Color.web("#334155"));
        spokeV.setStrokeWidth(2);

        steeringWheel.getChildren().addAll(outerWheel, spokeH, spokeV, innerHub);

        Label driverLabel = new Label("DRIVER");
        driverLabel.setStyle("-fx-font-size: 9px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        driverStation.getChildren().addAll(steeringWheel, driverLabel);

        driverDoorRow.getChildren().addAll(entranceDoor, spacer, driverStation);

        cabin.getChildren().addAll(windshield, driverDoorRow);
        return cabin;
    }

    private VBox createPassengerCompartment() {
        VBox compartment = new VBox(12);
        compartment.setPadding(new Insets(18, 16, 18, 16));
        compartment.setAlignment(Pos.TOP_CENTER);

        int cols = bus.getLayoutColumns();
        int aisleAfter = bus.getAisleAfterColumn();

        // Group seats by row
        Map<Integer, List<Seat>> rowsMap = new TreeMap<>();
        for (Seat s : seats) {
            rowsMap.computeIfAbsent(s.getRowNum(), k -> new ArrayList<>()).add(s);
        }

        for (Map.Entry<Integer, List<Seat>> entry : rowsMap.entrySet()) {
            HBox rowBox = new HBox(8);
            rowBox.setAlignment(Pos.CENTER);

            List<Seat> rowSeats = entry.getValue();
            rowSeats.sort(Comparator.comparingInt(Seat::getColNum));

            HBox leftGroup = new HBox(6);
            leftGroup.setAlignment(Pos.CENTER_RIGHT);

            HBox rightGroup = new HBox(6);
            rightGroup.setAlignment(Pos.CENTER_LEFT);

            for (Seat seat : rowSeats) {
                double seatFare = bus.calculateSeatFare(seat);
                SeatButton btn = new SeatButton(seat, seatFare, onSeatToggled);
                seatButtonMap.put(seat.getSeatNumber(), btn);

                if (seat.getColNum() <= aisleAfter) {
                    leftGroup.getChildren().add(btn);
                } else {
                    rightGroup.getChildren().add(btn);
                }
            }

            // Central Aisle Corridor Visual
            StackPane aisleCorridor = new StackPane();
            aisleCorridor.setPrefWidth(36);
            aisleCorridor.setMinWidth(32);
            Line aisleLine = new Line(0, 0, 0, 36);
            aisleLine.setStroke(Color.web("#E2E8F0"));
            aisleLine.setStrokeWidth(1.5);
            aisleLine.getStrokeDashArray().addAll(4.0, 4.0);
            aisleCorridor.getChildren().add(aisleLine);

            rowBox.getChildren().addAll(leftGroup, aisleCorridor, rightGroup);
            compartment.getChildren().add(rowBox);
        }

        return compartment;
    }

    private VBox createRearSection() {
        VBox rear = new VBox(8);
        rear.setAlignment(Pos.CENTER);
        rear.setPadding(new Insets(10, 20, 0, 20));

        // Emergency Exit Indicator
        HBox exitBox = new HBox(6);
        exitBox.setAlignment(Pos.CENTER);
        exitBox.setStyle("-fx-background-color: #DCFCE7; -fx-padding: 4px 12px; -fx-background-radius: 6px; -fx-border-color: #86EFAC; -fx-border-radius: 6px;");

        Circle dot = new Circle(4, Color.web("#16A34A"));
        Label exitText = new Label("EMERGENCY EXIT");
        exitText.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #15803D;");

        exitBox.getChildren().addAll(dot, exitText);

        rear.getChildren().add(exitBox);
        return rear;
    }

    private VBox createSideMirror(boolean isLeft) {
        VBox mirrorBox = new VBox();
        mirrorBox.setAlignment(Pos.TOP_CENTER);
        mirrorBox.setPadding(new Insets(30, 0, 0, 0));

        Rectangle arm = new Rectangle(14, 4);
        arm.setFill(Color.web("#475569"));

        Rectangle glass = new Rectangle(8, 22);
        glass.setArcWidth(4);
        glass.setArcHeight(4);
        glass.setFill(Color.web("#94A3B8"));
        glass.setStroke(Color.web("#334155"));
        glass.setStrokeWidth(1.5);

        if (isLeft) {
            mirrorBox.getChildren().addAll(arm, glass);
        } else {
            mirrorBox.getChildren().addAll(arm, glass);
        }

        return mirrorBox;
    }

    public void clearAllSelections() {
        for (SeatButton btn : seatButtonMap.values()) {
            if (btn.isSelected()) {
                btn.setSelected(false);
            }
        }
    }
}
