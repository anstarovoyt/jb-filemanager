package ru.kkey.ui.preview;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author anstarovoyt
 */
public class TextPreview implements Preview
{
	@Override
	public Collection<String> getExtentions()
	{
		return Arrays.asList("txt");
	}

	@Override
	public void render(JDialog dialog, InputStream fileStream)
	{
		JTextArea textArea = new JTextArea(30, 40);
		textArea.setWrapStyleWord(true);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		textArea.setText(readInputStreamAsString(fileStream));
		textArea.setCaretPosition(0);
		dialog.add(scrollPane);
	}

	public static String readInputStreamAsString(InputStream in)
	{
		try
		{
			BufferedInputStream bis = new BufferedInputStream(in);
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			int result = bis.read();
			while (result != -1)
			{
				byte b = (byte) result;
				buf.write(b);
				result = bis.read();
			}

			return buf.toString();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
