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
	public static final Collection<String> extensions = Arrays.asList("txt", "log", "java");

	@Override
	public Collection<String> getExtensions()
	{
		return extensions;
	}

	@Override
	public void render(JDialog dialog, InputStream fileStream)
	{
		JTextArea textArea = new JTextArea();
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

			//TODO define charset
			return buf.toString("UTF-8");
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
