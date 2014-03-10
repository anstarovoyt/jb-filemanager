package ru.kkey.ui;

import ru.kkey.core.FSSource;
import ru.kkey.core.FileItem;
import ru.kkey.core.Source;
import ru.kkey.ui.preview.Preview;
import ru.kkey.ui.preview.PreviewRegistry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.util.ArrayList;
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

	private final FilesView view;

	public FilesController(FilesView view)
	{
		this.view = view;
		bind();
	}

	public void updateFilesInView()
	{
		List<Object> items = new ArrayList<>();
		items.add(BACK_STRING);
		items.addAll(fileSource.listFiles());

		view.setFilesAndUpdateView(items);
	}

	private void bind()
	{
		bindDoubleClick();
		bindEnterKey();
	}

	private void bindDoubleClick()
	{
		view.addMouseListener(new MouseAdapter()
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

	private void bindEnterKey()
	{
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
		view.addKeySelectionListener(enter, ENTER, new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				onEnter();
			}
		});
	}

	private void onEnter()
	{

		if (view.isSelectedBackLink())
		{
			if (fileSource.goBack())
			{
				updateFilesInView();
			} else
			{
				if (!stack.isEmpty())
				{
					fileSource = stack.remove(stack.size() - 1);
					updateFilesInView();
				}
			}

			return;
		}

		FileItem item = view.getSelectedValue();

		if (item.isFolder())
		{
			fileSource.goInto(item);
			updateFilesInView();
		} else
		{
			if (!tryShowPreview(item))
			{
				String fileExtension = getFileExtension(item.getName());

				if ("zip".equals(fileExtension) && fileSource instanceof FSSource)
				{
					stack.add(fileSource);
					fileSource = ((FSSource) fileSource).createZipSource(item);
					updateFilesInView();
				}
			}
		}
	}

	private boolean tryShowPreview(FileItem item)
	{
		String fileExtension = getFileExtension(item.getName());

		for (Preview preview : PreviewRegistry.get().getPreviews())
		{
			if (preview.getExtensions().contains(fileExtension))
			{
				InputStream fileStream = fileSource.getFileStream(item);
				view.showDialog(preview, fileStream);
				return true;
			}
		}

		return false;
	}

	private String getFileExtension(String name)
	{
		return name.lastIndexOf('.') >= 0 ? name.substring(name.lastIndexOf('.') + 1).toLowerCase() : "";
	}
}
