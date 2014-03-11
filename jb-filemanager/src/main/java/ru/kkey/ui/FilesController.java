package ru.kkey.ui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import ru.kkey.core.FSSource;
import ru.kkey.core.FTPSource;
import ru.kkey.core.FileItem;
import ru.kkey.core.Source;
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

    private final CopyOnWriteArrayList<Source> prevSources = new CopyOnWriteArrayList<>();
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

    public void updateFilesInView()
    {
        List<Object> items = new ArrayList<>();
        items.add(BACK_STRING);
        items.addAll(fileSource.listFiles());

        view.setFilesAndUpdateView(items);
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
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            Source newSource = sourceFactory.create(result);
                            replaceFileSourceAndUpdateView(newSource);
                            view.resetStateMessage();
                        }
                        catch (RuntimeException e)
                        {
                            logger.log(Level.WARNING, e.getMessage(), e);
                            view.setStateMessage("Error open path: " + e.getMessage());
                        }
                    }
                });
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
                    onEnterAsync();
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
                onEnterAsync();
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
        if (!fileSource.goBack())
        {
            if (!prevSources.isEmpty())
            {
                replaceFileSourceAndUpdateView(prevSources.remove(prevSources.size() - 1));
                return;
            }
        }

        updateFilesInView();
    }

    private void goInto(FileItem item)
    {
        fileSource.goInto(item);
        try
        {
            updateFilesInView();
        }
        catch (RuntimeException e)
        {
            logger.log(Level.WARNING, e.getMessage(), e);

            List<Object> items = new ArrayList<>();
            items.add(BACK_STRING);
            view.setFilesAndUpdateView(items);

            throw e;
        }
    }

    private void onEnterAsync()
    {
        if (view.getSelectedValue() != null)
        {
            view.setStateMessage("Open ...");
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        processEnterKey();
                    }
                    catch (RuntimeException e)
                    {
                        logger.log(Level.WARNING, e.getMessage(), e);
                        view.setStateMessage("Cannot process action: " + e.getMessage());
                    }
                }
            });
        }
    }

    private synchronized void processEnterKey()
    {
        if (view.isSelectedBackLink())
        {
            goBack();
            view.resetStateMessage();
            return;
        }

        FileItem item = view.getSelectedValue();

        if (item.isDirectory())
        {
            goInto(item);
            view.resetStateMessage();
            return;
        }

        if (tryShowPreview(item))
        {
            return;
        }

        //try to get child file source
        Source newSource = fileSource.getSourceFor(item);
        if (null != newSource)
        {
            prevSources.add(fileSource);
            fileSource = newSource;
            updateFilesInView();
            view.resetStateMessage();
        }
        else
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

    }

    private void replaceFileSourceAndUpdateView(Source newSource)
    {
        fileSource.destroy();
        fileSource = newSource;
        updateFilesInView();
        view.resetStateMessage();
    }

    private void showPreview(final FileItem item, Preview preview)
    {
        final Preview previewForProcess = preview;
        view.setStateMessage("Loading preview for file " + item.getName());
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    byte[] file = fileSource.getFile(item);
                    view.showDialog(previewForProcess, file);
                    view.resetStateMessage();
                }
                catch (RuntimeException e)
                {
                    logger.log(Level.WARNING, e.getMessage(), e);
                    view.setStateMessage("Error open preview: " + e.getMessage());
                }
            }
        });
    }

    private boolean tryShowPreview(final FileItem item)
    {
        for (Preview preview : PreviewRegistry.get().getPreviews())
        {
            if (preview.getExtensions().contains(item.getFileExtension()))
            {
                showPreview(item, preview);
                return true;
            }
        }

        return false;
    }
}
