/**
 *
 */
package radar;

/**
 *
 */
public interface IDevice {

    /**
     * @param cmd
     * @param length
     */
    void writeBytes(byte[] cmd, int length);

    /**
     * @param listener
     */
    void addDataListener(MessageListener listener);

    /**
     *
     */
    void removeDataListener();

}
