package com.github.display4j.core;

import com.github.display4j.core.conn.DisplayConnectionMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SSD1306AwtMock extends SSD1306 {
    private static final Logger logger = LoggerFactory.getLogger(SSD1306AwtMock.class);
    public static final int DEFAULT_SCALE_FACTOR = 3;
    public static final int DEFAULT_SLOW_DOWN_FACTOR = 50;

    // TODO why is this needed?
    int paddingX=20;
    int paddingY=30;

    int[] rgbaOn = new int[]{255, 255, 255};
    int[] rgbaOff = new int[]{0, 0, 0};


    int displayScaleFactor = DEFAULT_SCALE_FACTOR;
    int pixelSizeFactor = DEFAULT_SCALE_FACTOR;
    protected int displaySlowDownFactor = DEFAULT_SLOW_DOWN_FACTOR;

    JFrame displayFrame;

    // drawing to this buffer for output
    BufferedImage bufferedImage;
    //Graphics2D bufferAsGraphics;





    public SSD1306AwtMock(int width, int height) {
        super(new DisplayConnectionMock(), width, height);
        init();
    }

    /**
     * @param displayScaleFactor rescale the output to have a bigger screen - DEFAULT_SCALE_FACTOR is default
     * @param displaySlowDownFactor slow down factor in percent
     * <p>
     * 100% should be more or less comparable to speed of native display
     * attached to I2C @ 400.000 baud.<br/>
     * If you´re using I2C at default speed (100 kBaud) a factor of ~400
     * would be comparable to native speed.
     * </p><p>
     * Use 0 if you´re not interested in slowing things down artificially
     * </p>
     * </p><p>
     * DEFAULT_SLOW_DOWN_FACTOR is default
     * </p>
     */
    public SSD1306AwtMock(int width, int height, int displayScaleFactor, int displaySlowDownFactor) {
        super(new DisplayConnectionMock(), width, height);
        this.displayScaleFactor = displayScaleFactor;
        this.displaySlowDownFactor = displaySlowDownFactor;
        init();
    }

    private void init() {
        if (displayScaleFactor > 2) {
            pixelSizeFactor = displayScaleFactor - 1; // keep space
        }
        displayFrame = new JFrame(this.getClass().getSimpleName());
        displayFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayFrame.setSize(width * displayScaleFactor + paddingX * displayScaleFactor, height * displayScaleFactor + paddingY * displayScaleFactor);
        displayFrame.setVisible(true);
        displayFrame.setResizable(false);

        rgbaOff = new int[]{Color.black.getRGB(),Color.black.getRGB(),Color.black.getRGB()};
        rgbaOn = new int[]{Color.white.getRGB(),Color.white.getRGB(),Color.white.getRGB()};


        bufferedImage = new BufferedImage(
            width * displayScaleFactor + paddingX * displayScaleFactor,
            height * displayScaleFactor + paddingY * displayScaleFactor,
            BufferedImage.TYPE_BYTE_BINARY);
    }


    @Override
    public boolean setPixel(int x, int y, boolean on) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        int[] rgb = on ? rgbaOn : rgbaOff;
        bufferedImage.setRGB(
                x* displayScaleFactor + paddingX,
                y* displayScaleFactor + paddingY,
                pixelSizeFactor,
                pixelSizeFactor,
                rgb,
                0,
                0);
        return true;
    }


    @Override
    public synchronized void display() throws IOException {
        logger.info("display");
        java.awt.Graphics g = displayFrame.getGraphics();
        g.drawImage(bufferedImage, 0,0, null);
        try {
            Thread.sleep((long) (displaySlowDownFactor * 2.2)); // slow down
        } catch (Exception ex) {}
    }

    @Override
    public void clearBuffer() {
        fillBufferWithPattern((byte) 0);
    }


    @Override
    public void rasterGraphics2DImage(boolean display) throws IOException {
        logger.info("rasterGraphics2DImage");
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int rgb = super.bufferedImage.getRGB(x, y);
                int rgbA = rgb & 0xff;
                int rgbB = rgb & 0xff00 >> 8;
                int rgbC = rgb & 0xff0000 >> 16;

                //setPixelRgb(x, y, rgbA);
                setPixel(x, y, (rgbA > 0));
            }
        }

        if (display) {
            display();
        }
    }

    public int getDisplaySlowDownFactor() {
        return displaySlowDownFactor;
    }

    public void setDisplaySlowDownFactor(int displaySlowDownFactor) {
        this.displaySlowDownFactor = displaySlowDownFactor;
    }
}
