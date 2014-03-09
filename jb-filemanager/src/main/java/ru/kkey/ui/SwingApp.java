package ru.kkey.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Swing the main frame
 *
 * @author anstarovoyt
 */
public class SwingApp implements Runnable
{

	JFrame frame;

	@Override
	public void run()
	{
		frame = new JFrame();

		frame.add(createScrollablePanel());

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(700, 500));
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private JComponent createScrollablePanel()
	{
		JComponent test = createTable();
		return new JScrollPane(test);
	}

	private JComponent createTable()
	{
		DefaultTableModel dm = new DefaultTableModel()
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		JTable table = new JTable(dm);
		table.setGridColor(new Color(0, 0, 0));
		table.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
		new FilesController(table, dm, frame);
		return table;
	}
}


