/**
 *
 */
package radar;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Concrete implementation of the radar device.
 */
public class RadarDevice implements IDevice {

    private final SerialPort port;

    /**
     * @param radarPort
     *
     */
    public RadarDevice(SerialPort radarPort) {
        port = radarPort;
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

}
