
package nnn.jconsole.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Dialog to create a new graph.
 *
 */
public class GraphCreateDialog extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9057749550538513352L;
	private static GraphCreateDialog dialog;
	private static boolean value;

	/**
	 * Show dialog to create a new graph.
	 * 
	 * @param parent
	 *            Parent's frame.
	 * @param g
	 *            possibly sequences.
	 * @return new graph component or <code>null</code> if user cancelled.
	 */
	public static GraphComponent showDialog(Component parent, String title,Set<GraphSequence> g) {
		Frame frame = JOptionPane.getFrameForComponent(parent);
		dialog = new GraphCreateDialog(frame, title,g);
		dialog.setVisible(true);
		if (value) {
			return new GraphComponent(dialog.titlefield.getText(), dialog.dataModel.getVisibles());
		}
		return null;
	}

	private GraphSequenceTableModel dataModel;
	private JTextField titlefield;

	private GraphCreateDialog(Frame frame,String title, Set<GraphSequence> g) {
		super(frame, Messages.getString("GraphCreateDialog.TITLE"), true); //$NON-NLS-1$

		JButton cancelButton = new JButton(Messages.getString("GraphCreateDialog.CANCEL")); //$NON-NLS-1$
		cancelButton.addActionListener(this);
		//
		final JButton setButton = new JButton(Messages.getString("GraphCreateDialog.CREATE")); //$NON-NLS-1$
		setButton.setActionCommand("Set"); //$NON-NLS-1$
		setButton.addActionListener(this);
		getRootPane().setDefaultButton(setButton);

		final JTable t = new JTable();
		// Add/Remove line if double-click.
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent me) {
				JTable table = (JTable) me.getSource();
				Point p = me.getPoint();
				int row = table.rowAtPoint(p);
				if (me.getClickCount() == 2 && row >= 0) {
					boolean b = !((Boolean) dataModel.getValueAt(row, 0));
					dataModel.setValueAt(b, row, 0);
					t.repaint();
				}
			}
		});
		// Add/Remove line if space typed.
		t.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == ' ') {
					int row = t.getSelectedRow();
					if (row >= 0) {
						boolean b = !((Boolean) dataModel.getValueAt(row, 0));
						dataModel.setValueAt(b, row, 0);
						t.repaint();
					}
				}
			}
		});
		this.dataModel = new GraphSequenceTableModel(g);
		t.setModel(dataModel);
		// set width for first and last columns
		t.getColumn(t.getColumnName(0)).setMaxWidth(64);
		TableColumn c = t.getColumn(t.getColumnName(3));
		c.setMaxWidth(64);
		c.setCellEditor(new ColorE());
		t.setDefaultRenderer(Color.class, new ColorR());
		JScrollPane listScroller = new JScrollPane(t);
		listScroller.setPreferredSize(new Dimension(800, 250));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		// default title
		titlefield = new JTextField(title);
		listPane.add(titlefield);
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(setButton);

		// Put everything together, using the content pane's BorderLayout.
		Container contentPane = getContentPane();
		contentPane.add(listPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);

		// Initialize values.
		if (frame != null) {
			setLocationRelativeTo(frame);
		}
		pack();
	}

	// Handle clicks on the Set and Cancel buttons.
	@Override
	public void actionPerformed(ActionEvent e) {
		if ("Set".equals(e.getActionCommand())) { //$NON-NLS-1$
			value = true;
		} else {
			value = false;
		}
		GraphCreateDialog.dialog.setVisible(false);
	}

	/**
	 * Render for Color Cell.
	 *
	 */
	private static class ColorR implements TableCellRenderer {

		public ColorR() {
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object color, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JComponent res;
			if (isSelected) {
				res = new JButton();
			} else {
				res = new JLabel();
			}
			res.setOpaque(true);
			res.setBackground((Color) color);
			return res;
		}

	}

	/**
	 * Editor for Color Cell.
	 *
	 */
	private static class ColorE extends AbstractCellEditor implements TableCellEditor {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8000638805335209414L;
		/**
		 * Choose value.
		 */
		Color cvalue;

		public ColorE() {
		}

		@Override
		public Object getCellEditorValue() {
			return cvalue;
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, final Object value, boolean isSelected, int row,
				int column) {
			final JButton button = new JButton();
			button.setForeground((Color) value);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					cvalue = JColorChooser.showDialog(button,
							Messages.getString("GraphCreateDialog.COLORCHOOSER_TITLE"), (Color) value); //$NON-NLS-1$
				}
			});
			return button;
		}
	}
}
