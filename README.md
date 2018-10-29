# display4j-core: SSDxxxx in Java
[![Build Status](https://travis-ci.org/display4j/display4j-core.svg?branch=master)](https://travis-ci.org/display4j/display4j-core)

This is a driver for Solomon Systech Displays (SSD1306, SSD1327 - f.e. [Adafruit SSD1306 OLED display](https://www.adafruit.com/categories/98) and others) on the Raspberry Pi, written (almost) completely in Java.
It makes use of the [Pi4J](https://github.com/Pi4J/pi4j) library, which does all the fiddly bits in native code to operate the GPIO pins and drive the SPI and I<sup>2</sup>C interfaces.

The aim of this project is to abstract away the low-level aspects of the SSD and focus on manipulating the contents of the screen through a very simple API.

| Device    | Mock |
|-----------|------|
|![example of SSD1327](https://raw.githubusercontent.com/display4j/display4j-docs/master/img/IMG_20181005_195320_cut.jpg)|![example of SSD1327 Awt Mock](https://raw.githubusercontent.com/display4j/display4j-docs/master/img/ssd1327_awt_mock_128_128.png)|
|![example of SSD1306](https://raw.githubusercontent.com/display4j/display4j-docs/master/img/IMG_20181029_222258__01__01.jpg)|![example of SSD1306 Awt Mock](https://raw.githubusercontent.com/display4j/display4j-docs/master/img/ssd1306_awt_mock_128_64.png)|

### Display support

| Device                              | state                |
|-------------------------------------|----------------------|
| SSD1306 (128px * 32 / 64 pxx * 1bit | working SPI and I2C  |
| SSD1327 (128px * 128px * 4bit grey) | working I2C (and should with SPI - is there a SPI variant?)        |

Other types should be easy to add if pixel-bitsize is the same


## GPIO Pinout

The pinout for the Raspberry Pi GPIO header is as follows:

| Display Pin  | Physical Pin (SPI)  | Physical Pin (I<sup>2</sup>C) |
| ------------:|:------------------- |:----------------------------- |
| Ground       | 6                   | 6                             |
| Voltage In   | 1                   | 1                             |
| 3v3          | N/C                 | N/C                           |
| Chip Select  | 24 (CS0) / 26 (CS1) | N/C                           |
| Reset        | 8 (GPIO_15) / Any   | 8 (GPIO_15) / Any (or N/C)    |
| Data/Command | 10 (GPIO_16) / Any  | N/C (0x3D) / GND (0x3C)       |
| Clock        | 23                  | 5                             |
| Data         | 19                  | 3                             |

## Getting Started 

### Quick-Start display test on RPi
Have a look at the project [display4j-examples](https://github.com/display4j/display4j-examples)

Can be as easy as ...
```bash
# ./run.sh dspTest SSD1327 I2C
```

### Java

To set up the display, simply create a new `SSDisplay` object, like so:

```java
DisplayConnection dspConn;
SSDisplay display;

// choose connection type SPI
dspConn = new DisplayConnectionSPI();

// or I2C
dspConn = new DisplayConnectionI2C();


// choose display type SSD1306
display = new SSD1306(dspConn, 128, 64);

// or SSD1327
display = new SSD1327(dspConn);

// or SSD1327 AWT Mock version for dev purposes
display = new SSD1327AwtMock();


// call startup
display.startup(false);

// and start using it..

// Turns the pixel in the top left corner on
display.setPixel(0, 0, true);

// Sends the internal buffer to the display
display.display();

// Inverts the display
display.setInverted(true);
```

Most properties of the display (eg. invertedness, display on/off) are reachable through getters and setters.
As the SSD controllers do not provide any information as to its state, these are implemented as fields in the `SSD1306` class.

## 2D Graphics with AWT

You can also do line & shape drawing using the `Graphics2D` class from `java.awt`, load images and render
fonts. On greyscale devices this automatically enables anti-aliasing.

Just call the `getGraphics2D()` method on the `SSDisplay` instance:

```java
// SSDisplay display = new SSD1306(dspConn, 128, 64);
SSDisplay display = new SSD1327(new DisplayConnectionI2C());
display.startup(false);

// make use of Java AWT Graphics2D
Graphics2D graphics = display.getGraphics2D();

// Draws a line from the top left to the bottom right of the display
graphics.drawRect(0, 0, display.getWidth()-1, display.getHeight()-1);

// Draws an arc from (63,31) with a radius of 8 pixels and an angle of 15 degrees
graphics.drawArc(display.getWidth() -25, 10, 30,30, 0, 360);

// Writes "Hello world!" at (20,20) using the Windows-1252 charset
Font font = new Font("Serif", Font.BOLD, 18);
graphics.setFont(font);
graphics.drawString( "Hello world!", 5, 20);

display.rasterGraphics2DImage(true);
```

## Legacy Text Rendering

This library can draw text onto the screen (wihtout using `java.awt`). It is also possible to change the character set.

Currently three character sets are supported:

- [**CP437**](https://en.wikipedia.org/wiki/Code_page_437) (from the IBM PC)
- [**CP850**](https://en.wikipedia.org/wiki/Code_page_850) (MS-DOS, also used in the Windows Command Prompt)
- [**Windows-1252**](https://en.wikipedia.org/wiki/Windows-1252) (the first 256 codepoints of Unicode; use this for the closest mapping between entered text and display output)

In addition, it is possible to create your own character sets by implementing `Font` and specifying the number of rows and columns, and the array of glyph data. Refer to the `Font` JavaDoc for an explanation on how the glyphs are encoded.

## Issues and limitations

### Font rendering issues using Mocks
If you compare the font output of the display with the output of the Mock on your DEV machine
you might encounter a difference in the font rendering (appearance and size).
This is not because of differences in implementation between device vs. Mock... but if
you´re working on Windows on your DEV machine (like me) and run the device implementation
on the Raspberry - you just see that both systems use different fonts. This is more a Linux
vs. Windows issue.

You can circumvent that if you develop on Linux yourself or your project includes 
it´s own TTF fonts and loads it like described 
in [https://docs.oracle.com/javase/tutorial/2d/text/fonts.html](https://docs.oracle.com/javase/tutorial/2d/text/fonts.html).

```java
try {
     GraphicsEnvironment ge = 
         GraphicsEnvironment.getLocalGraphicsEnvironment();
     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("A.ttf"));
} catch (IOException|FontFormatException e) {
     //Handle exception
}
```

Or you don´t use Graphics2D and use the legacy font rendering (see above).


### I2C Mode (& grey-scale displays - like SDD1327)

Because of the larger amounts of data needed to transfer on every display update
when using grayscale-displays
(f.e. the SDD1327 has 128 x 128 pixel x 4 bits per pixel = 8129 bytes), perhaps you may consider speeding up your I2C bus from default 100 kHz to 400 kHz.

Refer to [https://www.raspberrypi-spy.co.uk/2018/02/change-raspberry-pi-i2c-bus-speed/](https://www.raspberrypi-spy.co.uk/2018/02/change-raspberry-pi-i2c-bus-speed/)

This might not be such a huge problem using sw-displays but might be a limiting factor there too.
The display update of SSD1306 with 128 x 64 px x 1 BPP consumes 1024 bytes.


## To be done / wish list

* support more devices / test other dimensions
* implement a method for only updating an area of the whole display

## Contributions

Contributions are welcome... 

## Credits

Essential basis from
* [fauxpark/oled-core](https://github.com/fauxpark/oled-core) (all code for SSD1306)
* [PI4J](http://pi4j.com/) (IO-communication lib)

Some of this code has been borrowed from 
* [py-gaugette](https://github.com/guyc/py-gaugette)
* [pi-ssd1306-java](https://github.com/ondryaso/pi-ssd1306-java)
* [raspberry-pi4j-samples](https://github.com/OlivierLD/raspberry-pi4j-samples/)



The glyph data for the CP437 character set has been modified somewhat but appears to have originated from the [Adafruit GFX Library](https://github.com/adafruit/Adafruit-GFX-Library).
