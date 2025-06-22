/**
 *
 */
package radar;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import com.fazecast.jSerialComm.SerialPort;

/**
 *
 */
public class RadarServer {
    /**
     * @param args
     */
    public static void main(String[] args) {
        new RadarServer();
    }

    private RadarServer() {
        String serialPortName = "/dev/serial0";
        boolean terminated = false;

        int serverPort = 9990;

        SerialPort radarPort = SerialPort.getCommPort(serialPortName);
        RadarDevice radarDevice = new RadarDevice(radarPort);

        if (!radarPort.openPort()) {
            System.out.println("Serial port could not be opened");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(6868)) {
            System.out.println("Server is listening on port " + serverPort);

            do {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");

                    terminated = handleConnection(radarDevice, socket);
                }
            } while (!terminated);
        } catch (IOException e) {
            System.err.println("Server terminated with an error: " + e.getMessage());
        }

        radarPort.closePort();
    }

    /**
     * @param in
     * @param out
     * @return true if the client has asked to close the connection, false if it failed
     */
    private boolean handleConnection(IDevice radarPort, Socket client) {
        try (BufferedOutputStream bOut = new BufferedOutputStream(client.getOutputStream());
                BufferedReader rIn = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            MessageListener listener = new MessageListener(bOut);
            RadarConfig config = new RadarConfig();

            radarPort.addDataListener(listener);
            boolean terminated = false;

            while (!terminated) {
                terminated = processRequestFromClient(radarPort, rIn, config);
            }
            radarPort.removeDataListener();
            return true;
        } catch (IOException e) {
            System.err.println("Connection terminated with an error: " + e.getMessage());
        }
        return false;
    }

    /**
     * @param radarPort
     * @param in
     * @return
     * @throws IOException
     */
    private boolean processRequestFromClient(IDevice radarPort, BufferedReader rIn, RadarConfig config) throws IOException {
        String command = rIn.readLine();

        switch (command.charAt(0)) {
            case 'C':
                config.sendConfig(radarPort);
                break;
            case 'S':
                config.sendStatus(radarPort);
                break;
            case 'X':
                return false;
            default:
                break;
        }

        return true;
    }
}
