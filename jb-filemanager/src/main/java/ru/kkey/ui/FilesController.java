package ru.kkey.ui;

import ru.kkey.core.FileItem;
import ru.kkey.core.FileSource;
import ru.kkey.core.FileSourceStub;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class FilesController
{
	private static final String ENTER = "Solve";


	private FileSource fileSource = new FileSourceStub();
	private JTable table;
	private DefaultTableModel model;

	public FilesController(JTable table, DefaultTableModel model)
	{
		this.table = table;
		this.model = model;
		bindEnterKey();
		fillDefaultValues();
	}

	private void fillDefaultValues()
	{
		model.addColumn("Files");
		List<FileItem> files = fileSource.getFiles();
		for (int i = 0; i < files.size(); i++)
		{
			model.insertRow(i, new Object[]{files.get(i)});
		}
	}

	private void bindEnterKey()
	{
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, FilesController.ENTER);
		table.getActionMap().put(FilesController.ENTER, new EnterAction());

	}

	private class EnterAction extends AbstractAction
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			System.out.println(table.getSelectedRow());
		}
	}
}
