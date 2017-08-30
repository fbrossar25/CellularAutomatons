package application;
	
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;

//TODO Documentation

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			BorderPane root = (BorderPane)FXMLLoader.load(classLoader.getResource("application/gui/GUI.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(classLoader.getResource("application/gui/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
			//this makes all stages close and the app exit when the main stage is closed
			primaryStage.setOnCloseRequest(e -> Platform.exit());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
