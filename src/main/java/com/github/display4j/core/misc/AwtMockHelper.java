package com.github.display4j.core.misc;

import com.github.display4j.core.SSD1306AwtMock;
import com.github.display4j.core.SSDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

public class AwtMockHelper {
    private static final Logger logger = LoggerFactory.getLogger(AwtMockHelper.class);

    private SSDisplay display;

    public static final int DEFAULT_SCALE_FACTOR = 3;
    public static final int DEFAULT_SLOW_DOWN_FACTOR = 100;
    public static final int DEFAULT_ASSUMED_PORT_SPEED = 400000; // default I2C speed


    // TODO why is this needed?
    int paddingX=20;
    int paddingY=30;

    protected int displayScaleFactor = AwtMockHelper.DEFAULT_SCALE_FACTOR;
    protected int pixelSizeFactor = AwtMockHelper.DEFAULT_SCALE_FACTOR;

    // will be recalculated
    protected long displaySleepFactor = -1;


    protected int assumedPortSpeed = AwtMockHelper.DEFAULT_ASSUMED_PORT_SPEED;

    /**
     * in percent - 100 should be more or less native speed,
     * lesser should be faster - 0 fast as possible
     */
    protected int displaySlowDownFactor = AwtMockHelper.DEFAULT_SLOW_DOWN_FACTOR;



    public AwtMockHelper(SSDisplay display) {
        this.display = display;

        reCalcDisplaySleepFactor();
        setDisplayScaleFactor(DEFAULT_SCALE_FACTOR);
    }

    public void sleepAfterDraw() {
        try {
            // slow down
            Thread.sleep(getDisplaySleepFactor());
        } catch (Exception ex) {}
    }

    protected void reCalcDisplaySleepFactor() {
        // 9 = bits per byte + overhead
        if (display.getBuffer() == null) {
            logger.error("buffer not initialized");
        }
        float pSpeedByBufferFPS = (float)getAssumedPortSpeed() / (float)display.getBuffer().length / (float)9;

        long sleepFactor = (long)(1000 / pSpeedByBufferFPS);
        sleepFactor = (long)((float)sleepFactor * ((float)getDisplaySlowDownFactor() / 100));

        this.displaySleepFactor = sleepFactor;

        logger.warn("estimated FPS: {} - sleepFactor for Mock: {}", pSpeedByBufferFPS, sleepFactor);

        //return sleepFactor;
    }

    public BufferedImage getBufferedImage(int imageType) {
        return new BufferedImage(
                display.getWidth() * displayScaleFactor + paddingX * displayScaleFactor,
                display.getHeight() * displayScaleFactor + paddingY * displayScaleFactor,
                imageType);
    }

    public void setPixelRgb(BufferedImage bufferedImage, int x, int y, int[] rgb) {
        bufferedImage.setRGB(
                x* displayScaleFactor + paddingX,
                y* displayScaleFactor + paddingY,
                pixelSizeFactor,
                pixelSizeFactor,
                rgb,
                0,
                0);
    }


    public int getDisplaySlowDownFactor() {
        return displaySlowDownFactor;
    }

    public void setDisplaySlowDownFactor(int displaySlowDownFactor) {
        this.displaySlowDownFactor = displaySlowDownFactor;
        reCalcDisplaySleepFactor();
    }

    public int getAssumedPortSpeed() {
        return assumedPortSpeed;
    }

    public void setAssumedPortSpeed(int assumedPortSpeed) {
        this.assumedPortSpeed = assumedPortSpeed;
        reCalcDisplaySleepFactor();
    }

    public int getDisplayScaleFactor() {
        return displayScaleFactor;
    }

    public void setDisplayScaleFactor(int displayScaleFactor) {
        if (displayScaleFactor > 2) {
            pixelSizeFactor = displayScaleFactor - 1; // keep space
        }
        this.displayScaleFactor = displayScaleFactor;
    }

    public int getPixelSizeFactor() {
        return pixelSizeFactor;
    }

    public void setPixelSizeFactor(int pixelSizeFactor) {
        this.pixelSizeFactor = pixelSizeFactor;
    }

    public long getDisplaySleepFactor() {
        return displaySleepFactor;
    }
}
