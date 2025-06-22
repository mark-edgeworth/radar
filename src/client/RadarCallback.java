/**
 *
 */
package client;

import common.RadarConfig.Direction;

/**
 *
 */
public class RadarCallback implements IResponseCallback {

    @Override
    public void recordSpeed(Direction d, float measurement) {
        System.out.println("Vehicle %s at %2.1f mph".formatted(d, measurement));
    }

    @Override
    public void message(String response) {
        System.out.println("Message: " + response);
    }

    @Override
    public void recordZero() {
        System.out.println();
    }

}
