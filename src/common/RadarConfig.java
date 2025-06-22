/**
 *
 */
package common;

import radar.IDevice;

/**
 *
 */
public class RadarConfig {
    public enum Direction {
        TOWARDS, AWAY, BOTH;
    }

    private int lowerSpeedLimitKph = 1; // In KPH
    private int angleComp = 0;
    private int sensitivity = 5;

    private Direction direction = Direction.TOWARDS;
    private int reportRate = 1;

    private boolean configChanged = true;
    private boolean setupChanged = true;

    /**
     * @param lowerSpeedLimit
     *     the lowerSpeedLimit to set (in mph, between 1-60)
     */
    public void setLowerSpeedLimit(int newLowerSpeedLimitMph) {

        lowerSpeedLimitKph = minMax((int) (newLowerSpeedLimitMph * 1.609f), 1, 100);
        setupChanged = true;
    }

    /**
     * @param angleComp
     *     the angleComp to set (in degrees)
     */
    public void setAngleComp(int angleComp) {
        this.angleComp = minMax(angleComp, 0, 90);
        setupChanged = true;
    }

    /**
     * @param sensitivity
     *     the sensitivity to set
     */
    public void setSensitivity(int newSensitivity) {
        sensitivity = minMax(newSensitivity, 1, 15);
        setupChanged = true;
    }

    /**
     * @param direction
     *     the direction to set
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
        configChanged = true;
    }

    /**
     * @param reportRate
     *     the reportRate to set (reports per second)
     */
    public void setReportRate(int reportRate) {

        this.reportRate = minMax(reportRate, 1, 22);
        configChanged = true;
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
    public void sendStatus(IDevice radarPort) {
        byte[] cmd = new byte[] { (byte) 0x43, (byte) 0x46, (byte) 0x07, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        send(radarPort, cmd);
    }

    public void sendSetup(IDevice radarPort) {
        byte[] cmd = new byte[] { (byte) 0x43, (byte) 0x46, (byte) 0x01, (byte) lowerSpeedLimitKph, (byte) angleComp,
                (byte) sensitivity, (byte) 0x0d, (byte) 0x0a };
        send(radarPort, cmd);
        setupChanged = false;
    }

    public void sendConfig(IDevice radarPort) {
        byte directionAsValue = (byte) switch (direction) {
            case TOWARDS -> 1;
            case AWAY -> 2;
            case BOTH -> 0;
        };

        byte reportRateAsValue = (byte) (22 / reportRate - 1);
        byte unit = 0x1; // MPH

        byte[] cmd = new byte[] { (byte) 0x43, (byte) 0x46, (byte) 0x02, directionAsValue, reportRateAsValue, unit, (byte) 0x0d,
                (byte) 0x0a };
        send(radarPort, cmd);
        configChanged = false;
    }

    /**
     * @param radarPort
     * @param cmd
     */
    private void send(IDevice radarPort, byte[] cmd) {
        radarPort.writeBytes(cmd, cmd.length);
    }

    /**
     *
     */
    public void update(IDevice radarPort, boolean force) {
        if (setupChanged || force) {
            sendSetup(radarPort);
        }
        if (configChanged || force) {
            sendConfig(radarPort);
        }
    }

    /**
     * @return
     */
    public void reset() {
        lowerSpeedLimitKph = 1; // In KPH
        angleComp = 0;
        sensitivity = 5;

        direction = Direction.TOWARDS;
        reportRate = 1;

        configChanged = true;
        setupChanged = true;
    }
}
