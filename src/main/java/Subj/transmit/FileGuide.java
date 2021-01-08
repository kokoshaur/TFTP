package Subj.transmit;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class FileGuide {
    File side;
    String root = "";

    public FileGuide(String path){
        side = new File(path);
        try {
            root = side.getCanonicalPath();
        } catch (IOException e) {
            Transmitter.show("Нарушение прав доступа\n");
        }
    }

    public boolean goTo(String path){
        File buf = new File(side.getPath()+ "/" + path);
        try {
            if (buf.getCanonicalPath().startsWith(root))
                side = buf;
        } catch (IOException e) {
            Transmitter.show("Нарушение прав доступа\n");
        }
        return side.isDirectory();
    }

    public String getContent(){
        StringBuilder dirs = new StringBuilder();
        StringBuilder files = new StringBuilder();
        dirs.append("..\n");
        if (side.isDirectory()) {
            for (File item : Objects.requireNonNull(side.listFiles())) {
                if (item.isDirectory()) {
                    dirs.append(item.getName()).append("\n");
                } else {
                    files.append(item.getName()).append("\n");
                }
            }
        }
        return "!dirs\n" + dirs.toString() + "!files\n" + files.toString();
    }

    public String wereWe(){
        try {
            return side.getCanonicalPath();
        } catch (IOException e) {
            Transmitter.show("Нарушение прав доступа\n");
            return root;
        }
    }

    public String[] getAllDirSubjs(String nameDir){
        StringBuilder dirs = new StringBuilder();
        StringBuilder files = new StringBuilder();

        scanDir(new File(wereWe() + "/" + nameDir), dirs, files, "");
        return new String[]{dirs.toString(), files.toString()};
    }

    public void scanDir(File baseDirectory, StringBuilder dirs, StringBuilder files, String were){
        if (baseDirectory.isDirectory()){
            for (File file : Objects.requireNonNull(baseDirectory.listFiles())) {
                if(file.isFile()){
                    files.append(were).append("/").append(file.getName()).append("\n");
                }else {
                    dirs.append(were).append("/").append(file.getName()).append("\n");
                    scanDir(file, dirs, files, were + "/" + file.getName());
                }
            }
        }
    }
}
