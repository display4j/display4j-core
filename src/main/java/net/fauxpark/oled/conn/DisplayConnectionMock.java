package net.fauxpark.oled.conn;

import net.fauxpark.oled.misc.HexConversionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * just dumps the display connection calls to log
 */
public class DisplayConnectionMock extends DisplayConnection {
    private static final Logger logger = LoggerFactory.getLogger(DisplayConnectionMock.class);



    /**
     * minimal constructor - will use DEFAULT_I2C_BUS_ID and DEFAULT_I2C_ADDRESS
     */
    public DisplayConnectionMock() {
        logger.info("init mock");
    }


    @Override
    public void command(int command, int... params) throws IOException {
        if (params == null || params.length == 0) {
            logger.info("command: {}", HexConversionHelper.int255ToHex(command));
        } else {
            logger.info("command: {}, params: {}", HexConversionHelper.int255ToHex(command), HexConversionHelper.ints255ToHex(params));
        }
    }

    @Override
    public void data(byte[] data) throws IOException {
        if (data == null) {
            throw new RuntimeException("data should not be null");
        }
        //logger.info("data: {}", bytesToHex(data));
        logger.info("data: {}", data.length);
    }

    @Override
    public void data(byte[] data, int start, int len) throws IOException {
        if (data == null) {
            throw new RuntimeException("data should not be null");
        }
        if (data.length < start + len) {
            throw new RuntimeException("data too short");
        }
        if (len == 0) {
            throw new RuntimeException("len = 0");
        }
        //logger.info("data: {}", bytesToHex(data));
        logger.info("data: {}, start: {}, len: {}", data.length, start, len);
    }

    @Override
    public void reset() {
        logger.info("reset");
    }

    @Override
    public void shutdown() {
        logger.info("shutdown");
    }
}
