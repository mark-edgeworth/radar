/**
 *
 */
package radar;

/**
 *
 */
public interface IDevice extends AutoCloseable {

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

    @Override
    void close();

    /**
     * @return
     */
    boolean open();
}
