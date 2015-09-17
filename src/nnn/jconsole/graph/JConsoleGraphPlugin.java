package nnn.jconsole.graph;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

import com.sun.tools.jconsole.JConsoleContext.ConnectionState;
import com.sun.tools.jconsole.JConsolePlugin;

/**
 * Entry of the plugin.
 *
 */
public class JConsoleGraphPlugin extends JConsolePlugin {
	JConsoleGraphPluginPanel panel = new JConsoleGraphPluginPanel();

	@Override
	public Map<String, JPanel> getTabs() {
		Map<String, JPanel> res = new HashMap<String, JPanel>();
		res.put("Custom Graphs", panel);
		return res;
	}

	@Override
	public SwingWorker<?, ?> newSwingWorker() {

		return new SwingWorker<Boolean, Object>() {

			@Override
			public Boolean doInBackground() {
				try {
					if (JConsoleGraphPlugin.this.getContext().getConnectionState() == ConnectionState.CONNECTED) {
						panel.updateGraphs(JConsoleGraphPlugin.this.getContext());
					}
					return true;
				} catch (UndeclaredThrowableException e) {
					return false;
				}
			}

		};
	}
}