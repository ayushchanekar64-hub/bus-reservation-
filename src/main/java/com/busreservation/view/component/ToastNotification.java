package com.busreservation.view.component;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Toast Notification banner for user-facing feedback.
 */
public class ToastNotification extends StackPane {

    private final Label messageLabel;

    public ToastNotification() {
        setAlignment(Pos.CENTER);
        setMouseTransparent(true);
        setVisible(false);
        setOpacity(0.0);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-background-color: #1E293B; -fx-text-fill: #FFFFFF; -fx-font-size: 13px; " +
                "-fx-font-weight: 600; -fx-padding: 10px 24px; -fx-background-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);");

        getChildren().add(messageLabel);
    }

    public void show(String message, boolean isError) {
        messageLabel.setText(message);
        if (isError) {
            messageLabel.setStyle("-fx-background-color: #DC2626; -fx-text-fill: #FFFFFF; -fx-font-size: 13px; " +
                    "-fx-font-weight: 600; -fx-padding: 10px 24px; -fx-background-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);");
        } else {
            messageLabel.setStyle("-fx-background-color: #15803D; -fx-text-fill: #FFFFFF; -fx-font-size: 13px; " +
                    "-fx-font-weight: 600; -fx-padding: 10px 24px; -fx-background-radius: 24px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 10, 0, 0, 3);");
        }

        setVisible(true);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), this);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        PauseTransition stay = new PauseTransition(Duration.millis(2500));

        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), this);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        SequentialTransition seq = new SequentialTransition(fadeIn, stay, fadeOut);
        seq.setOnFinished(e -> setVisible(false));
        seq.play();
    }
}
