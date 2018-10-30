package com.github.display4j.core;

import com.github.display4j.core.conn.DisplayConnectionMock;
import com.github.display4j.core.misc.AwtMockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class SSD1306AwtMock extends SSD1306 {
    private static final Logger logger = LoggerFactory.getLogger(SSD1306AwtMock.class);

    int[] rgbaOn = new int[]{Color.white.getRGB(),Color.white.getRGB(),Color.white.getRGB()};
    int[] rgbaOff = new int[]{Color.black.getRGB(),Color.black.getRGB(),Color.black.getRGB()};


    JFrame displayFrame;

    // drawing to this buffer for output
    BufferedImage bufferedImage;

    AwtMockHelper awtMockHelper = null;


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
        init();
        awtMockHelper.setDisplayScaleFactor(displayScaleFactor);
        awtMockHelper.setDisplaySlowDownFactor(displaySlowDownFactor);
    }

    private void init()
    {
        awtMockHelper = new AwtMockHelper(this);

        bufferedImage = awtMockHelper.getBufferedImage(BufferedImage.TYPE_BYTE_BINARY);

        displayFrame = new JFrame(this.getClass().getSimpleName());
        displayFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayFrame.setSize(bufferedImage.getWidth(), bufferedImage.getHeight());
        displayFrame.setVisible(true);
        displayFrame.setResizable(false);
    }


    @Override
    public boolean setPixel(int x, int y, boolean on) {
        if(x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        int[] rgb = on ? rgbaOn : rgbaOff;
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


    @Override
    public void rasterGraphics2DImage(boolean display) throws IOException {
        logger.info("rasterGraphics2DImage");
        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                int rgb = super.bufferedImage.getRGB(x, y);
                int rgbA = rgb & 0xff;

                setPixel(x, y, (rgbA > 0));
            }
        }

        if (display) {
            display();
        }
    }
}
