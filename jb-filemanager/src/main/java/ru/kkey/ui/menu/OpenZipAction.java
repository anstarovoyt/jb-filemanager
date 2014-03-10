package ru.kkey.ui.menu;

import javax.swing.*;

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

	public OpenZipAction()
	{
		super();
	}

	protected String showDialog()
	{
		return JOptionPane.showInputDialog(
				frame, "Enter path to a zip file:", "Location", JOptionPane.PLAIN_MESSAGE);
	}
}
