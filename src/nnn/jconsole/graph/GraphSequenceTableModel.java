package nnn.jconsole.graph;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Using to configure sequence to add.
 * @see GraphCreateDialog
 *
 */
public class GraphSequenceTableModel implements TableModel {

	private GraphSequence[] tables;
	private int nbcolor = 0;

	/**
	 * Create the list.
	 * @param g the list of possibly sequences.
	 */
	public GraphSequenceTableModel(Set<GraphSequence> g) {
		tables = new GraphSequence[g.size()];
		tables = g.toArray(tables);
		Arrays.sort(tables);
	}

	@Override
	public void addTableModelListener(TableModelListener arg0) {

	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		switch (arg0) {
		case 0:
			return Boolean.class;
		case 1:
		case 2:
			return String.class;
		case 3:
			return Color.class;
		default:
			return null;
		}
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public String getColumnName(int arg0) {
		switch (arg0) {
		case 0:
			return "Add?";
		case 1:
			return "ObjectName";
		case 2:
			return "Attribute";
		case 3:
			return "Color";
		default:
			return null;
		}
	}

	@Override
	public int getRowCount() {
		return tables.length;
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		GraphSequence g = tables[arg0];
		switch (arg1) {
		case 0:
			return g.isVisible();
		case 1:
			return g.getObjectName();
		case 2:
			return g.getAttribute();
		case 3:
			return g.getColor();
		default:
			return null;
		}
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return (arg1 == 0 || arg1 == 3);
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {

	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		GraphSequence g = tables[arg1];
		switch (arg2) {
		case 0:
			g.setVisible((Boolean) arg0);
			if (g.getColor() == null) {
				g.setColor(newColor());
			}
			break;
		case 3:
			g.setColor((Color) arg0);
			break;
		default:
			return;
		}
	}

	/**
	 * Default colors for sequences.
	 */
	private static Color[] COLORS = new Color[] { Color.BLACK, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK,
			Color.RED, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.WHITE, };

	/**
	 * Give a new default color.
	 * @return the choose color.
	 */
	private Color newColor() {
		Color res = COLORS[nbcolor % COLORS.length];
		nbcolor++;
		return res;
	}

	/**
	 * Returns all selected sequences.
	 * @return sequences to show.
	 */
	public Collection<GraphSequence> getVisibles() {
		HashSet<GraphSequence> res = new HashSet<GraphSequence>();
		for (GraphSequence g : tables) {
			if (g.isVisible()) {
				res.add(g);
			}
		}
		return res;
	}

}
