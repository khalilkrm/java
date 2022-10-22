package graphics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Image {
	/**
	 * Returns a BufferedImage as the result of decoding the file with supplied
	 * pathname.
	 * 
	 * @param pathname
	 *            A pathname String.
	 * @return A BufferedImage containing the decoded contents of the input, or
	 *         null.
	 */
	public static BufferedImage loadImage(String pathname) {
		BufferedImage canvas = null;
		try {
			canvas = ImageIO.read(new File(pathname));
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		return canvas;
	}

	/**
	 * Displays a BufferedImage in a window.
	 * 
	 * @param image	A BufferedImage to be displayed.
	 * @param title The window's title.
	 */
    public static DrawingPanel displayImage(BufferedImage image, String title) {
    	if (image == null) return null;
		DrawingPanel p = new DrawingPanel(image.getWidth(), image.getHeight());
		if (title != null) p.setTitle(title);
		Graphics g = p.getGraphics();
		g.drawImage(image, 0, 0, null);
		return p;
	}
    
	/**
	 * Displays a BufferedImage in a window.
	 * 
	 * @param image	A BufferedImage to be displayed.
	 * @param title The window's title.
	 */
    public static void updateImage(DrawingPanel p, BufferedImage image, String title) {
    	if (image == null || p == null) return;
		if (title != null) p.setTitle(title);
		Graphics g = p.getGraphics();
		g.drawImage(image, 0, 0, null);
	}
    
    /**
     * Converts a BufferedImage to a specific imageType
     * 
     * @param image A BufferedImage to be converted
     * @param type The target imageType
     * @return The converted image
     */
	public static BufferedImage convertToType(BufferedImage image, int type) {
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), type);
	    Graphics2D graphics = newImage.createGraphics();
	    graphics.drawImage(image, 0, 0, null);
	    graphics.dispose();
	    return newImage;
	}
}
