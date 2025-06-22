/**
 *
 */
package radar;

import java.io.IOException;

/**
 * Concrete implementation of the radar device.
 */
public class RadarDeviceConsole implements IDevice {

    /**
     * @param radarPort
     *
     */
    public RadarDeviceConsole() throws IOException {
        // Nothing
    }

    @Override
    public void writeBytes(byte[] cmd, int length) {
        System.out.println("Write to radar: " + btos(cmd));
    }

    @Override
    public void addDataListener(MessageListener listener) {
        // Do nothing
    }

    @Override
    public void removeDataListener() {
        // Do nothing
    }

    private String btos(byte[] cmd) {
        StringBuilder sb = new StringBuilder();
        for (byte b : cmd) {
            sb.append("%02X ".formatted((b) & 0xff));
        }

        return sb.toString().trim();
    }

    @Override
    public void close() {
        // Nothing
    }

    @Override
    public boolean open() {
        return true;
    }

}
