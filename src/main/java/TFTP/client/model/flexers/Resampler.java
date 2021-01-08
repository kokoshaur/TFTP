package TFTP.client.model.flexers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public abstract class Resampler {
    public static String pathToSave = "subj/";

    private static Scanner lang;
    private static Scanner set;
    private static PrintWriter saveSet;

    public static void refreshLang(String language) throws FileNotFoundException {
        lang = new Scanner(new File(pathToSave + language + ".txt"),"UTF-16LE");
    }

    public static void refreshSettings() throws FileNotFoundException {
        if (saveSet != null)
            saveSet.close();
        try {
            set = new Scanner(new File(pathToSave + "settings.txt"));
        }catch (FileNotFoundException q){
            File dir = new File(pathToSave);
            dir.mkdirs();
            File f = new File(dir, "settings.txt");
            try {
                f.createNewFile();
                System.out.println(f.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            throw new FileNotFoundException();
        }
    }

    public static String getNextWord(){
        if (lang.hasNext())
            return lang.nextLine();
        else return "";
    }

    public static String getNextSettings(){
        if (set.hasNext())
            return set.nextLine();
        else return "";
    }

    public static void startSaveSettings() throws FileNotFoundException {
        saveSet = new PrintWriter(new File(pathToSave + "settings.txt"));
    }

    public static void saveSetting(String set){
        saveSet.println(set);
        saveSet.flush();
    }
}

