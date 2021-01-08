package TFTP.client.model.subjs;

import java.net.InetAddress;

public abstract class Settings {
    public static class Connect{
        public static InetAddress adres;
        public static int port;
        public static String pathToDownload = "files/";
    }
}
