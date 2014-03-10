package ru.kkey.ui.menu;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author anstarovoyt
 */
public class OpenLocationAction implements MenuAction
{
	protected volatile JFrame frame;
	protected volatile SelectMenuResult result;

	@Override
	public String getName()
	{
		return "Open location...";
	}

	public OpenLocationAction()
	{

	}

	@Override
	public void bind(JFrame frame, SelectMenuResult result)
	{
		this.frame = frame;
		this.result = result;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		String s = showDialog();

		if (null != s && !s.isEmpty())
		{
			result.forResult(s);
		}
	}

	protected String showDialog()
	{
		return JOptionPane.showInputDialog(
				frame, "Enter new location:", "Location", JOptionPane.PLAIN_MESSAGE);
	}
}
