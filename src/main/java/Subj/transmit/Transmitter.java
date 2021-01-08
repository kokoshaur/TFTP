package Subj.transmit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public abstract class Transmitter {
    static Scanner in;
    static FileWriter log;
    public static void init(String name){
        try {
            in = new Scanner(System.in);
            File dir = new File("logs/" + name + "/");
            new File(dir.getPath()).mkdirs();

            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            File f = new File(dir, ((formatter.parse(formatter.format(new Date())).toString()).replace(':', '.').trim() + ".txt").replace(' ', '.'));
            f.createNewFile();
            log = new FileWriter(f, true);
        } catch (ParseException | IOException e) {
            System.out.println("Ошибка создания файла логов");
        }
    }

    public static void show(String meassage){
        try {
            log.write(meassage);
            log.flush();
        } catch (IOException e) {
            System.out.println("Ошибка записи файла логов");
        }
        System.out.println(meassage);
    }

    public static String getMessage(){
        return in.nextLine();
    }
}
