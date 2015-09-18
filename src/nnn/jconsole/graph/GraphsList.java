
package nnn.jconsole.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Allow to select a graph.
 *
 */
public class GraphsList extends JDialog implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9057749550538513352L;
	private static GraphsList dialog;
	private static GraphComponent value;

	/**
	 * Show the list of graphs.
	 * @param parent frame.
	 * @param g list of graph.
	 * @return graph selected or <code>null</code>.
	 */
	public static GraphComponent showDialog(Component parent, Set<GraphComponent> g) {
		Frame frame = JOptionPane.getFrameForComponent(parent);
		dialog = new GraphsList(frame, g);
		dialog.setVisible(true);
		return value;
	}

	private GraphsList(Frame frame, Set<GraphComponent> g) {
		super(frame, Messages.getString("GraphsList.TITLE"), true); //$NON-NLS-1$

		JButton cancelButton = new JButton(Messages.getString("GraphsList.CANCEL")); //$NON-NLS-1$
		cancelButton.addActionListener(this);
		//
		final JButton setButton = new JButton(Messages.getString("GraphsList.SELECT")); //$NON-NLS-1$
		setButton.setActionCommand("Set"); //$NON-NLS-1$
		setButton.addActionListener(this);
		getRootPane().setDefaultButton(setButton);

		JList<GraphComponent> t = new JList<GraphComponent>();
		DefaultListModel<GraphComponent> ml = new DefaultListModel<GraphComponent>();
		for (GraphComponent gc : g) {
			ml.addElement(gc);
		}
		t.setModel(ml);
		t.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				value = t.getSelectedValue();
			}
		});
		JScrollPane listScroller = new JScrollPane(t);
		listScroller.setPreferredSize(new Dimension(800, 250));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel(Messages.getString("GraphsList.SELECT")); //$NON-NLS-1$
		label.setLabelFor(t);
		listPane.add(label);
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

		Container contentPane = getContentPane();
		contentPane.add(listPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);

		if (frame != null) {
			setLocationRelativeTo(frame);
		}
		pack();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!"Set".equals(e.getActionCommand())) { //$NON-NLS-1$
			value = null;
		}
		GraphsList.dialog.setVisible(false);
	}
}
