/**
 *
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.RadarConfig.Direction;

/**
 *
 */
public class RadarClient {

    // private static final String HOSTNAME = "localhost";
    private static final String HOSTNAME = "raspberrypi";
    private static final int PORT = 9990;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Radar Client");
        new RadarClient(HOSTNAME, PORT);
    }

    /**
     *
     */
    public RadarClient(String hostname, int port) {
        RadarCallback radarCallback = new RadarCallback();

        try (Socket socket = new Socket(hostname, port)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            MessageReader fromServer = new MessageReader(socket, radarCallback);
            fromServer.start();

            String text;

            BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
            do {
                System.out.println("Enter command: ");
                text = r.readLine();
                writer.println(text);

            } while (!text.equals("bye") && !fromServer.isInterrupted());

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    class MessageReader extends Thread {
        private final Socket socket;
        private final IResponseCallback callback;

        /**
         *
         */
        public MessageReader(Socket s, IResponseCallback callback) {
            this.socket = s;
            this.callback = callback;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                while (!interrupted()) {

                    String response = reader.readLine();
                    if (response == null) {
                        System.out.println("Client reads terminated due to EOF from server");
                        break;
                    }

                    parseResponse(response, callback);
                }

                interrupt();
            } catch (IOException e) {
                System.out.println("Client reads interupted: " + e);
            }
        }

        Pattern speedPattern = Pattern.compile("V([+-])(\\d\\d\\d\\.\\d)");

        /**
         * @param response
         * @param callback2
         */
        private void parseResponse(String response, IResponseCallback c) {
            Matcher speedMatcher = speedPattern.matcher(response);
            if (speedMatcher.matches()) {
                String direction = speedMatcher.group(1);
                Direction d = switch (direction) {
                    case "+" -> Direction.TOWARDS;
                    case "-" -> Direction.AWAY;
                    default -> throw new IllegalArgumentException("Invalid direction flag '" + direction + "'");
                };

                float measurement = Float.parseFloat(speedMatcher.group(2));

                if (measurement > 0.1f) {
                    callback.recordSpeed(d, measurement);
                } else if (measurement == 0) {
                    callback.recordZero();
                }
            } else {
                callback.message(response);
            }

        }
    }
}
