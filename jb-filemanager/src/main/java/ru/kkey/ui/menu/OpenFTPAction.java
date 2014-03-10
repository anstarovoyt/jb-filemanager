package ru.kkey.ui.menu;

import javax.swing.*;

/**
 * @author anstarovoyt
 */
public class OpenFTPAction extends OpenLocationAction
{
	@Override
	public String getName()
	{
		return "Connect to FTP...";
	}


	protected String showDialog()
	{
		return JOptionPane.showInputDialog(
				frame, "Location...", "Location", JOptionPane.PLAIN_MESSAGE);
	}
}
