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
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

/**
 *
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) {
        new Main();
    }

    private Main() {
        String serialPortName = "/dev/serial0";
        boolean terminated = false;

        int serverPort = 9990;

        SerialPort radarPort = SerialPort.getCommPort(serialPortName);

        if (!radarPort.openPort()) {
            System.out.println("Serial port could not be opened");
            return;
        }

        try (ServerSocket serverSocket = new ServerSocket(6868)) {
            System.out.println("Server is listening on port " + serverPort);
            do {
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");

                    terminated = handleConnection(radarPort, socket);
                }
            } while (!terminated);
        } catch (IOException e) {
            System.err.println("Server terminated with an error: " + e.getMessage());
        }

        radarPort.closePort();
    }

    private final class MessageListener implements SerialPortMessageListener {
        private BufferedOutputStream out;

        /**
         * @param out
         */
        public MessageListener(BufferedOutputStream out) {
            this.out = out;
        }

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
        }

        @Override
        public byte[] getMessageDelimiter() {
            return new byte[] { (byte) 0x0D, (byte) 0x0A };
        }

        @Override
        public boolean delimiterIndicatesEndOfMessage() {
            return true;
        }

        @Override
        public void serialEvent(SerialPortEvent event) {
            byte[] delimitedMessage = event.getReceivedData();
            System.out.println("Received the following delimited message from the radar module: '" + delimitedMessage + "'");

            try {
                out.write(delimitedMessage);
            } catch (IOException e) {
                System.err.println("Unable to send radar response: " + e.getMessage());
            }
        }
    }

    /**
     * @param in
     * @param out
     * @return true if the client has asked to close the connection, false if it failed
     */
    private boolean handleConnection(SerialPort radarPort, Socket client) {
        RadarConfig config = new RadarConfig();

        try (BufferedOutputStream bOut = new BufferedOutputStream(client.getOutputStream());
                BufferedReader rIn = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            MessageListener listener = new MessageListener(bOut);
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
    private boolean processRequestFromClient(SerialPort radarPort, BufferedReader rIn, RadarConfig config) throws IOException {
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
