package ru.kkey.ui.widget;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * The panel render image with scalling over window size
 *
 * @author astarovoyt
 */
public class AutoResizeImagePanel extends JPanel
{
	private static final long serialVersionUID = 1L;

	private final BufferedImage image;
	private Image resizedImage;
	private int prevWidth = -1;
	private int prevHeight = -1;


	public AutoResizeImagePanel(BufferedImage image)
	{
		this.image = getNewBufferedImage(image);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

		int w = getWidth();
		int h = getHeight();
		int iw = image.getWidth();
		int ih = image.getHeight();
		double xScale = (double) w / iw;
		double yScale = (double) h / ih;
		double scale = Math.min(xScale, yScale);
		int width = scale < 1 ? (int) (scale * iw) : iw;
		int height = scale < 1 ? (int) (scale * ih) : ih;

		if (width != prevWidth || height != prevHeight)
		{
			resizedImage = image.getScaledInstance(width, height, Image.SCALE_FAST);
			prevHeight = height;
			prevWidth = width;
		}
		int x = (w - width) / 2;
		int y = (h - height) / 2;
		g2.drawImage(resizedImage, x, y, this);

	}

	public BufferedImage getNewBufferedImage(BufferedImage image)
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice screen = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = screen.getDefaultConfiguration();
		BufferedImage newImage = gc.createCompatibleImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);
		newImage.getGraphics().drawImage(image, 0, 0, null);
		return newImage;
	}
}
