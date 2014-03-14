package ru.kkey.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import ru.kkey.core.FSSource;
import ru.kkey.core.FTPSource;
import ru.kkey.core.FileItem;
import ru.kkey.core.Source;
import ru.kkey.core.Utils.Pair;
import ru.kkey.core.ZipSource;
import ru.kkey.ui.menu.SelectMenuResult;
import ru.kkey.ui.preview.Preview;
import ru.kkey.ui.preview.PreviewRegistry;

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

    private final ArrayList<Pair<Source, FileItem>> prevSources = new ArrayList<>();
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

    public void updateFilesInView(List<FileItem> fileItems, FileItem selectedItem)
    {
        List<Object> items = new ArrayList<>();
        items.add(BACK_STRING);
        items.addAll(fileItems);

        view.setFilesAndUpdateView(items, selectedItem);
        view.resetStateMessage();
    }

    public void updateFilesInViewAsync()
    {
        updateFilesInViewAsync(null);

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
                            Source newSource = get();
                            replaceSourceAndUpdateView(newSource, null);
                            view.resetStateMessage();

                        }
                        catch (Exception e)
                        {
                            setError("Error open path: ", e);
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

    private Throwable getCause(Exception e)
    {
        Throwable cause = e;
        if (e.getCause() != null)
        {
            cause = e.getCause();
        }
        return cause;
    }

    private void goBack()
    {
        new SwingWorker<FileItem, Void>()
        {
            @Override
            protected FileItem doInBackground() throws Exception
            {
                return fileSource.goBack();
            }

            @Override
            protected void done()
            {
                try
                {
                    FileItem fileItem = get();
                    if (null != fileItem)
                    {
                        updateFilesInViewAsync(fileItem);
                        return;
                    }

                    if (!prevSources.isEmpty())
                    {
                        Pair<Source, FileItem> prevSource = prevSources.remove(prevSources.size() - 1);
                        replaceSourceAndUpdateView(prevSource.key, prevSource.value);
                    }

                    view.resetStateMessage();
                }
                catch (Exception e)
                {
                    setError("Cannot go to parent: ", e);
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
                    view.setStateMessage("Load files ...");
                    updateFilesInViewAsync();
                }
                catch (Exception e)
                {
                    setError("Cannot go to the directory: ", e);
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

    private void replaceDataSource(Source newSource)
    {
        fileSource.destroy();
        fileSource = newSource;
    }

    private void replaceSourceAndUpdateView(Source newSource, FileItem selectedItem)
    {
        replaceDataSource(newSource);
        updateFilesInViewAsync(selectedItem);
    }

    private void setError(String message, Exception e)
    {
        Throwable cause = getCause(e);
        logger.log(Level.WARNING, cause.getMessage(), cause);
        view.setStateMessage(message + cause.getMessage());
    }

    private void setNotFoundPreviewMessageFor(FileItem item)
    {
        if (!"".equals(item.getFileExtension()))
        {
            view.setStateMessage("Cannot find preview for the file extension '" + item.getFileExtension() + "'");
        }
        else
        {
            view.setStateMessage("Cannot find preview to the file '" + item.getName() + "'");
        }
    }

    private void showPreviewAsync(final FileItem item, Preview preview)
    {
        final Preview previewForProcess = preview;
        view.setStateMessage("Loading preview for file " + item.getName());

        new SwingWorker<byte[], Void>()
        {
            @Override
            protected byte[] doInBackground() throws Exception
            {
                return fileSource.getFile(item);
            }

            @Override
            protected void done()
            {
                try
                {
                    byte[] file = get();
                    view.showDialog(previewForProcess, file);
                    view.resetStateMessage();
                }
                catch (ExecutionException e)
                {
                    setError("Cannot load file: ", e);
                }
                catch (Exception e)
                {
                    setError("Error open preview: ", e);
                }
            }
        }.execute();
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
                        prevSources.add(new Pair<>(fileSource, item));
                        fileSource = newSource;
                        updateFilesInViewAsync();
                    }
                    else
                    {
                        setNotFoundPreviewMessageFor(item);
                    }
                }
                catch (Exception e)
                {
                    logger.log(Level.WARNING, e.getMessage(), e);
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

    private void updateFilesInViewAsync(final FileItem selectedValue)
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
                    updateFilesInView(get(), selectedValue);
                }
                catch (Exception e)
                {
                    updateFilesInView(new ArrayList<FileItem>(), selectedValue);
                    setError("Error file list loading: ", e);
                }
            }
        }.execute();
    }
}
