package ru.kkey.ui.widget;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * The panel render image with scalling over window size
 *
 * @author astarovoyt
 */
public class AutoResizeImagePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final BufferedImage image;
	private final int imageWidth;
	private final int imageHeight;
	private Image prevImage;
	private int prevWidth = -1;
	private int prevHeight = -1;



	public AutoResizeImagePanel(BufferedImage image)
	{
		this.imageWidth = image.getWidth();
		this.imageHeight = image.getHeight();
		this.image = getNewBufferedImage(image);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		setHits(g2);

		int w = getWidth();
		int h = getHeight();
		int iw = getImageWidth();
		int ih = getImageHeight();
		double xScale = (double) w / iw;
		double yScale = (double) h / ih;
		double scale = Math.min(xScale, yScale);
		int width = scale < 1 ? (int) (scale * iw) : iw;
		int height = scale < 1 ? (int) (scale * ih) : ih;

		if (width != prevWidth || height != prevHeight)
		{
			prevImage = scaleImage(image, width, height);
			prevHeight = height;
			prevWidth = width;
		}
		int x = (w - width) / 2;
		int y = (h - height) / 2;
		g2.drawImage(prevImage, x, y, this);
	}

	private void setHits(Graphics2D g2)
	{
		Map<RenderingHints.Key, Object> hits = new HashMap<>();
		hits.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		hits.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		hits.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		hits.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		hits.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		hits.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		g2.addRenderingHints(hits);
	}

	private int getImageHeight()
	{
		return imageHeight;
	}

	private int getImageWidth()
	{
		return imageWidth;
	}

	private BufferedImage getNewBufferedImage(BufferedImage image)
	{
		GraphicsConfiguration gc = getConfiguration();
		BufferedImage newImage = gc.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);
		Graphics2D graphics = (Graphics2D) newImage.getGraphics();
		setHits(graphics);
		graphics.drawImage(image, 0, 0, null);
		graphics.dispose();
		return newImage;
	}

	private GraphicsConfiguration getConfiguration()
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen = ge.getDefaultScreenDevice();
		return screen.getDefaultConfiguration();
	}

	private BufferedImage scaleImage(BufferedImage src,
									 int targetWidth, int targetHeight)
	{
		BufferedImage result = new BufferedImage(
				targetWidth,
				targetHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D resultGraphics = result.createGraphics();
		setHits(resultGraphics);
		resultGraphics.drawImage(src, 0, 0, targetWidth, targetHeight, null);
		resultGraphics.dispose();
		return result;
	}

}
