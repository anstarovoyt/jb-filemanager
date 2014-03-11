package ru.kkey.ui.preview;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

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
        Image img = Toolkit.getDefaultToolkit().createImage(file);

        JLabel picLabel = new JLabel(new ImageIcon(img));
        JScrollPane scrollPane = new JScrollPane(picLabel);
        dialog.add(scrollPane);

    }
}
