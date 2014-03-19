package ru.kkey.ui.preview;

import static javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.KeyStroke;

/**
 * Action for closing the Preview dialog
 */
public class CloseAction extends AbstractAction
{
    private static final long serialVersionUID = 1L;

    public static void bindToEsc(JDialog window)
    {
        KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        window.getRootPane().registerKeyboardAction(new CloseAction(window), key, WHEN_IN_FOCUSED_WINDOW);
    }

    private JDialog dialog;

    public CloseAction(JDialog dialog)
    {
        this.dialog = dialog;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
        dialog.setVisible(false);
    }
}
