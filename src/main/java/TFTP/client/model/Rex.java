package TFTP.client.model;

import TFTP.client.model.flexers.Resampler;
import TFTP.client.model.subjs.Language;
import TFTP.client.model.subjs.Settings;
import TFTP.client.modelView.MainController;
import TFTP.client.modelView.SettingsController;
import TFTP.client.modelView.WorkController;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.ArrayList;

public abstract class Rex {
    public static void refreshLang(String language) throws FileNotFoundException {
        Resampler.refreshLang(language);

        Language.Main.Connect = Resampler.getNextWord();
        Language.Main.reg = Resampler.getNextWord();
        Language.Main.log = Resampler.getNextWord();
        Language.Main.pas = Resampler.getNextWord();

        Language.Set.settings = Resampler.getNextWord();
        Language.Set.save = Resampler.getNextWord();

        Language.Work.send = Resampler.getNextWord();
        Language.Work.nDir = Resampler.getNextWord();
        Language.Work.TFPath = Resampler.getNextWord();
        Language.Work.dirName = Resampler.getNextWord();
        Language.Work.crypto = Resampler.getNextWord();

        Language.Errors.badLog = Resampler.getNextWord();
        Language.Errors.OK = Resampler.getNextWord();
        Language.Errors.logIsRes = Resampler.getNextWord();
        Language.Errors.badPas = Resampler.getNextWord();

        try {
            MainController.refrashLang();
        }catch (Exception ignore){};
        try {
            SettingsController.refreshLang();
        }catch (Exception ignore){};
        try {
            WorkController.refreshLang();
        }catch (Exception ignore){};
    }

    public static void refreshSettings(){
        try{
            Resampler.refreshSettings();

            try {
                Settings.Connect.adres = InetAddress.getByName(Resampler.getNextSettings());
                Settings.Connect.port = Integer.parseInt(Resampler.getNextSettings());
            }   catch (Exception ignore){};
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void saveSettings(ArrayList<String> args) throws FileNotFoundException {
        Resampler.startSaveSettings();
        for (String subj : args) {
            Resampler.saveSetting(subj);
        }
    }
}

