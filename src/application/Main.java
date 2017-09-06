package application;

import application.gui.ExceptionDialog;
import application.gui.GUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//TODO Documentation

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Thread.setDefaultUncaughtExceptionHandler(Main::exceptionHandler);
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(classLoader.getResource("application/gui/GUI.fxml"));
            BorderPane root = (BorderPane) loader.load();
            ((GUIController) loader.getController()).setStage(primaryStage);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(classLoader.getResource("application/gui/application.css").toExternalForm());
            primaryStage.setScene(scene);
            initStage(primaryStage);
            primaryStage.show();
            // this makes all stages close and the app exit when the main stage is closed
            primaryStage.setOnCloseRequest(e -> {
                Platform.exit();
                System.exit(0);
            });
        } catch (Exception e) {
            new ExceptionDialog(e);
            Platform.exit();
            System.exit(-1);
        }

    }

    public static void exceptionHandler(Thread t, Throwable e) {
        new ExceptionDialog(e);
        Platform.exit();
        System.exit(-1);
    }

    public void initStage(Stage primaryStage) {
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
