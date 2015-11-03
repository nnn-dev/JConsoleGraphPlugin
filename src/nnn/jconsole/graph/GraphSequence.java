package nnn.jconsole.graph;

import java.awt.Color;

import javax.management.ObjectName;

/**
 * A sequence for a graph.
 *
 */
public class GraphSequence implements Comparable<GraphSequence> {

	/**
	 * Create a sequence.
	 * 
	 * @param objectName
	 *            name of the mbean.
	 * @param attributename
	 *            name of the attribute.
	 */
	public GraphSequence(ObjectName objectName, String attributename) {
		this.name = objectName;
		this.attribute = attributename;
	}

	private ObjectName name;
	private String attribute;

	/**
	 * Get the mbean name.
	 * 
	 * @return ObjectName.
	 */
	public ObjectName getObjectName() {
		return name;
	}

	/**
	 * Get the attribute.
	 * 
	 * @return attribute.
	 */
	public String getAttribute() {
		return attribute;
	}

	private boolean visible = false;

	public void setVisible(boolean v) {
		this.visible = v;
	}

	/**
	 * Indicate if the sequence is visible (or to be added to a new graph).
	 * 
	 * @return sequence is visible.
	 */
	public boolean isVisible() {
		return this.visible;
	}

	private Color color;

	/**
	 * Indicate the color of the graph.
	 * 
	 * @return color of the graph or <code>null</code>.
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Set color.
	 * 
	 * @param c
	 *            new color.
	 */
	public void setColor(Color c) {
		this.color = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(GraphSequence o) {
		if (o.getObjectName().equals(this.getObjectName())) {
			return this.getAttribute().compareTo(o.getAttribute());
		}
		return this.getObjectName().compareTo(o.getObjectName());
	}

}
