/**
 *
 */
package radar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import radar.RadarConfig.Direction;

/**
 *
 */
class TestRadarConfig {

    class MockDevice implements IDevice {
        public byte[] bytesWritten;

        @Override
        public void writeBytes(byte[] cmd, int length) {
            bytesWritten = cmd;
        }

        @Override
        public void removeDataListener() {
            // TODO Auto-generated method stub

        }

        @Override
        public void addDataListener(MessageListener listener) {
            // TODO Auto-generated method stub

        }

        public byte[] stoba(String s) {
            String[] parts = s.split("\\s+");
            byte[] bOut = new byte[parts.length];

            for (int i = 0; i < parts.length; i++) {
                bOut[i] = (byte) (Integer.parseInt(parts[i], 16));
            }
            return bOut;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (byte b : bytesWritten) {
                sb.append("%02X ".formatted((b) & 0xff));
            }

            return sb.toString().trim();
        }
    }

    @Test
    void testStatusCmd() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.sendStatus(d);
        assertEquals("43 46 07 00 00 00 00 00 00 00 00 00 00", d.toString());
    }

    @Test
    void testSetupCmd_Initial() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.sendSetup(d);
        assertEquals("43 46 01 01 00 05 0D 0A", d.toString());
    }

    @Test
    void testConfigCmd_Initial() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.sendConfig(d);
        assertEquals("43 46 02 00 15 01 0D 0A", d.toString());
    }

    @Test
    void testSetupCmd_SpeedLimit() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.setLowerSpeedLimit(0);
        c.sendSetup(d);
        assertEquals("43 46 01 01 00 05 0D 0A", d.toString());

        c.setLowerSpeedLimit((int) (101f / 1.6));
        c.sendSetup(d);
        assertEquals("43 46 01 64 00 05 0D 0A", d.toString());

        c.setLowerSpeedLimit((int) (30f / 1.6));
        c.sendSetup(d);
        assertEquals("43 46 01 1C 00 05 0D 0A", d.toString());
    }

    @Test
    void testSetupCmd_Angle() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.setAngleComp(-1);
        c.sendSetup(d);
        assertEquals("43 46 01 01 00 05 0D 0A", d.toString());

        c.setAngleComp(91);
        c.sendSetup(d);
        assertEquals("43 46 01 01 5A 05 0D 0A", d.toString());

        c.setAngleComp(9);
        c.sendSetup(d);
        assertEquals("43 46 01 01 09 05 0D 0A", d.toString());
    }

    @Test
    void testSetupCmd_Direction() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.setDirection(Direction.IN);
        c.sendConfig(d);
        assertEquals("43 46 02 00 15 01 0D 0A", d.toString());

        c.setDirection(Direction.OUT);
        c.sendConfig(d);
        assertEquals("43 46 02 01 15 01 0D 0A", d.toString());

        c.setDirection(Direction.IN_OUT);
        c.sendConfig(d);
        assertEquals("43 46 02 02 15 01 0D 0A", d.toString());
    }

    @Test
    void testSetupCmd_Rate() {
        RadarConfig c = new RadarConfig();
        MockDevice d = new MockDevice();

        c.setReportRate(1);
        c.sendConfig(d);
        assertEquals("43 46 02 00 15 01 0D 0A", d.toString());

        c.setReportRate(2);
        c.sendConfig(d);
        assertEquals("43 46 02 00 0A 01 0D 0A", d.toString());

        c.setReportRate(11);
        c.sendConfig(d);
        assertEquals("43 46 02 00 01 01 0D 0A", d.toString());

        c.setReportRate(22);
        c.sendConfig(d);
        assertEquals("43 46 02 00 00 01 0D 0A", d.toString());

        c.setReportRate(0);
        c.sendConfig(d);
        assertEquals("43 46 02 00 15 01 0D 0A", d.toString());

        c.setReportRate(100);
        c.sendConfig(d);
        assertEquals("43 46 02 00 00 01 0D 0A", d.toString());

    }
}
