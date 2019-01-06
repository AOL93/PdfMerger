package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The application helps me in my current job to combine many waybills to 4 pages on one page (2x2).
 * As soon as possible, I will add the possibility of merging many pdf files
 * to one pdf file and several PDF files to any sheet on one page (for example 2x1, 2x2,4x1,1x1 etc.).
 * Additional functionality to add is possibility to rotate pdf files.
 * @author Adam
 * @version v0.0.1
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("sample.fxml"));
        primaryStage.setTitle("PDF Merger");
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
