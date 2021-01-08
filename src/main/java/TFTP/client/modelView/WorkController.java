package TFTP.client.modelView;

import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import Subj.transmit.FileManager;
import TFTP.client.model.subjs.Language;
import TFTP.client.model.subjs.Settings;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import Subj.net.Connection;
import Subj.transmit.MessageHandler;
import Subj.transmit.Transmitter;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class WorkController implements MessageHandler {
    private static Label showMessage;
    static Connection connection;
    private static VBox fileViewer;
    private FileManager fileRex = new FileManager(Settings.Connect.pathToDownload);

    private static Button send;
    private static Button nDir;
    private static TextField TFpath;
    private static TextField dirName;
    private static CheckBox crypto;

    public static int Error;
    private static boolean getAnsver = false;

    public static Cipher cipher;
    public static Cipher deCipher;
    public static Key key;
    public static boolean isCruptoFile = false;

    int count = 0;
    static int lvl = 0;
    static String wereWe = "";

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button BSend;

    @FXML
    private TextField TFPathClient;

    @FXML
    private VBox FileAndDir;

    @FXML
    private Label LIp;

    @FXML
    private Button BNewdir;

    @FXML
    private TextField TFNameDir;

    @FXML
    private CheckBox isCrypto;

    @FXML
    void initialize() {
        LIp.setText(String.valueOf(Settings.Connect.adres).substring(1));
        showMessage = LIp;
        fileViewer = FileAndDir;

        send = BSend;
        nDir = BNewdir;
        TFpath = TFPathClient;
        dirName = TFNameDir;
        crypto = isCrypto;

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec("Aga".toCharArray(), "Mda".getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            key = new SecretKeySpec(tmp.getEncoded(), "AES");

            cipher = Cipher.getInstance("AES");
            deCipher = Cipher.getInstance("AES");

            cipher.init(Cipher.ENCRYPT_MODE, key);
            deCipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }

        refreshLang();

        try {
            connection = new Connection(this);
        } catch (IOException e) {
            showExeption(connection, e);
        }

        TFPathClient.setOnMouseClicked(actionEvent -> {
            FileChooser file = new FileChooser();
            file.setTitle("Выберете файл");
            file.setInitialDirectory(new File("../"));
            TFPathClient.setText(file.showOpenDialog(showMessage.getScene().getWindow()).getAbsolutePath());
        });

        BSend.setOnAction(actionEvent -> {
            try {
                File file = new File(TFPathClient.getText());
                connection.sendMessage(("!fileReady:" +":" + wereWe + file.getName() +":"+  file.length()).getBytes());
                FileInputStream fin = new FileInputStream(file);

                long wereWe = 0;
                while(file.length() > wereWe){
                    wereWe += 4096;
                    TimeUnit.MICROSECONDS.sleep(1);
                    if (crypto.isSelected())
                        connection.sendMessage(cipher.doFinal(fin.readNBytes(4096)));
                    else connection.sendMessage(fin.readNBytes(4096));
                }
            } catch (IOException | InterruptedException | BadPaddingException | IllegalBlockSizeException e) {
                Transmitter.show("Ошибка отправки пакета\n");
            }
        });

        BNewdir.setOnAction(actionEvent -> {
            connection.sendMessage(("!newDir:" + wereWe + "/" + TFNameDir.getText()).getBytes());
        });

        Error = -1;
    }

    public static int connectStatus(String log, String pas, String plot){
        connection.sendMessage((plot + log + ":" + pas).getBytes());
        try {
            do
                TimeUnit.MILLISECONDS.sleep(500);
            while (!getAnsver);
            TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException ignored) {Transmitter.show("Некорректный ответ сервера\n");}
        return Error;
    }

    @Override
    public void connect(Connection subj) {
        getAnsver = true;
    }

    @Override
    public void disconnect(Connection subj) {
        connection.sendMessage("@exit".getBytes());
    }

    @Override
    public void getFile(Connection subj, byte[] message) {
        if (new String(message).trim().equals("Добро пожаловать")) {
            Error = 0;
            connect(subj);
        }
        if (new String(message).trim().equals("Неверный логин/пароль")){
            Error = -1;
        }
        if (new String(message).trim().equals("Логин занят")){
            Error = 1;
        }
        if (new String(message).trim().equals("Пароль короток")){
            Error = 2;
        }

        if (count > 0){
            if (count - 4096 <0) {
                byte[] buf = new byte[count];
                System.arraycopy(message, 0, buf, 0, buf.length);

                fileRex.saveFile(buf);
                fileRex.closeAll();

                count = 0;
                return;
            } else {
                fileRex.saveFile(message);
                count -= 4096;
            }
            return;
        }

        if (new String(message).substring(0, 1).equals("!")) {
            if (new String(message).substring(1, 11).equals("fileReady:")) {
                Transmitter.show("Запрос на принятие " + new String(message).trim());
                count = fileRex.fileReady((new String(message)));
                return;
            }
            if (new String(message).substring(1, 5).equals("dirs")) {
                Transmitter.show("Загрузка файловой иерархии\n ");
                String[] args = new String(message).substring(6).trim().split("!files");
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        reloadFileViewer(args);
                    }
                });
                return;
            }
            if(new String(message).substring(1, 9).equals("dirReady")){
                Transmitter.show("Создание папок: " + new String(message).substring(10).trim());
                fileRex.createDirs(new String(message).substring(10).trim());
            }
        }
    }

    @Override
    public void showExeption(Connection subj, Exception e) {

    }

    public static void refreshLang(){
        send.setText(Language.Work.send);
        nDir.setText(Language.Work.nDir);
        TFpath.setPromptText(Language.Work.TFPath);
        dirName.setPromptText(Language.Work.dirName);
        crypto.setText(Language.Work.crypto);
    }

    public static void close(){
        connection.sendMessage("@exit".getBytes());
        showMessage.getScene().getWindow().hide();
    }

    public static void reloadFileViewer(String[] args){
        fileViewer.getChildren().clear();

        refreshFileViewer(args[0].split("\n"), true);
        if (args.length == 2)
            refreshFileViewer(args[1].split("\n"), false);
    }

    private static void refreshFileViewer(String[] args, boolean isDir){
        for (String subj:args) {
            try{
                if ((subj.length() > 0) && (!subj.equals("\n"))){
                    Button button = new Button(subj.trim());
                    if (isDir){
                        button.setStyle("-fx-background-color: #2E3348; -fx-text-fill: #fafafa;");
                        button.setOnMouseClicked(actionEvent ->{
                            if (actionEvent.getButton() == MouseButton.PRIMARY){
                                if (subj.trim().equals(".."))
                                    lvl--;
                                else lvl++;
                                if (lvl > -1)
                                    wereWe += subj.trim() + "/";
                                connection.sendMessage(("!goTo:"+subj.trim()).getBytes());
                            }else if(actionEvent.getButton() == MouseButton.SECONDARY){
                                connection.sendMessage(("!dirGivme:"+subj.trim()).getBytes());
                            }
                        });
                    }else {
                        button.setStyle("-fx-background-color: #fafafa; -fx-text-fill: #2E3348;");
                        button.setOnMouseClicked(actionEvent ->{
                            if (actionEvent.getButton() == MouseButton.PRIMARY) {
                                isCruptoFile = false;
                            }else if(actionEvent.getButton() == MouseButton.SECONDARY){
                                isCruptoFile = true;
                            }
                            connection.sendMessage(("!fileGivme:" + wereWe + subj.trim() + ":").getBytes());
                        });
                    }

                    fileViewer.getChildren().addAll(button);
                }
            }catch(NullPointerException x){Transmitter.show("Ошибка инициализации иерархии файловой системы\n");}
        }
    }
}


