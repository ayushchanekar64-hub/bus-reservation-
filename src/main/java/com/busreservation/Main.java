package com.busreservation;

import com.busreservation.dao.DAOFactory;
import com.busreservation.view.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Application Entry Point for the Bus Reservation System.
 * MCA 1st Semester Object-Oriented Programming Project.
 */
public class Main extends Application {

    @Override
    public void init() throws Exception {
        super.init();
        // Initialize Data Access Layer (detects PostgreSQL or activates In-Memory mode)
        DAOFactory.initialize();
    }

    @Override
    public void start(Stage primaryStage) {
        ViewManager viewManager = new ViewManager(primaryStage);
        viewManager.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
