package ru.kkey.ui;

/**
 * Swing the main frame
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
		controller.updateFilesInView();

		view.show();
	}


}


