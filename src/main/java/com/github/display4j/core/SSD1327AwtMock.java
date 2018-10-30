package com.github.display4j.core;

import com.github.display4j.core.conn.DisplayConnectionMock;
import com.github.display4j.core.misc.AwtMockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SSD1327AwtMock extends SSD1327{
    private static final Logger logger = LoggerFactory.getLogger(SSD1327AwtMock.class);

    JFrame displayFrame;

    // drawing to this buffer for output
    BufferedImage bufferedImage;

    AwtMockHelper awtMockHelper = null;

    public SSD1327AwtMock(int width, int height) {
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
    public SSD1327AwtMock(int width, int height, int displayScaleFactor, int displaySlowDownFactor) {
        super(new DisplayConnectionMock(), width, height);
        init();
        awtMockHelper.setDisplayScaleFactor(displayScaleFactor);
        awtMockHelper.setDisplaySlowDownFactor(displaySlowDownFactor);
    }

    private void init() {
        awtMockHelper = new AwtMockHelper(this);

        bufferedImage = awtMockHelper.getBufferedImage(BufferedImage.TYPE_BYTE_GRAY);

        displayFrame = new JFrame(this.getClass().getSimpleName());
        displayFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayFrame.setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
        displayFrame.setVisible(true);
        displayFrame.setResizable(false);
    }

    @Override
    public boolean setPixel(int x, int y, boolean on) {
        return setPixel(x, y, 255);
    }

    @Override
    public boolean setPixel(int x, int y, int grey) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        Color col = (new Color(grey, grey, grey));

        int[] rgb = {col.getRGB(), col.getRGB(), col.getRGB()};
        awtMockHelper.setPixelRgb(bufferedImage, x, y, rgb);


        return true;
    }

    @Override
    public synchronized void display() throws IOException {
        logger.info("display");
        java.awt.Graphics g = displayFrame.getGraphics();
        g.drawImage(bufferedImage, 0,0, null);
        awtMockHelper.sleepAfterDraw();
    }

    @Override
    public void clearBuffer() {
        fillBufferWithPattern((byte) 0);
    }

    // TODO this seems not to work
    @Override
    public void fillBufferWithPattern(byte bPattern) {
        int grey = bPattern & 0xFF;
        logger.info("fillBufferWithPattern");
        for (int x=0; x<height; x++) {
            for (int y=0; y<width; y++) {
                setPixel(y, x, grey);
            }
        }
    }

    @Override
    public void rasterGraphics2DImage(boolean display) throws IOException {
        logger.info("rasterGraphics2DImage");
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int rgb = super.bufferedImage.getRGB(x, y);
                int[] rgbA = {rgb, rgb, rgb};

                awtMockHelper.setPixelRgb(bufferedImage, x, y, rgbA);
            }
        }

        if (display) {
            display();
        }
    }
}
