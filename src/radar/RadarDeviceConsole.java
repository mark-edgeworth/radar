/**
 *
 */
package radar;

/**
 * Concrete implementation of the radar device.
 */
public class RadarDeviceConsole implements IDevice {

    /**
     * @param radarPort
     *
     */
    public RadarDeviceConsole() {
    }

    @Override
    public void writeBytes(byte[] cmd, int length) {
        System.out.println("Write to radar: " + btos(cmd));
    }

    @Override
    public void addDataListener(MessageListener listener) {
    }

    @Override
    public void removeDataListener() {
    }

    private String btos(byte[] cmd) {
        StringBuilder sb = new StringBuilder();
        for (byte b : cmd) {
            sb.append("%02X ".formatted((b) & 0xff));
        }

        return sb.toString().trim();
    }

}
