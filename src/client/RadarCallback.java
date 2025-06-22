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
        System.out.println("Vehicle %s us at %f mph".formatted(d, measurement));
    }

    @Override
    public void message(String response) {
        System.out.println("Message: " + response);
    }

}
