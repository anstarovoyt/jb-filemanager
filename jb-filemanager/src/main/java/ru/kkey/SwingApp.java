package ru.kkey;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Swing main frame
 *
 * @author anstarovoyt
 */
public class SwingApp implements Runnable {

	@Override
	public void run() {
		JFrame frame = new JFrame();

		frame.add(createScrollablePanel());

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(800, 600));
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
		DefaultTableModel dm = new DefaultTableModel();
		dm.addColumn("Files");
		for (int i=0; i<30;i++)
		{
			dm.insertRow(i, new Object[] {"Row " + i} );
		}
		JTable table = new JTable(dm);
		table.setGridColor(new Color(0, 0, 0));
		table.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
		table.removeEditor();
		return table;
	}
}


