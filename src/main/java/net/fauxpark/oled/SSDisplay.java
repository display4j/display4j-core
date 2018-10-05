package net.fauxpark.oled;

/*
 * Licensed under The MIT License
 *
 * Copyright (c) 2017 fauxpark <fauxpark@gmail.com>
 *
 * see /LICENSE-MIT
 */
/**
 * A base class for defining implementations of the SSD1306 OLED display.
 *
 * @author fauxpark
 */
public abstract class SSDisplay {
	/**
	 * A helper class for drawing lines, shapes, text and images.
	 */
	private Graphics graphics;

	/**
	 * The width of the display in pixels.
	 */
	protected int width;

	/**
	 * The height of the display in pixels.
	 */
	protected int height;

	/**
	 * The number of pages in the display.
	 */
	protected int pages;

	/**
	 * The display buffer.
	 */
	protected byte[] buffer;

	/**
	 * Indicates whether the display has been started up.
	 */
	private boolean initialised;

	/**
	 * Indicates whether the display is on or off.
	 */
	private boolean displayOn;

	/**
	 * Indicates whether the display is inverted.
	 */
	private boolean inverted;

	/**
	 * Indicates whether the display is horizontally flipped.
	 */
	private boolean hFlipped;

	/**
	 * Indicates whether the display is vertically flipped.
	 */
	private boolean vFlipped;

	/**
	 * Indicates whether the display is currently scrolling.
	 */
	private boolean scrolling;

	/**
	 * The current contrast level of the display.
	 */
	private int contrast;

	/**
	 * The current display offset.
	 */
	private int offset;

	/**
	 * SSD1306 constructor.
	 *
	 * @param width The width of the display in pixels.
	 * @param height The height of the display in pixels.
	 */
	public SSDisplay(int width, int height) {
		this.width = width;
		this.height = height;
		pages = height / 8;
		buffer = new byte[width * pages];
	}

	/**
	 * Get the initialised state of the display.
	 */
	public boolean isInitialised() {
		return initialised;
	}

	/**
	 * Start the power on procedure for the display.
	 *
	 * @param externalVcc Indicates whether the display is being driven by an external power source.
	 */
	public void startup(boolean externalVcc) {
		initialised = true;
	}

	/**
	 * Start the power off procedure for the display.
	 */
	public void shutdown() {
		initialised = false;
		setInverted(false);
		setHFlipped(false);
		setVFlipped(false);
		stopScroll();
		setContrast(0);
		setOffset(0);
	}

	/**
	 * Reset the display.
	 */
	public abstract void reset();

	/**
	 * Clear the buffer.
	 * <br/>
	 * NOTE: This does not clear the display, you must manually call {@link#display()}.
	 */
	public void clear() {
		buffer = new byte[width * pages];
	}

	/**
	 * Send the buffer to the display.
	 */
	public abstract void display();

	/**
	 * Get the width of the display.
	 *
	 * @return The display width in pixels.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get the height of the display.
	 *
	 * @return The display height in pixels.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Get the display state.
	 *
	 * @return True if the display is on.
	 */
	public boolean isDisplayOn() {
		return displayOn;
	}

	/**
	 * Turn the display on or off.
	 *
	 * @param displayOn Whether to turn the display on.
	 */
	public void setDisplayOn(boolean displayOn) {
		this.displayOn = displayOn;
	}

	/**
	 * Get the inverted state of the display.
	 *
	 * @return Whether the display is inverted or not.
	 */
	public boolean isInverted() {
		return inverted;
	}

	/**
	 * Invert the display.
	 * When inverted, an "on" bit in the buffer results in an unlit pixel.
	 *
	 * @param inverted Whether to invert the display or return to normal.
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	/**
	 * Get the display contrast.
	 *
	 * @return The current contrast level of the display.
	 */
	public int getContrast() {
		return contrast;
	}

	/**
	 * Set the display contrast.
	 *
	 * @param contrast The contrast to set, from 0 to 255. Values outside of this range will be clamped.
	 */
	public void setContrast(int contrast) {
		if(contrast < 0) {
			contrast = 0;
		} else if(contrast > 255) {
			contrast = 255;
		}

		this.contrast = contrast;
	}

	/**
	 * Get the display offset.
	 *
	 * @return The number of rows the display is offset by.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Set the display offset.
	 *
	 * @param offset The number of rows to offset the display by. Values outside of this range will be clamped.
	 */
	public void setOffset(int offset) {
		if(offset < 0) {
			offset = 0;
		} else if(offset > height - 1) {
			offset = height - 1;
		}

		this.offset = offset;
	}

	/**
	 * Get the scrolling state of the display.
	 *
	 * @return Whether the display is scrolling.
	 */
	public boolean isScrolling() {
		return scrolling;
	}

	/**
	 * Scroll the display horizontally.
	 *
	 * @param direction The direction to scroll, where a value of true results in the display scrolling to the left.
	 * @param start The start page address, from 0 to 7.
	 * @param end The end page address, from 0 to 7.
	 * @param speed The scrolling speed (scroll step).
	 *
	 * @see Constant#SCROLL_STEP_5
	 */
	public abstract void scrollHorizontally(boolean direction, int start, int end, int speed);

	/**
	 * Scroll the display horizontally and vertically.
	 *
	 * @param direction The direction to scroll, where a value of true results in the display scrolling to the left.
	 * @param start The start page address, from 0 to 7.
	 * @param end The end page address, from 0 to 7.
	 * @param offset The number of rows from the top to start the vertical scroll area at.
	 * @param rows The number of rows in the vertical scroll area.
	 * @param speed The scrolling speed (scroll step).
	 * @param step The number of rows to scroll vertically each frame.
	 *
	 * @see Constant#SCROLL_STEP_5
	 */
	public abstract void scrollDiagonally(boolean direction, int start, int end, int offset, int rows, int speed, int step);

	/**
	 * Start scrolling the display.
	 */
	public void startScroll() {
		scrolling = true;
	}

	/**
	 * Stop scrolling the display.
	 */
	public void stopScroll() {
		scrolling = false;
	}

	/**
	 * No operation.
	 */
	public abstract void noOp();

	/**
	 * Get the horizontal flip state of the display.
	 *
	 * @return Whether the display is horizontally flipped.
	 */
	public boolean isHFlipped() {
		return hFlipped;
	}

	/**
	 * Flip the display horizontally.
	 *
	 * @param hFlipped Whether to flip the display or return to normal.
	 */
	public void setHFlipped(boolean hFlipped) {
		this.hFlipped = hFlipped;
	}

	/**
	 * Get the vertical flip state of the display.
	 *
	 * @return Whether the display is vertically flipped.
	 */
	public boolean isVFlipped() {
		return vFlipped;
	}

	/**
	 * Flip the display vertically.
	 *
	 * @param vFlipped Whether to flip the display or return to normal.
	 */
	public void setVFlipped(boolean vFlipped) {
		this.vFlipped = vFlipped;
	}

	/**
	 * Get a pixel in the buffer.
	 *
	 * @param x The X position of the pixel to set.
	 * @param y The Y position of the pixel to set.
	 *
	 * @return False if the pixel is "off" or the given coordinates are out of bounds, true if the pixel is "on".
	 */
	public boolean getPixel(int x, int y) {
		if(x < 0 || x >= width || y < 0 || y >= height) {
			return false;
		}

		return (buffer[x + (y / 8) * width] & (1 << (y & 7))) != 0;
	}

	/**
	 * Set a pixel in the buffer.
	 *
	 * @param x The X position of the pixel to set.
	 * @param y The Y position of the pixel to set.
	 * @param on Whether to turn this pixel on or off.
	 *
	 * @return False if the given coordinates are out of bounds.
	 */
	public boolean setPixel(int x, int y, boolean on) {
		if(x < 0 || x >= width || y < 0 || y >= height) {
			return false;
		}

		if(on) {
			buffer[x + (y / 8) * width] |= (1 << (y & 7));
		} else {
			buffer[x + (y / 8) * width] &= ~(1 << (y & 7));
		}

		return true;
	}

	/**
	 * Get the display buffer.
	 *
	 * @return The display buffer.
	 */
	public byte[] getBuffer() {
		return buffer;
	}

	/**
	 * Set the display buffer.
	 *
	 * @param buffer The buffer to set.
	 */
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}

	/**
	 * Send a command to the display.
	 *
	 * @param command The command to send.
	 * @param params Any parameters the command requires.
	 */
	public abstract void command(int command, int... params);

	/**
	 * Send pixel data to the display.
	 *
	 * @param data The data to send.
	 */
	public abstract void data(byte[] data);

	/**
	 * Get the Graphics instance, creating it if necessary.
	 *
	 * @return The Graphics instance.
	 */
	public final Graphics getGraphics() {
		if(graphics == null) {
			graphics = new Graphics(this);
		}

		return graphics;
	}
}
