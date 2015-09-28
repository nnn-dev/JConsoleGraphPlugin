package nnn.jconsole.graph;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.sun.tools.jconsole.JConsoleContext;
import com.sun.tools.jconsole.JConsoleContext.ConnectionState;

import sun.tools.jconsole.TimeComboBox;

/**
 * Main panel of the plugin.
 *
 */
public class JConsoleGraphPluginPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5247442298341991824L;
	private JConsoleContext context;
	private Set<GraphComponent> graphs;

	/**
	 * Construct the panel.
	 */
	public JConsoleGraphPluginPanel() {
		setLayout(new BorderLayout());
		JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
		JButton adder = new JButton(Messages.getString("JConsoleGraphPluginPanel.ADD_CHART")); //$NON-NLS-1$
		JButton remover = new JButton(Messages.getString("JConsoleGraphPluginPanel.REMOVE_CHART")); //$NON-NLS-1$
		final JPanel graphspanel = new JPanel();
		graphspanel.setLayout(new BoxLayout(graphspanel, BoxLayout.PAGE_AXIS));
		final TimeComboBox timeComboBox = new TimeComboBox();
		top.add(new JLabel(String.format("%s : ", Messages.getString("JConsoleGraphPluginPanel.TIME_RANGE")))); //$NON-NLS-1$ ,$NON-NLS-2$
		top.add(timeComboBox);
		top.add(adder);
		top.add(remover);
		add(top, BorderLayout.NORTH);
		add(graphspanel, BorderLayout.CENTER);
		graphs = new HashSet<GraphComponent>();
		// how to add graph.
		adder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (context.getConnectionState() != ConnectionState.CONNECTED) {
					showError(Messages.getString("JConsoleGraphPluginPanel.NOT_CONNECTED")); //$NON-NLS-1$
				} else {
					try {
						GraphComponent g = GraphCreateDialog.showDialog(JConsoleGraphPluginPanel.this,
								String.format("%s %d", Messages.getString("GraphCreateDialog.DEFAULT_CHART"), //$NON-NLS-1$ //$NON-NLS-2$
										graphs.size() + 1),
								Utils.getPotentialsSequences(context.getMBeanServerConnection()));
						if (g != null) {
							graphs.add(g);
							g.linkToTimeComboBox(timeComboBox);
							graphspanel.add(g);
							graphspanel.repaint();
						}
					} catch (JConsoleGraphPluginException e2) {
						showError(e2);
					}
				}
			}

		});
		// how to remove graph.
		remover.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				GraphComponent g = GraphsList.showDialog(JConsoleGraphPluginPanel.this, graphs);
				if (g != null) {
					graphs.remove(g);
					g.setVisible(false);
					graphspanel.repaint();
				}
			}

		});
	}

	/**
	 * Show a error dialog.
	 * 
	 * @param message
	 *            message.
	 */
	protected void showError(String message) {
		JOptionPane.showMessageDialog(this, message, Messages.getString("JConsoleGraphPluginPanel.ERROR_TITLE"), //$NON-NLS-1$
				JOptionPane.WARNING_MESSAGE);

	}

	/**
	 * Show exception error.
	 * 
	 * @param e2
	 *            exception.
	 */
	private void showError(Exception e2) {
		StringBuilder sb = new StringBuilder();
		appendExceptionError(sb, e2);
		showError(sb.toString());
	}

	private void appendExceptionError(StringBuilder sb, Throwable e2) {
		sb.append(e2.getLocalizedMessage());
		StackTraceElement[] s = e2.getStackTrace();
		if (s != null && s.length > 0) {
			sb.append('(');
			sb.append(s[0].getClassName());
			sb.append('.');
			sb.append(s[0].getMethodName());
			sb.append(':');
			sb.append(s[0].getLineNumber());
			sb.append(')');
		}
		if (e2.getCause() != null) {
			sb.append(String.format("%n")); //$NON-NLS-1$
			appendExceptionError(sb, e2.getCause());
		}
	}

	public void updateGraphs(JConsoleContext context) {
		this.context = context;
		for (GraphComponent gc : graphs) {
			if (context.getConnectionState() == ConnectionState.CONNECTED) {
				gc.updateGraph(context.getMBeanServerConnection());
			}
		}

	}

}
