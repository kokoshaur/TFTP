package TFTP.client;

import Subj.transmit.Transmitter;
import TFTP.client.model.Rex;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Start extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Rex.refreshSettings();
        Transmitter.init("client");
        Rex.refreshLang("RU");

        Parent root = FXMLLoader.load(getClass().getResource("/view/mainWindow.fxml"));
        primaryStage.setTitle("TFTP by kokoshaur");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest((new EventHandler<WindowEvent>(){
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }

        }));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
