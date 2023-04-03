package Client;

import java.io.*;
import java.net.Socket;

import Connection.TCPConnection;
import Connection.ITCPConnectionListener;
import Exceptions.IncorrectPortException;

public class TCPClient implements ITCPConnectionListener {

    private static String IP_ADDR;
    private static int PORT;
    private TCPConnection tcpConnection;
    private String logPath;
    private final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
        new TCPClient();
    }

    private TCPClient() {
        installInitialValues();

        try {
            tcpConnection = new TCPConnection(this, new Socket(IP_ADDR, PORT));
            while (true) {
                String msg = stdin.readLine();
                tcpConnection.sendMessage(msg);
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String str) {
        System.out.println("Здарова" + str);
        log(str);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        System.out.println("Connection close");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Connection exception: " + e);
    }

    private void installInitialValues() {
        try (BufferedReader clientPortFileReader = new BufferedReader(new FileReader("config.txt"))){
            IP_ADDR = clientPortFileReader.readLine();
            var portStr = clientPortFileReader.readLine();
            if(portStr.length() > 4){
                throw new IncorrectPortException();
            }
            PORT = Integer.parseInt(portStr);
            System.out.println("Host: " + IP_ADDR + "\n" +
                    "Port: " + PORT + "\n");
            System.out.print("Client journal file path: ");
            logPath = stdin.readLine();
            File clientJournalFile = new File(logPath);
            if (!clientJournalFile.exists()) {
                clientJournalFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch(Exception e){
            System.out.println(e);
        }
    }

    private void log(String str) {
        try {
            FileWriter clientJournalFileWriter = new FileWriter(logPath, true);
            clientJournalFileWriter.write(str);
            clientJournalFileWriter.write('\n');
            clientJournalFileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}