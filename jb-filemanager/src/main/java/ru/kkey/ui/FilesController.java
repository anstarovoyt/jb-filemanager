package ru.kkey.ui;

import ru.kkey.core.FSSource;
import ru.kkey.core.FileItem;
import ru.kkey.core.Source;
import ru.kkey.core.ZipSource;
import ru.kkey.ui.preview.Preview;
import ru.kkey.ui.preview.PreviewRegistry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author anstarovoyt
 */
public class FilesController
{
	private static final String ENTER = "enter";
	private static final String BACK_STRING = "/...";

	private volatile Source fileSource = new FSSource("");

	private CopyOnWriteArrayList<Source> stack = new CopyOnWriteArrayList<>();


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
		table.setRowHeight(20);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
			if (fileSource.goBack())
			{
				updateTable();
			} else
			{
				if (!stack.isEmpty())
				{
					fileSource = stack.remove(stack.size() - 1);
					updateTable();
				}
			}

			return;
		}

		FileItem item = fileSource.listFiles().get(table.getSelectedRow() - 1);

		if (item.isFolder())
		{
			fileSource.goInto(item);
			updateTable();
		} else
		{
			if (!tryShowPreview(item))
			{
				String fileExtension = getFileExtension(item.getName());

				if ("zip".equals(fileExtension))
				{
					stack.add(fileSource);
					fileSource = new ZipSource(item.getPath().toString());
					updateTable();
				}
			}
		}
	}

	boolean tryShowPreview(FileItem item)
	{
		InputStream fileStream = fileSource.getFileStream(item);

		String fileExtension = getFileExtension(item.getName());

		for (Preview preview : PreviewRegistry.get().getPreviews())
		{
			if (preview.getExtensions().contains(fileExtension))
			{
				JDialog dialog = new JDialog(mainFrame, true);
				dialog.setSize(new Dimension(800, 600));
				dialog.setLocationRelativeTo(null);

				//destroy because it is too hard clean form
				//inner state of the dialog can be vastly changed
				dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				preview.render(dialog, fileStream);
				dialog.setVisible(true);
				return true;
			}
		}

		return false;
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
		List<FileItem> files = fileSource.listFiles();

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

	private String getFileExtension(String name)
	{
		return name.lastIndexOf('.') >= 0 ? name.substring(name.lastIndexOf('.') + 1) : "";
	}
}
