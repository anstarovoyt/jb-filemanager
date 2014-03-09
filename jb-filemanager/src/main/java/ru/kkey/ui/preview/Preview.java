package ru.kkey.ui.preview;

import javax.swing.*;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author anstarovoyt
 */
public interface Preview
{
	Collection<String> getExtentions();

	void render(JDialog dialog, InputStream fileStream);
}
