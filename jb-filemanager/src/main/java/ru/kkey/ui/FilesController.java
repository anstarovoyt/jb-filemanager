package ru.kkey.ui;

import ru.kkey.core.*;
import ru.kkey.ui.menu.SelectMenuResult;
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

	private final CopyOnWriteArrayList<Source> stack = new CopyOnWriteArrayList<>();
	private final FilesView view;

	public FilesController(FilesView view)
	{
		this.view = view;
	}

	public void updateFilesInView()
	{
		List<Object> items = new ArrayList<>();
		items.add(BACK_STRING);
		items.addAll(fileSource.listFiles());

		view.setFilesAndUpdateView(items);
	}

	public void bind()
	{
		bindDoubleClick();
		bindEnterKey();
		bindMenuActions();
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

	private void bindMenuActions()
	{
		view.addActionForMenu(FilesView.MENU_ITEM_LOCATION, new SelectMenuResult()
		{
			@Override
			public void forResult(String result)
			{
				try
				{
					Source newSource = new FSSource(result);
					replaceFileSourceAndUpdateView(newSource);

				} catch (RuntimeException e)
				{
					System.err.println(e);
				}
			}
		});
		view.addActionForMenu(FilesView.MENU_ITEM_ZIP, new SelectMenuResult()
		{
			@Override
			public void forResult(final String result)
			{
				view.setState("Open zip file ...");

				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							Source newSource = new ZipSource(result);
							replaceFileSourceAndUpdateView(newSource);
							view.setState("");
						} catch (RuntimeException e)
						{
							view.setState("Error open zip: " + e.getMessage());
						}
					}
				});
			}
		});
		view.addActionForMenu(FilesView.MENU_ITEM_FTP, new SelectMenuResult()
		{
			@Override
			public void forResult(final String result)
			{
				view.setState("Connect to ftp server: " + result);

				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							Source newSource = FTPSource.create(result);
							replaceFileSourceAndUpdateView(newSource);
							view.setState("Connected to " + result);

						} catch (RuntimeException e)
						{
							view.setState("Error open ftp: " + e.getMessage());
						}
					}

				});
			}
		});
	}

	private void replaceFileSourceAndUpdateView(Source newSource)
	{
		fileSource.destroy();
		fileSource = newSource;
		updateFilesInView();
		view.setState("");
	}

	private synchronized void onEnter()
	{

		if (view.isSelectedBackLink())
		{
			tryGoBack();

			return;
		}

		FileItem item = view.getSelectedValue();

		if (item.isFolder())
		{
			fileSource.goInto(item);
			updateFilesInView();
			return;
		}

		if (!tryShowPreview(item))
		{
			//try to get child file source
			Source newSource = fileSource.getSourceFor(item);
			if (null != newSource)
			{
				stack.add(fileSource);
				fileSource = newSource;
				updateFilesInView();
			}
		}
	}

	private void tryGoBack()
	{
		if (fileSource.goBack())
		{
			updateFilesInView();
		} else
		{
			if (!stack.isEmpty())
			{
				replaceFileSourceAndUpdateView(stack.remove(stack.size() - 1));
			}
		}
	}

	private boolean tryShowPreview(FileItem item)
	{
		String fileExtension = item.getFileExtension();

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
}
