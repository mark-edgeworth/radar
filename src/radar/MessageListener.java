/**
 *
 */
package radar;

import java.io.BufferedOutputStream;
import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

public class MessageListener implements SerialPortMessageListener {
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
