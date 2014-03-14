package ru.kkey.ui.preview;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JPanel;

import ru.kkey.ui.widget.AutoResizeImagePanel;

/**
 * Image preview
 *
 * @author anstarovoyt
 */
public class ImagePreview implements Preview
{
    public static final Set<String> extensions = new HashSet<>(Arrays.asList("jpg", "png", "gif", "tif", "jpeg"));

    @Override
    public Set<String> getExtensions()
    {
        return extensions;
    }

    @Override
    public void render(JDialog dialog, byte[] file)
    {
        try
        {
            InputStream in = new ByteArrayInputStream(file);
            BufferedImage imageRaw = ImageIO.read(in);

            JPanel scrollPane = new AutoResizeImagePanel(imageRaw);
            dialog.add(scrollPane);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }

    }
}
