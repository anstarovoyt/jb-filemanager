package ru.kkey.ui.preview;

import javax.swing.*;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Text file preview
 *
 * For all files is used default encoding UTF-8
 *
 * @author anstarovoyt
 */
public class TextPreview implements Preview
{
    public static final Set<String> extensions = new HashSet<>(Arrays.asList("txt", "log", "java"));

    @Override
    public Set<String> getExtensions()
    {
        return extensions;
    }

    @Override
    public void render(JPanel dialog, byte[] file)
    {
        JTextArea textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setText(new String(file, Charset.forName("UTF-8")));
        textArea.setCaretPosition(0);
        dialog.add(scrollPane);
    }
}
