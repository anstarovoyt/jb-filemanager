package ru.kkey.ui.menu;

import java.awt.event.ActionListener;

import javax.swing.JFrame;

/**
 * Common interface for select menu action
 *
 * @author anstarovoyt
 * @see SelectMenuResult
 */
public interface MenuAction extends ActionListener
{
    void bind(JFrame frame, SelectMenuResult result);

    String getName();
}
