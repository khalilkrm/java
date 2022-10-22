package spanning;

import java.awt.image.BufferedImage;

public class PixelColor {

    private final int red;
    private final int green;
    private final int blue;

    public PixelColor(final int red, final int green, final int blue) {
        this.red = red;
        this.blue = blue;
        this.green = green;
    }

    public static PositionSetter from(final BufferedImage image) {
        return new PositionSetter(image);
    }

    public boolean hasRgb(final int red, final int green, final int blue) {
        return this.red == red && this.green == green && this.blue == blue;
    }

    @Override
    public String toString() {
        return "PixelColor{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                '}';
    }

    static class PositionSetter {

        private final BufferedImage image;

        private PositionSetter(final BufferedImage image) {
            this.image = image;
        }

        PixelColor at(final Point point) {
            int  clr   = image.getRGB(point.getX(), point.getY());
            int  red   = (clr & 0x00ff0000) >> 16;
            int  green = (clr & 0x0000ff00) >> 8;
            int  blue  =  clr & 0x000000ff;
            return new PixelColor(red, green, blue);
        }
    }
}