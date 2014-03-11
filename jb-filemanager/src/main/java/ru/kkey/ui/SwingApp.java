package ru.kkey.ui;

/**
 * Swing interface builder
 *
 * @author anstarovoyt
 */
public class SwingApp implements Runnable
{
    @Override
    public void run()
    {
        FilesView view = new FilesView();

        FilesController controller = new FilesController(view);
        controller.bind();
        controller.updateFilesInView();

        view.show();
    }

}
