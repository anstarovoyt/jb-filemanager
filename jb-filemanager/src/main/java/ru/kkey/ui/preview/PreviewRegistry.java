package ru.kkey.ui.preview;

import java.util.Arrays;
import java.util.List;

/**
 * The registry contains all preview types
 *
 * @author anstarovoyt
 * @see Preview
 */

public class PreviewRegistry
{
    private static final List<Preview> previews = Arrays.asList(new TextPreview(), new ImagePreview());
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
