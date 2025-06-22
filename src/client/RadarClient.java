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

/**
 *
 */
public class RadarClient {

    private static final String HOSTNAME = "localhost";
    // private static final String HOSTNAME = "raspberrypi";
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
        try (Socket socket = new Socket(hostname, port)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);

            MessageReader fromServer = new MessageReader(socket);
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
        private Socket socket;

        /**
         *
         */
        public MessageReader(Socket s) {
            this.socket = s;
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

                    System.out.println(response);
                }

                interrupt();
            } catch (IOException e) {
                System.out.println("Client reads interupted: " + e);
            }
        }
    }

    // public static void main(String[] args) {
    // if (args.length < 2) {
    // return;
    // }
    //
    // String hostname = args[0];
    // int port = Integer.parseInt(args[1]);
    //
    // }
}
