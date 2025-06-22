/**
 *
 */
package radar;

import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Concrete implementation of the radar device.
 */
public class RadarDevice implements IDevice {

    private final SerialPort port;

    /**
     * @param radarPort
     * @throws IOException
     *
     */
    public RadarDevice(String serialPortName) throws IOException {
        port = SerialPort.getCommPort(serialPortName);
        if (!port.openPort()) {
            throw new IOException("Failed to open serial port");
        }
    }

    @Override
    public void writeBytes(byte[] cmd, int length) {
        port.writeBytes(cmd, length);
    }

    @Override
    public void addDataListener(MessageListener listener) {
        port.addDataListener(listener);
    }

    @Override
    public void removeDataListener() {
        port.removeDataListener();
    }

    @Override
    public void close() {
        port.closePort();
    }

    @Override
    public boolean open() {
        return port.openPort();
    }

}
