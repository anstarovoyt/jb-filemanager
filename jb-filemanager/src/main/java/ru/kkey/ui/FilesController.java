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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the application window
 *
 * @author anstarovoyt
 * @see FilesView
 */
public class FilesController
{
	private static final String ENTER = "enter";
	private static final String BACK_STRING = "/...";
	private static final Logger logger = Logger.getAnonymousLogger();

	private volatile Source fileSource = new FSSource("");

	private final ArrayList<Source> prevSources = new ArrayList<>();
	private final FilesView view;

	public FilesController(FilesView view)
	{
		this.view = view;
	}

	public void bind()
	{
		bindDoubleClick();
		bindEnterKey();
		bindMenuActions();
	}

	public void updateFilesInViewAsync()
	{
		new SwingWorker<List<FileItem>, Void>()
		{
			@Override
			protected List<FileItem> doInBackground() throws Exception
			{
				return fileSource.listFiles();
			}

			@Override
			protected void done()
			{
				try
				{
					updateFilesInView(get());
				} catch (Exception e)
				{
					updateFilesInView(new ArrayList<FileItem>());
					view.setStateMessage("Error file list loading: " + e.getMessage());
				}
			}
		}.execute();

	}

	public void updateFilesInView(List<FileItem> fileItems)
	{
		List<Object> items = new ArrayList<>();
		items.add(BACK_STRING);
		items.addAll(fileItems);

		view.setFilesAndUpdateView(items);
		view.resetStateMessage();
	}


	private void addMenuAction(final String code, final String stateLoadingMessage,
							   final Source.SourceFactory sourceFactory)
	{
		view.addActionForMenu(code, new SelectMenuResult()
		{
			@Override
			public void process(final String result)
			{
				view.setStateMessage(stateLoadingMessage + result);

				new SwingWorker<Source, Void>()
				{
					@Override
					protected Source doInBackground() throws Exception
					{
						return sourceFactory.create(result);
					}

					@Override
					protected void done()
					{
						try
						{
							Source newSource = sourceFactory.create(result);
							replaceSourceAndUpdateView(newSource);
							view.resetStateMessage();

						} catch (RuntimeException e)
						{
							logger.log(Level.WARNING, e.getMessage(), e);
							view.setStateMessage("Error open path: " + e.getMessage());
						}
					}
				}.execute();
			}
		});
	}

	private void bindDoubleClick()
	{
		view.addMouseListener(new MouseAdapter()
		{
			@Override
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
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				onEnter();
			}
		});
	}

	private void bindMenuActions()
	{
		addMenuAction(FilesView.MENU_ITEM_LOCATION, "Open location: ", FSSource.FACTORY);
		addMenuAction(FilesView.MENU_ITEM_ZIP, "Open zip file: ", ZipSource.FACTORY);
		addMenuAction(FilesView.MENU_ITEM_FTP, "Connect to ftp server: ", FTPSource.FACTORY);
	}

	private void goBack()
	{
		new SwingWorker<Boolean, Void>()
		{
			@Override
			protected Boolean doInBackground() throws Exception
			{
				return fileSource.goBack();
			}

			@Override
			protected void done()
			{
				try
				{
					if (get())
					{
						updateFilesInViewAsync();
						return;
					}

					if (!prevSources.isEmpty())
					{
						replaceSourceAndUpdateView(prevSources.remove(prevSources.size() - 1));
					}
				} catch (Exception e)
				{
					logger.log(Level.WARNING, e.getMessage(), e);
					view.setStateMessage("Cannot go to parent: " + e.getMessage());
				}
			}
		}.execute();
	}

	private void goInto(final FileItem item)
	{
		new SwingWorker<Void, Void>()
		{
			@Override
			protected Void doInBackground() throws Exception
			{
				fileSource.goInto(item);
				return null;
			}

			@Override
			protected void done()
			{
				try
				{
					get();
					updateFilesInViewAsync();
				} catch (Exception e)
				{
					logger.log(Level.WARNING, e.getMessage(), e);
					view.setStateMessage("Cannot go to the directory: " + e.getMessage());
				}
			}
		}.execute();

	}

	private void onEnter()
	{
		if (view.getSelectedValue() != null)
		{
			view.setStateMessage("Open ...");
			processEnterKey();
		}
	}

	private void processEnterKey()
	{
		if (view.isSelectedBackLink())
		{
			goBack();
			return;
		}

		final FileItem item = view.getSelectedValue();

		if (item.isDirectory())
		{
			goInto(item);
			return;
		}

		if (tryShowPreview(item))
		{
			return;
		}

		tryGetChildSource(item);
	}

	private void tryGetChildSource(final FileItem item)
	{
		new SwingWorker<Source, Void>()
		{
			@Override
			protected Source doInBackground() throws Exception
			{
				return fileSource.getSourceFor(item);
			}

			@Override
			protected void done()
			{
				try
				{
					Source newSource = get();
					if (null != newSource)
					{
						prevSources.add(fileSource);
						fileSource = newSource;
						updateFilesInViewAsync();
					} else
					{
						setNotFoundPreviewMessageFor(item);
					}
				} catch (Exception e)
				{
					logger.log(Level.WARNING, e.getMessage(), e);
				}
			}
		}.execute();
	}

	private void setNotFoundPreviewMessageFor(FileItem item)
	{
		if (!"".equals(item.getFileExtension()))
		{
			view.setStateMessage("Cannot find preview for the file extension '" + item.getFileExtension() + "'");
		} else
		{
			view.setStateMessage("Cannot find preview to the file '" + item.getName() + "'");
		}
	}

	private void replaceSourceAndUpdateView(Source newSource)
	{
		replaceDataSource(newSource);
		updateFilesInViewAsync();
	}

	private void replaceDataSource(Source newSource)
	{
		fileSource.destroy();
		fileSource = newSource;
	}

	private void showPreviewAsync(final FileItem item, Preview preview)
	{
		final Preview previewForProcess = preview;
		view.setStateMessage("Loading preview for file " + item.getName());

		new SwingWorker<Object, Void>()
		{
			@Override
			protected Object doInBackground() throws Exception
			{
				return fileSource.getFile(item);
			}

			@Override
			protected void done()
			{
				try
				{
					byte[] file = (byte[]) get();
					view.showDialog(previewForProcess, file);
					view.resetStateMessage();

				} catch (Exception e)
				{
					logger.log(Level.WARNING, e.getMessage(), e);
					view.setStateMessage("Error open preview: " + e.getMessage());
				}
			}
		}.execute();
	}

	private boolean tryShowPreview(final FileItem item)
	{
		for (Preview preview : PreviewRegistry.get().getPreviews())
		{
			if (preview.getExtensions().contains(item.getFileExtension()))
			{
				showPreviewAsync(item, preview);
				return true;
			}
		}

		return false;
	}
}
