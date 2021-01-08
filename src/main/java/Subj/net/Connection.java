package Subj.net;

import Subj.transmit.FileGuide;
import Subj.transmit.MessageHandler;

import java.io.*;
import java.net.*;

public class Connection {
    private MessageHandler messageHandler;
    private DatagramSocket socket;
    private Thread thread;

    private InetAddress adres = InetAddress.getByName("localhost");
    private int port = 8188;

    private String log = "";

    public Connection(MessageHandler messageHandler) throws IOException {
        this(messageHandler, new DatagramSocket());
    }

    public Connection(MessageHandler messageHandler, int port) throws IOException {
        this(messageHandler, new DatagramSocket(port));
    }

    public Connection(MessageHandler messageHandler, DatagramSocket socket) throws IOException{
        this.messageHandler = messageHandler;
        this.socket = socket;

        thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    messageHandler.connect(Connection.this);
                    while (!thread.isInterrupted()) {
                        byte[] message = getMessage();
                        if (message != null)
                            messageHandler.getFile(Connection.this, message);
                    }
                }catch (IOException e) {
                    messageHandler.showExeption(Connection.this, e);
                }
                finally {
                    messageHandler.disconnect(Connection.this);
                }
            }
        });
        thread.start();
    }

    public void refreshLog(String login, String password){
        log = login;
    }

    public void refreshSet(String ip, int port) throws UnknownHostException, SocketException {
        adres = InetAddress.getByName(ip);
        this.port = port;
        socket = new DatagramSocket(port);
    }

    public byte[] getMessage() throws IOException {
        byte[] receivingDataBuffer = new byte[4096];
        DatagramPacket inputPacket = new DatagramPacket(receivingDataBuffer, receivingDataBuffer.length);
        socket.receive(inputPacket);

        adres = inputPacket.getAddress();
        port = inputPacket.getPort();

        return inputPacket.getData();
    }

    public synchronized void sendMessage(byte[] message){
        try {
            DatagramPacket outputPacket = new DatagramPacket(
                    message, message.length,
                    adres,port
            );

            socket.send(outputPacket);
        } catch (IOException e) {
            messageHandler.showExeption(Connection.this, e);
            disconnect();
        }
    }

    public synchronized void disconnect(){
        thread.interrupt();
        socket.close();
    }

    public void setName(String name){
        log = name;
    }

    public String getName(){
        return log;
    }
}

