package Subj.transmit;

import Subj.net.Connection;

public interface MessageHandler {
    void connect(Connection subj);
    void disconnect(Connection subj);
    void getFile(Connection subj, byte[] message);
    void showExeption(Connection subj, Exception e);
}
