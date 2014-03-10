package ru.kkey.ui;

import ru.kkey.ui.menu.*;
import ru.kkey.ui.preview.DisposeAction;
import ru.kkey.ui.preview.Preview;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author anstarovoyt
 */
public class FilesView
{
	public static final String MENU_ITEM_LOCATION = "location";
	public static final String MENU_ITEM_ZIP = "zip";
	public static final String MENU_ITEM_FTP = "ftp";

	private final Map<String, MenuAction> menuActions = new LinkedHashMap<>();

	{
		menuActions.put(MENU_ITEM_LOCATION, new OpenLocationAction());
		menuActions.put(MENU_ITEM_ZIP, new OpenZipAction());
		menuActions.put(MENU_ITEM_FTP, new OpenFTPAction());
	}

	private final DefaultTableModel tableModel = new DefaultTableModel()
	{
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	};

	private final JFrame mainFrame;
	private final JTable table;

	private final JLabel state;

	public FilesView()
	{
		mainFrame = createMainFrame();

		table = createTable();
		state = new JLabel();
		state.setText(" ");
		state.setLayout(new BorderLayout());
		state.setHorizontalAlignment(SwingConstants.LEFT);
		JMenuBar menuBar = createMenu();
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		mainFrame.setJMenuBar(menuBar);
		mainFrame.add(panel);
		panel.add(packToScrollablePane(table));
		panel.add(state);

		customizeRowStyle();
	}

	private JFrame createMainFrame()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(700, 500));

		return frame;
	}

	private JTable createTable()
	{
		JTable table = new JTable(tableModel);
		table.setGridColor(new Color(0, 0, 0));
		table.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

		return table;
	}

	private JMenuBar createMenu()
	{
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("File");
		bar.add(menu);

		for (Map.Entry<String, MenuAction> entry : menuActions.entrySet())
		{
			JMenuItem menuItem = new JMenuItem(entry.getValue().getName());
			menuItem.addActionListener(entry.getValue());
			menu.add(menuItem);
		}

		return bar;
	}

	public void show()
	{
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	public void addMouseListener(MouseListener adapter)
	{
		table.addMouseListener(adapter);
	}

	public void addKeySelectionListener(KeyStroke key, String code, Action action)
	{
		table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, code);
		table.getActionMap().put(code, action);
	}

	public void setFilesAndUpdateView(java.util.List<?> files)
	{
		tableModel.getDataVector().clear();
		table.clearSelection();
		for (Object item : files)
		{
			tableModel.addRow(new Object[]{item});
		}

		tableModel.fireTableDataChanged();
		table.addRowSelectionInterval(0, 0);
	}

	public boolean isSelectedBackLink()
	{
		return table.getSelectedRow() == 0;
	}

	public void showDialog(Preview preview, InputStream fileStream)
	{
		final JDialog dialog = new JDialog(mainFrame, true);
		dialog.setSize(new Dimension(800, 600));
		dialog.setLocationRelativeTo(null);

		//destroy because it is too hard clean form
		//inner state of the dialog can be vastly changed
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		DisposeAction.bindToEsc(dialog);

		preview.render(dialog, fileStream);
		dialog.setVisible(true);
	}


	public <T> T getSelectedValue()
	{
		return (T) table.getValueAt(table.getSelectedRow(), 0);
	}

	public void addActionForMenu(String item, SelectMenuResult result)
	{
		menuActions.get(item).bind(mainFrame, result);
	}

	public void setState(String newState)
	{
		state.setText(newState);
	}


	private void customizeRowStyle()
	{
		tableModel.addColumn("Files");
		table.setFont(new Font(table.getFont().getFontName(), 0, 15));
		table.setRowHeight(20);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private JComponent packToScrollablePane(JComponent component)
	{
		JScrollPane jScrollPane = new JScrollPane(component);

		jScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

		return jScrollPane;
	}

}
