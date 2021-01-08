package TFTP.client.modelView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import TFTP.client.model.Rex;
import TFTP.client.model.subjs.Language;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainController {
    private static Label showStatus;
    private static Button go;
    private static Button reg;
    private static TextField log;
    private static PasswordField pas;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BConnect;

    @FXML
    private Button BRegistr;

    @FXML
    private TextField TFLog;

    @FXML
    private PasswordField TFPas;

    @FXML
    private Label LStatus;

    @FXML
    private ImageView BSettings;

    @FXML
    void initialize() throws IOException {
        {
            showStatus = LStatus;
            go = BConnect;
            log = TFLog;
            pas = TFPas;
            reg = BRegistr;

            FXMLLoader work = new FXMLLoader(getClass().getResource("/view/workWindow.fxml"));
            FXMLLoader settings = new FXMLLoader(getClass().getResource("/view/settingsWindow.fxml"));

            Parent rootWork = work.load();
            Stage stageWork = new Stage();
            stageWork.setScene(new Scene(rootWork));
            stageWork.setOnCloseRequest((new EventHandler<WindowEvent>(){

                @Override

                public void handle(WindowEvent event) {
                    System.exit(0);
                }

            }));

            Parent rootSettings = settings.load();
            Stage stageSettings = new Stage();
            stageSettings.setScene(new Scene(rootSettings));
            stageSettings.setOnCloseRequest((new EventHandler<WindowEvent>(){
                @Override
                public void handle(WindowEvent event) {
                    SettingsController.close();
                }

            }));

            BConnect.setOnAction(event -> {
                initConnect(stageWork, "@log:");
            });

            BRegistr.setOnAction(event->{
                initConnect(stageWork, "@reg:");
            });

            BSettings.setOnMouseClicked(event -> {
                stageSettings.show();
                close();
            });
        }
    }

    public static void refrashLang(){
        go.setText(Language.Main.Connect);
        reg.setText(Language.Main.reg);
        log.setPromptText(Language.Main.log);
        pas.setPromptText(Language.Main.pas);

        showStatus.setText(Language.Errors.OK);
    }

    public static void close(){
        ((Stage) showStatus.getScene().getWindow()).hide();
    }

    public static void open(){
        ((Stage) showStatus.getScene().getWindow()).show();
    }

    public static void showMessage(String message){
        showStatus.setText(message);
    }

    private void initConnect(Stage stageWork, String plot){
        switch (WorkController.connectStatus(TFLog.getText(), TFPas.getText(), plot)) {
            case -1:
                LStatus.setText(Language.Errors.badLog);
                return;

            case 0:
                stageWork.show();
                close();
                return;

            case 1:
                LStatus.setText(Language.Errors.logIsRes);
                return;

            case 2:
                LStatus.setText(Language.Errors.badPas);
                return;
        }
    }
}


