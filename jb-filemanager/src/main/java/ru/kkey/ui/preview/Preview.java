package ru.kkey.ui.preview;

import java.util.Set;

import javax.swing.JDialog;

/**
 * Common interface for all preview types
 * New preview should be registered in {@link PreviewRegistry}
 *
 * @author anstarovoyt
 */
public interface Preview
{
    /**
     * @return set acceptable file extensions
     */
    Set<String> getExtensions();

    /**
     * Add to dialog preview of the file
     */
    void render(JDialog dialog, byte[] file);
}
