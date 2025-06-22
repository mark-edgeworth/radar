/**
 *
 */
package client;

import common.RadarConfig.Direction;

/**
 *
 */
public interface IResponseCallback {

    /**
     * A speed recording was made
     *
     * @param d
     *     The direction of travel
     * @param measurement
     *     The measurement in MPH
     */
    void recordSpeed(Direction d, float measurement);

    /**
     * @param response
     */
    void message(String response);

}
