package nnn.jconsole.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Collection;
import java.util.Hashtable;

import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.swing.JLabel;
import javax.swing.JPanel;

import sun.tools.jconsole.Plotter;
import sun.tools.jconsole.PlotterPanel;
import sun.tools.jconsole.TimeComboBox;

/**
 * Graphical component for displaying a graph.
 *
 */
public class GraphComponent extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3950600890573459383L;
	/**
	 * Internal display.
	 */
	private PlotterPanel p;
	/**
	 * List of sequences (for update values).
	 */
	private GraphSequence[] l;
	/**
	 * To show message error.
	 */
	private JLabel message;
	/*
	 * Graph's title.
	 */
	private String title;

	/* (non-Javadoc)
	 * @see java.awt.Component#toString()
	 */
	@Override
	public String toString() {
		return title;
	}

	/**
	 * Create component.
	 * @param title graph title.
	 * @param timeComboBox link to the global timeComboBox.
	 * @param collection sequences.
	 */
	public GraphComponent(String title, Collection<GraphSequence> collection) {
		this.title = title;
		setLayout(new BorderLayout());
		p = new PlotterPanel(title, Plotter.Unit.NONE, false);
		p.setVisible(true);
		l=new GraphSequence[collection.size()];
		l=collection.toArray(l);
		for(int i=0;i<l.length;i++){
			final GraphSequence gs=l[i];
			final ObjectName o=gs.getObjectName();
			Hashtable<String, String> properties=o.getKeyPropertyList();
			String name=gs.getAttribute();
			//look if objetname as a name
			for(String key:new String[]{"name","id","path","token"}){ //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				if (properties.containsKey(key)) {
					name=String.format("%s.%s",properties.get(key),name); //$NON-NLS-1$
					break;
				}
			}
			p.getPlotter().createSequence(String.valueOf(i),name, gs.getColor(), true);
			
		}
		message = new JLabel();
		message.setLabelFor(p);
		message.setForeground(Color.RED);
		add(p, BorderLayout.CENTER);
		add(message, BorderLayout.SOUTH);
	}
	
	/**
	 * Add this graph to a TimeComboBox.
	 * @param timeComboBox the timeComboBox.
	 */
	public void linkToTimeComboBox(TimeComboBox timeComboBox){
		timeComboBox.addPlotter(p.getPlotter());
	}

	/**
	 * Update the values of the graph.
	 * @param msc MBeanServerConnection.
	 */
	public void updateGraph(MBeanServerConnection msc) {
		//remove error
		message.setText(""); //$NON-NLS-1$
		long[] values = new long[l.length];
		for (int i = 0; i < l.length; i++) {
			final GraphSequence seq = l[i];
			ObjectName o = seq.getObjectName();
			String n = seq.getAttribute();
			try {
				//get value
				values[i] = Utils.getValue(msc, o, n);
			} catch (MBeanException e) {
				//set error
				message.setText(e.getLocalizedMessage());
				return;
			}
		}
		//add all values
		p.getPlotter().addValues(System.currentTimeMillis(), values);
	}

}
