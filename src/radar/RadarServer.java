/**
 *
 */
package radar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import radar.RadarConfig.Direction;

/**
 *
 */
public class RadarServer {
    private boolean serverRunning = false;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Radar Server");
        new RadarServer(true);
    }

    private RadarServer(boolean useConsole) {
        String serialPortName = "/dev/serial0";

        int serverPort = 9990;

        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            serverRunning = true;

            IDevice radarDevice = useConsole ? new RadarDeviceConsole() : new RadarDevice(serialPortName);
            do {
                System.out.println("Server is listening for a new client connection on port " + serverPort);
                try (Socket socket = serverSocket.accept()) {
                    System.out.println("New client connected");
                    if (radarDevice.open()) {
                        handleConnection(radarDevice, socket);
                    } else {
                        System.err.println("Unable to open connection to radar module");
                    }
                }
            } while (serverRunning);
            System.out.println("Server stopped");
        } catch (IOException e) {
            System.err.println("Server terminated with an error: " + e.getMessage());
        }
    }

    /**
     * @param in
     * @param out
     */
    private void handleConnection(IDevice radarPort, Socket client) {
        try (PrintWriter bOut = new PrintWriter(client.getOutputStream(), true);
                BufferedReader rIn = new BufferedReader(new InputStreamReader(client.getInputStream()))) {
            MessageListener listener = new MessageListener(bOut);
            RadarConfig config = new RadarConfig();

            radarPort.addDataListener(listener);
            processRequestsFromClient(radarPort, rIn, listener, config);
            radarPort.removeDataListener();
        } catch (IOException e) {
            System.err.println("Connection terminated with an error: " + e.getMessage());
        } finally {
            System.out.println("Client disconnected");
        }
    }

    /**
     * @param radarPort
     * @param in
     * @return
     * @throws IOException
     */
    private void processRequestsFromClient(IDevice radarPort, BufferedReader fromClient, MessageListener toClient,
            RadarConfig config) throws IOException {
        boolean exit = false;
        do {
            String command = fromClient.readLine();
            if (command == null) {
                exit = true;
            } else if (!command.isBlank()) {
                toClient.messageToClient("Command '" + command + "' received");
                switch (command.charAt(0)) {
                    case 'C' -> config.sendConfig(radarPort);
                    case 'S' -> config.sendSetup(radarPort);
                    case 'U' -> config.update(radarPort, true);
                    case 'R' -> {
                        config.reset();
                        config.update(radarPort, true);
                    }
                    case '?' -> config.sendStatus(radarPort);
                    case 'X' -> exit = true;
                    case 'V' -> toClient.messageToClient(setValue(radarPort, command, config));
                    case 'T' -> {
                        serverRunning = false;
                        exit = true;
                    }
                    default -> {
                        toClient.messageToClient("Command '" + command + "' ignored");
                    }
                }
            }
        } while (!exit);
    }

    /**
     * @param command
     * @param config
     * @return
     */
    private String setValue(IDevice radarPort, String command, RadarConfig config) {
        String[] parts = command.split(" ");

        if (parts.length != 3) {
            return "Invalid value setting";
        }

        String param = parts[1];
        String value = parts[2];
        String error = null;
        try {
            switch (param) {
                case "limit" -> config.setLowerSpeedLimit(Integer.parseInt(value));
                case "sens" -> config.setSensitivity(Integer.parseInt(value));
                case "direction" -> config.setDirection(Direction.valueOf(value));
                default -> error = "Unrecognised parameter '" + param + "'";
            }
        } catch (Throwable t) {
            error = "Value setting failed: " + t;
        }

        if (error == null) {
            config.update(radarPort, false);
            error = "Setting '%s' to '%s'".formatted(param, value);
        }

        return error;
    }
}
