/**
 *
 */
package radar;

import java.io.PrintWriter;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

public class MessageListener implements SerialPortMessageListener {
    private PrintWriter out;

    /**
     * @param out
     */
    public MessageListener(PrintWriter out) {
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

        out.println("Message from radar: " + delimitedMessage);
    }
}
