package ru.kkey.ui.menu;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * @author anstarovoyt
 */
public interface MenuAction extends ActionListener
{
	void bind(JFrame frame, SelectMenuResult result);

	String getName();
}
