package at.sporty.team1.presentation;

import at.sporty.team1.presentation.controllers.MainViewController;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.rmi.RMISecurityManager;

/**
 * This is Utility class which starts the whole application.
 */
public class MainApp extends Application {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String SECURITY_PROPERTY = "java.security.policy";

    private static final String PATH_TO_SECURITY_POLICIES_FILE = "security.policy";
    private static final String PATH_TO_DEFAULT_CSS_FILE = "/at/sporty/team1/presentation/css/main.css";

    private static final String DEFAULT_TITLE = "SPORTY";
    private static final int DEFAULT_WIDTH = 960;
    private static final int DEFAULT_HEIGHT = 540;

    /**
     * Default (empty) constructor for this utility class.
     */
    public MainApp() {
    }

    @Override
    public void stop() throws Exception {
    }

    @Override
    public void start(Stage initStage) throws Exception {
        URL securityPoliciesURL = getClass().getClassLoader().getResource(PATH_TO_SECURITY_POLICIES_FILE);
        if (securityPoliciesURL != null) {

            System.setProperty(SECURITY_PROPERTY, securityPoliciesURL.toString());
            System.setSecurityManager(new RMISecurityManager());
            showMainStage(new Stage());

        } else {
            LOGGER.error("Error occurs while starting a client. Security policies were not found.");
        }
    }

    /**
     * Displays the main stage of the application.
     * @param primaryStage stage to be shown.
     */
    private void showMainStage(Stage primaryStage) {
        ViewLoader<MainViewController> viewLoader = ViewLoader.loadView(MainViewController.class);
        Parent pane = (Parent) viewLoader.loadNode();

        Scene scene = new Scene(pane, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        scene.getStylesheets().add(PATH_TO_DEFAULT_CSS_FILE);

        primaryStage.setScene(scene);
        primaryStage.setTitle(DEFAULT_TITLE);

        primaryStage.show();
    }

    /**
     * Default main method. Starts "this" application.
     * @param args the command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
	}
}
