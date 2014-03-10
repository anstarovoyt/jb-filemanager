package ru.kkey.ui.preview;

import java.util.Arrays;
import java.util.List;

/**
 * The register contains all preview types
 *
 * @author anstarovoyt
 * @see Preview
 */

public class PreviewRegistry
{
	public static final List<Preview> previews = Arrays.<Preview>asList(new TextPreview());

	private static final PreviewRegistry instance = new PreviewRegistry();

	public static PreviewRegistry get()
	{
		return instance;
	}

	public List<Preview> getPreviews()
	{
		return previews;
	}
}
