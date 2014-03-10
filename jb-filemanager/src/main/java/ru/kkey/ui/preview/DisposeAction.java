package ru.kkey.ui.preview;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

/**
 * Action for closing the Preview dialog
 */
public class DisposeAction extends AbstractAction
{
	public static void bindToEsc(JDialog window)
	{
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		window.getRootPane().registerKeyboardAction(new DisposeAction(window), key, WHEN_IN_FOCUSED_WINDOW);
	}

	private final Window dialog;

	public DisposeAction(Window dialog)
	{
		this.dialog = dialog;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		dialog.dispose();
	}
}
