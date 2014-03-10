package ru.kkey.ui.preview;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author anstarovoyt
 */
public class ImagePreview implements Preview
{
	public static final Collection<String> extensions = Arrays.asList("jpg", "png", "gif");

	@Override
	public Collection<String> getExtensions()
	{
		return extensions;
	}

	@Override
	public void render(JDialog dialog, InputStream fileStream)
	{
		try
		{
			BufferedImage img = ImageIO.read(fileStream);

			JLabel picLabel = new JLabel(new ImageIcon(img));
			JScrollPane scrollPane = new JScrollPane(picLabel);
			dialog.add(scrollPane);

		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}

	}
}
