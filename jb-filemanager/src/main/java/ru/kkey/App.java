package ru.kkey;

import ru.kkey.ui.SwingApp;

import javax.swing.*;

/**
 * Entry point
 *
 * @author anstarovoyt
 */
public class App
{
	private static String OS = System.getProperty("os.name").toLowerCase();

	public static void main(String[] args)
	{
		try
		{
			if (isMac())
			{
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "anstarovoyt");
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			SwingUtilities.invokeLater(new SwingApp());
		} catch (Exception e)
		{
			System.out.println("ClassNotFoundException: " + e.getMessage());
		}

	}


	private static boolean isMac()
	{
		return OS.contains("mac");
	}
}
