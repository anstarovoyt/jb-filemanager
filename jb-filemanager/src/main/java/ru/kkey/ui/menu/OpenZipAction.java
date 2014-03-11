package ru.kkey.ui.menu;

import javax.swing.JOptionPane;

/**
 * @author anstarovoyt
 */
public class OpenZipAction extends OpenLocationAction
{
    @Override
    public String getName()
    {
        return "Open Zip...";
    }

    @Override
    protected String showDialog()
    {
        return JOptionPane.showInputDialog(frame, "Enter path to a zip file:", "Location", JOptionPane.PLAIN_MESSAGE);
    }
}
