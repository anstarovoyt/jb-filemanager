package ru.kkey;

import ru.kkey.ui.SwingApp;

import javax.swing.*;

/**
 * Entry point
 *
 * @author anstarovoyt
 */
public class App 
{
    public static void main( String[] args )
    {
		SwingUtilities.invokeLater(new SwingApp());
    }
}
