package ru.kkey.ui;

import ru.kkey.ui.menu.*;
import ru.kkey.ui.preview.CloseAction;
import ru.kkey.ui.preview.Preview;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * View for the application window
 *
 * @author anstarovoyt
 * @see FilesController
 */
public class FilesView
{
	public static final String MENU_ITEM_LOCATION = "location";
	public static final String MENU_ITEM_ZIP = "zip";
	public static final String MENU_ITEM_FTP = "ftp";

	private static final String HOME_KEY = "home";
	private static final String END_KEY = "end";

	private final Map<String, MenuAction> menuActions = new LinkedHashMap<>();

	{
		menuActions.put(MENU_ITEM_LOCATION, new OpenLocationAction());
		menuActions.put(MENU_ITEM_ZIP, new OpenZipAction());
		menuActions.put(MENU_ITEM_FTP, new OpenFTPAction());
	}

	private final DefaultTableModel tableModel = new DefaultTableModel()
	{
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	};

	private final JFrame mainFrame;
	private final JMenuBar menuBar;
	private final JTable table;
	private final JLabel stateMessage;
	private final JDialog dialog;
	//process only in dispatch thread -> no volatile
	private JPanel contentDialogPanel;

	public FilesView()
	{
		mainFrame = createMainFrame();
		menuBar = createMenu();
		table = createTable();
		stateMessage = createStateField();
		dialog = createDialog();
		//vertical presentation
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		mainFrame.setJMenuBar(menuBar);
		mainFrame.add(panel);

		panel.add(packToScrollablePane(table));
		panel.add(stateMessage);

		customizeRowStyle();

		bindHomeKey();
		bindEndKey();
	}

	public void addActionForMenu(String item, SelectMenuResult result)
	{
		menuActions.get(item).bind(mainFrame, result);
	}

	public void addKeySelectionListener(KeyStroke key, String code, Action action)
	{
		table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(key, code);
		table.getActionMap().put(code, action);
	}

	public void addMouseListener(MouseListener adapter)
	{
		table.addMouseListener(adapter);
	}

	@SuppressWarnings("unchecked")
	public <T> T getSelectedValue()
	{
		return -1 == table.getSelectedRow() ? null : (T) table.getValueAt(table.getSelectedRow(), 0);
	}

	public int getValueIndex(List<?> data, Object item)
	{
		if (null == item)
		{
			return 0;
		}
		int valueIndex = data.indexOf(item);
		if (valueIndex < 0)
		{
			return 0;
		}

		return valueIndex;
	}

	public boolean isSelectedBackLink()
	{
		return table.getSelectedRow() == 0;
	}

	public void resetStateMessage()
	{
		stateMessage.setText(" ");
	}

	public void setFilesAndUpdateView(java.util.List<?> files, Object selectedItem)
	{
		tableModel.getDataVector().clear();
		table.clearSelection();
		for (Object item : files)
		{
			tableModel.addRow(new Object[]{item});
		}

		tableModel.fireTableDataChanged();
		table.requestFocus();

		int selectionIndex = getValueIndex(files, selectedItem);
		table.changeSelection(selectionIndex, 0, false, false);
	}

	public void setStateMessage(String newState)
	{
		stateMessage.setText(newState);
	}

	public void show()
	{
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}

	public void showDialog(Preview preview, byte[] file)
	{
		try
		{
			contentDialogPanel = new JPanel();
			contentDialogPanel.setLayout(new BorderLayout());
			dialog.add(contentDialogPanel);
			dialog.setSize(new Dimension(800, 600));
			dialog.setLocationRelativeTo(null);
			preview.render(contentDialogPanel, file);
			dialog.revalidate();
			dialog.repaint();
			dialog.setVisible(true);

		} catch (RuntimeException e)
		{
			dialog.dispose();
			cleanDialog();
			throw e;
		}
	}

	private void bindEndKey()
	{
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0);
		addKeySelectionListener(enter, END_KEY, new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				int rowNum = table.getRowCount() - 1;
				if (rowNum < 0)
				{
					return;
				}
				setFocusToRow(rowNum);
			}
		});
	}

	private void bindHomeKey()
	{
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0);
		addKeySelectionListener(enter, HOME_KEY, new AbstractAction()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setFocusToRow(0);
			}
		});
	}

	private JDialog createDialog()
	{
		JDialog dialog = new JDialog(mainFrame, true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		dialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				cleanDialog();
			}
		});
		dialog.setSize(new Dimension(800, 600));
		CloseAction.bindToEsc(dialog);

		return dialog;
	}

	private void cleanDialog()
	{
		if (null != contentDialogPanel)
		{
			dialog.remove(contentDialogPanel);
			contentDialogPanel = null;
			dialog.revalidate();
		}
	}

	private JFrame createMainFrame()
	{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setSize(new Dimension(700, 500));
		frame.setTitle("Simple file manager");
		return frame;
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

	private JLabel createStateField()
	{
		JLabel state = new JLabel();
		state.setText(" ");
		state.setLayout(new BorderLayout());
		state.setHorizontalAlignment(SwingConstants.LEFT);

		return state;
	}

	private JTable createTable()
	{
		JTable table = new JTable(tableModel);
		table.setGridColor(new Color(0, 0, 0));
		table.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));

		return table;
	}

	private void customizeRowStyle()
	{
		tableModel.addColumn("File list");
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

	private void setFocusToRow(int rowNum)
	{
		table.requestFocus();
		table.changeSelection(rowNum, 0, false, false);
	}
}
