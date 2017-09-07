package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import application.gui.ExceptionDialog;
import application.gui.GUIController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

//TODO Documentation

public class Main extends Application {
    public static final List<Locale> supportedLocales = new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.FRENCH));
    public static final Locale       fallBackLocale   = Locale.ENGLISH;

    public boolean isSupportedLocale(Locale locale) {
        for (Locale l : supportedLocales) {
            if (l.getLanguage().equals(locale.getLanguage()))
                return true;
        }
        return false;
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.setDefaultUncaughtExceptionHandler(Main::exceptionHandler);
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            System.out.println(Locale.getDefault().toString());
            Locale locale = isSupportedLocale(Locale.getDefault()) ? Locale.getDefault() : fallBackLocale;
            FXMLLoader loader = new FXMLLoader(classLoader.getResource("resources/gui/GUI.fxml"),
                    ResourceBundle.getBundle("resources.i18n.Locale", locale));
            BorderPane root = (BorderPane) loader.load();
            GUIController controller = (GUIController) loader.getController();
            controller.setStage(primaryStage);
            controller.setLoader(loader);
            controller.setResourceBundle(loader.getResources());
            Scene scene = new Scene(root);
            scene.getStylesheets().add(classLoader.getResource("resources/gui/application.css").toExternalForm());
            primaryStage.setScene(scene);
            initStage(primaryStage);
            primaryStage.show();
            // this makes all stages close and the app exit when the main stage is closed
            primaryStage.setOnCloseRequest(evt -> {
                Alert confirmClose = new Alert(AlertType.CONFIRMATION);
                confirmClose.setTitle("Confirm");
                confirmClose.setHeaderText("Confirm exit");
                confirmClose.setContentText("Are you sure you want to exit ?");
                Optional<ButtonType> closeResponse = confirmClose.showAndWait();
                if (!ButtonType.OK.equals(closeResponse.get())) {
                    evt.consume();
                } else {
                    Platform.exit();
                    System.exit(0);
                }
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
        String version = this.getClass().getPackage().getImplementationVersion();
        primaryStage.setResizable(true);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Cellular Automatons" + ((version == null) ? "" : (" " + version)));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
