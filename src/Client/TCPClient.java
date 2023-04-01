package Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import Connection.TCPConnection;
import Connection.ITCPConnectionListener;

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
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        System.out.println("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String str) {
        System.out.println(str);
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
        try {
            System.out.print("host: ");
            IP_ADDR = stdin.readLine();

            System.out.print("port: ");
            PORT = Integer.parseInt(stdin.readLine());

            System.out.print("client journal file path: ");
            logPath = stdin.readLine();

            File clientJournalFile = new File(logPath);
            if (!clientJournalFile.exists()) {
                clientJournalFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
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