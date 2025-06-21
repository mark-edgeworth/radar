/**
 *
 */
package radar;

import com.fazecast.jSerialComm.SerialPort;

/**
 *
 */
public class RadarConfig {
    public enum Direction {
        IN, OUT, IN_OUT;
    }

    private int lowerSpeedLimitKph = 1; // In KPH
    private int angleComp = 0;
    private int sensitivity = 5;

    private Direction direction;
    private int reportRate;

    /**
     * @param lowerSpeedLimit
     *     the lowerSpeedLimit to set (in mph, between 1-60)
     */
    public void setLowerSpeedLimit(int newLowerSpeedLimitMph) {

        lowerSpeedLimitKph = minMax((int) (newLowerSpeedLimitMph * 1.609f), 1, 100);
    }

    /**
     * @param angleComp
     *     the angleComp to set (in degrees)
     */
    public void setAngleComp(int angleComp) {
        this.angleComp = angleComp;
    }

    /**
     * @param sensitivity
     *     the sensitivity to set
     */
    public void setSensitivity(int newSensitivity) {
        sensitivity = minMax(newSensitivity, 1, 15);
    }

    /**
     * @param direction
     *     the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * @param reportRate
     *     the reportRate to set (reports per second)
     */
    public void setReportRate(int reportRate) {

        this.reportRate = minMax(reportRate, 1, 22);
    }

    private int minMax(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * @param radarPort
     */
    public void sendStatus(SerialPort radarPort) {
        byte[] cmd = new byte[] { (byte) 0x43, (byte) 0x46, (byte) 0x07, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        send(radarPort, cmd);
    }

    public void sendConfig(SerialPort radarPort) {
        byte[] cmd = new byte[] { (byte) 0x43, (byte) 0x46, (byte) 0x01, (byte) lowerSpeedLimitKph, (byte) angleComp,
                (byte) sensitivity, (byte) 0x0d, (byte) 0x0a };
        send(radarPort, cmd);

        byte directionAsValue = (byte) switch (direction) {
            case IN -> 0;
            case OUT -> 1;
            case IN_OUT -> 2;
        };

        byte reportRateAsValue = (byte) (22 / reportRate - 1);
        byte unit = 0x1; // MPH

        cmd = new byte[] { (byte) 0x43, (byte) 0x46, (byte) 0x02, directionAsValue, reportRateAsValue, unit, (byte) 0x0d,
                (byte) 0x0a };
        send(radarPort, cmd);
    }

    /**
     * @param radarPort
     * @param cmd
     */
    private void send(SerialPort radarPort, byte[] cmd) {
        radarPort.writeBytes(cmd, cmd.length);
    }
}
