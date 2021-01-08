package TFTP.server;

import Subj.net.Connection;
import Subj.transmit.FileGuide;
import Subj.transmit.MessageHandler;
import Subj.transmit.Transmitter;
import Subj.transmit.FileManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Server implements MessageHandler {
    private ArrayList<Connection> friends = new ArrayList<Connection>();
    int count = 0;

    private FileManager fileRex;
    private FileGuide fileStix;

    private static Statement st;

    public static void main(String[] args) {
        Transmitter.init("server");

        try {
            Class.forName("org.sqlite.JDBC");
            st = DriverManager.getConnection("jdbc:sqlite:subj/users.db").createStatement();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        new Server();
    }

    public Server() {
        try {
            new ServerSocket(8188);
            Transmitter.show("Сервер создался\n");

            try {
                new Connection(this, 8188);
            } catch (IOException e) {
                showExeption(null, e);
            }

            while(true){
                getFile(null, Transmitter.getMessage().getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void connect(Connection subj) {

    }

    @Override
    public synchronized void disconnect(Connection subj) {
        friends.remove(subj);
        subj.disconnect();
    }

    @Override
    public synchronized void getFile(Connection connection, byte[] message) {
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

        if (new String(message).substring(0, 1).equals("@")) {
            switch (new String(message).substring(1, 5)){
                case "log:":
                    if (isFriend(new String(message).substring(5).trim())){
                        newConnct(connection, new String(message).split(":")[1].trim());
                    }else {
                        connection.sendMessage("Пароль не верный".getBytes());
                        Transmitter.show(new String(message).trim() +" Неудачная попытка подключения\n");
                    }
                    return;

                case "reg:":
                    switch (isRegistr(new String(message).substring(5))){
                        case -1:
                            connection.sendMessage("Неверный логин/пароль".getBytes());
                            return;

                        case 0:
                            newConnct(connection, new String(message).split(":")[1].trim());
                            return;

                        case 1:
                            connection.sendMessage("Логин занят".getBytes());
                            return;

                        case 2:
                            connection.sendMessage("Пароль короток".getBytes());
                            return;
                    }

                case "exit":
                    disconnect(connection);
                    return;
            }
        }


        if (new String(message).substring(0, 1).equals("!")) {
            switch (new String(message).substring(1, new String(message).indexOf(":"))){
                case "fileReady":
                    if (friends.contains(connection))
                        count = fileRex.fileReady((new String(message)).trim());
                    else {
                        connection.sendMessage("Пароль не верный".getBytes());
                        Transmitter.show(connection.getName() +" Попытка внедрения\n");
                    }
                    return;

                case "fileGivme":
                    if (friends.contains(connection))
                        fileRex.fileGivme(connection, new String(message).trim());
                    else {
                        connection.sendMessage("Пароль не верный".getBytes());
                        Transmitter.show(connection.getName() +" Попытка внедрения\n");
                    }
                    return;

                case "goTo":
                    if (friends.contains(connection)){
                        fileStix.goTo(new String(message).substring(6).trim());
                        sendDir(connection);
                    }
                    else {
                        connection.sendMessage("Пароль не верный".getBytes());
                        Transmitter.show(connection.getName() +" Попытка внедрения\n");
                    }
                    return;

                case "dirGivme":
                    if (friends.contains(connection)){
                        Transmitter.show("Запрос на скачивание директории: " + new String(message).substring(10).trim());
                        transportDir(connection, new String(message).substring(10).trim());
                    }
                    else {
                        connection.sendMessage("Пароль не верный".getBytes());
                        Transmitter.show(connection.getName() +" Попытка внедрения\n");
                    }
                    return;

                case "newDir":
                    if (friends.contains(connection)){
                        Transmitter.show("Создание директории директории: " + new String(message).substring(8).trim());
                        fileRex.newDir(new String(message).substring(8).trim());
                    }
                    else {
                        connection.sendMessage("Пароль не верный".getBytes());
                        Transmitter.show(connection.getName() +" Попытка внедрения\n");
                    }
                    return;
            }
        }
    }

    @Override
    public synchronized void showExeption(Connection subj, Exception e) {
        if (subj == null)
            Transmitter.show(e.getMessage());
        else Transmitter.show(subj.toString() + " -Ошибка подключения:" + e.getMessage());
    }

    private void newConnct(Connection connection, String name){
        connection.setName(name);

        friends.add(connection);
        fileRex = new FileManager("files/" + connection.getName() + "/");
        fileStix = new FileGuide("files/" + connection.getName());

        Transmitter.show(connection.getName() +" Подключился\n");
        connection.sendMessage("Добро пожаловать".getBytes());

        sendDir(connection);
    }

    private boolean isFriend(String arg){
        try {
            String[] args = arg.split(":");

            ResultSet rs = st.executeQuery("SELECT * FROM users WHERE name = '"+ args[0] + "'");

            return  ((String.valueOf(args[1].trim().hashCode())).equals(rs.getString("password")));
        } catch (Exception e) {
            return false;
        }
    }

    private int isRegistr(String arg){
        try {
            String[] args = arg.split(":");

            ResultSet rs = st.executeQuery("SELECT * FROM users WHERE name = '"+ args[0] + "'");

            if (rs.next())
               return  1;
            if (args[1].trim().length() < 5)
               return 2;
            System.out.println(args[1].trim());
            System.out.println("saf");
            st.executeUpdate("INSERT INTO users (name, password)" + "VALUES ('"+ args[0] + "', '" + args[1].trim().hashCode() + "');");
            return 0;
        } catch (Exception e) {
            return -1;
        }
    }

    private void sendDir(Connection connection){
        connection.sendMessage("!Список файлов".getBytes());

        connection.sendMessage(fileStix.getContent().getBytes());
    }

    private void transportDir(Connection connection, String dir){
        String[] args = fileStix.getAllDirSubjs(dir);
        connection.sendMessage(("!dirReady:"+ dir + "\n" + args[0]).getBytes());

        for (String file:args[1].split("\n")) {
            fileRex.Relativ = dir + "/";
            getFile(connection, ("!fileGivme:" + dir + "/" + file +":").getBytes());
        }
        fileRex.Relativ = "";
    }
}
