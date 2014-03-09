package ru.kkey.ui;

import ru.kkey.core.FSSource;
import ru.kkey.core.FileItem;
import ru.kkey.core.FileSource;
import ru.kkey.ui.preview.Preview;
import ru.kkey.ui.preview.TextPreview;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * @author anstarovoyt
 */
public class FilesController
{
	private static final String ENTER = "enter";
	private static final String BACK_STRING = "/...";

	public static final List<Preview> previews = Arrays.<Preview>asList(new TextPreview());

	private volatile FileSource fileSource = new FSSource("");
	private final JTable table;
	private final DefaultTableModel model;
	private final JFrame mainFrame;

	public FilesController(JTable table, DefaultTableModel model, JFrame mainFrame)
	{
		this.table = table;
		this.model = model;
		this.mainFrame = mainFrame;
		bindEnterKey();
		bindDoubleClick();
		setRowStyle();
		fillDefaultValues();
	}

	private void bindDoubleClick()
	{
		table.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					onEnter();
				}
			}
		});
	}

	private void setRowStyle()
	{
		model.addColumn("Files");
		table.setFont(new Font(table.getFont().getFontName(), 0, 15));
		table.setRowHeight(30);
	}

	private void fillDefaultValues()
	{
		updateTable();
	}

	private void bindEnterKey()
	{
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, FilesController.ENTER);
		table.getActionMap().put(FilesController.ENTER, new EnterAction());

	}

	private void onEnter()
	{
		if (table.getSelectedRow() < 0)
		{
			return;
		}

		if (table.getSelectedRow() == 0)
		{
			fileSource.goBack();
			updateTable();
			return;
		}

		FileItem item = fileSource.getFiles().get(table.getSelectedRow() - 1);

		if (item.isFolder())
		{
			fileSource.goInto(item);
			updateTable();
		} else
		{
			tryShowPreview(item);
		}
	}

	void tryShowPreview(FileItem item)
	{
		InputStream fileStream = fileSource.getFileStream(item);

		String fileExtention = getFileExtention(item.getName());

		for (Preview preview : previews)
		{
			if (preview.getExtentions().contains(fileExtention))
			{
				JDialog dialog = new JDialog(mainFrame, true);
				dialog.setSize(new Dimension(800, 600));
				dialog.setLocationRelativeTo(null);
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				preview.render(dialog, fileStream);
				dialog.setVisible(true);
			}
		}
	}

	private void updateTable()
	{
		table.clearSelection();
		updateFilesFromSource();
		model.fireTableDataChanged();
		table.addRowSelectionInterval(0, 0);
	}

	private void updateFilesFromSource()
	{
		model.getDataVector().clear();
		List<FileItem> files = fileSource.getFiles();

		model.insertRow(0, new Object[]{BACK_STRING});

		for (FileItem item : files)
		{
			model.addRow(new Object[]{item});
		}
	}

	private class EnterAction extends AbstractAction
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			onEnter();
		}
	}

	private String getFileExtention(String name)
	{
		return name.lastIndexOf('.') >= 0 ? name.substring(name.lastIndexOf('.') + 1) : "";
	}
}
