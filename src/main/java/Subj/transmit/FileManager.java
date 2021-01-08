package Subj.transmit;

import Subj.net.Connection;
import TFTP.client.modelView.WorkController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.*;
import java.util.concurrent.TimeUnit;

public class FileManager {
    private String pathToDir;
    private FileOutputStream out;
    public String Relativ = "";

    public FileManager(String pathToDir){
        this.pathToDir = pathToDir;
        try {
            out = new FileOutputStream(new File(pathToDir));
        } catch (FileNotFoundException ignored) { }
    }

    public int fileReady(String mes){
        String[] args = mes.split(":");
        try {
            File dir = new File( pathToDir + args[1]);
            dir.mkdirs();
            File f = new File(dir, args[2]);
            f.createNewFile();
            out = new FileOutputStream(f);
            System.out.println(args[1]);
            Transmitter.show("Запрос на сохранение файла: " + dir.getAbsolutePath());
        } catch (IOException ignored) { Transmitter.show("Неверный запрос на сохранение файла\n");}
        return Integer.parseInt(args[3].trim());
    }

    public void fileGivme(Connection connection, String mes) {
        String[] args = mes.split(":");
        try {
            File file = new File(pathToDir + args[1]);

            FileInputStream fin = new FileInputStream(file);
            connection.sendMessage(("!fileReady:" + ":" + Relativ + file.getName() +":"+  file.length()).getBytes());

            long wereWe = 0;
            while(file.length() > wereWe){
                wereWe += 4096;
                TimeUnit.MICROSECONDS.sleep(1);
                connection.sendMessage(fin.readNBytes(4096));
            }
        } catch (Exception ignored) { Transmitter.show("Потеря пакета\n");}
    }

    public void saveFile(byte[] subj){
        try {
            if (WorkController.isCruptoFile){
                out.write(WorkController.deCipher.doFinal(subj));
            }else {
                out.write(subj);
            }
        } catch (IOException e) { Transmitter.show("Ошибка сохранения файл\n");} catch (BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
    }

    public void closeAll(){
        try {
            out.flush();
            out.close();
            Transmitter.show(" Файл сохранён\n");
        } catch (IOException e) {
            Transmitter.show(" Файл не сохранён\n");
        }
    }

    public void createDirs(String paths){
        String[] args = paths.split("\n");
        String dirName = args[0];
        args[0] = "";
        for (String dir:args) {
            newDir( dirName + dir);
        }
    }

    public void newDir(String paths){
        new File(pathToDir + paths).mkdirs();
    }
}
