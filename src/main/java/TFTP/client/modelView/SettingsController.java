package TFTP.client.modelView;


import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import TFTP.client.model.Rex;
import TFTP.client.model.subjs.Language;
import TFTP.client.model.subjs.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

public class SettingsController {
    private static Label showStatus;
    private static Label LSet;
    private static Button BSave;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BSaveSettings;

    @FXML
    private TextField TFIp;

    @FXML
    private TextField TFPort;

    @FXML
    private Label LStatus;

    @FXML
    private ImageView BSetRu;

    @FXML
    private ImageView BSetEn;

    @FXML
    private Label LSettings;

    @FXML
    void initialize() {
        showStatus = LStatus;
        LSet = LSettings;
        BSave = BSaveSettings;

        TFIp.setText(String.valueOf(Settings.Connect.adres).substring(1));
        TFPort.setText(String.valueOf(Settings.Connect.port));

        BSaveSettings.setOnAction(actionEvent ->{
            ArrayList<String> settings = new ArrayList<>();

            settings.add(TFIp.getText());
            settings.add(TFPort.getText());
            try {
                Rex.saveSettings(settings);

                MainController.open();
                BSaveSettings.getScene().getWindow().hide();
            } catch (FileNotFoundException e) {
                showMessage("Ошибка");
            }
        });

        BSetRu.setOnMouseClicked(mouseEvent -> {
            try {
                Rex.refreshLang("RU");
                LStatus.setText("Язык установлен");
            } catch (FileNotFoundException e) {
                LStatus.setText("Файл не найден");
            }
        });

        BSetEn.setOnMouseClicked(mouseEvent -> {
            try {
                Rex.refreshLang("EN");
                LStatus.setText("Language reset");
            } catch (FileNotFoundException e) {
                LStatus.setText("Файл не найден");
            }
        });

    }

    public static void showMessage(String message){
        showStatus.setText(message);
    }

    public static void close(){
        showStatus.getScene().getWindow().hide();
        MainController.open();
    }

    public static void refreshLang(){
        LSet.setText(Language.Set.settings);
        BSave.setText(Language.Set.save);
    }
}

