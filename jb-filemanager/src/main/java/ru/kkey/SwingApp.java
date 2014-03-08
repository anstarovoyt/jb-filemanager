package ru.kkey;

import javax.swing.*;
import java.awt.*;

/**
 *
 */
public class SwingApp implements Runnable {

	@Override
	public void run() {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setSize(new Dimension(800, 600));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}


