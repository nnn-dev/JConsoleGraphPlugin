package nnn.jconsole.graph;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.JMException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeType;

/**
 * Others methods.
 *
 */
public class Utils {
	private Utils() {

	};

	/**
	 * Get all the potential sequences (i.e. with number value)
	 * 
	 * @param msc
	 *            connection.
	 * @return all potential sequences.
	 * @throws JConsoleGraphPluginException
	 *             exception during retrieve.
	 */
	public static Set<GraphSequence> getPotentialsSequences(MBeanServerConnection msc)
			throws JConsoleGraphPluginException {
		Set<ObjectName> os;
		try {
			os = msc.queryNames(null, null);
		} catch (IOException e1) {
			throw new JConsoleGraphPluginException(Messages.getString("Utils.ERROR_MBEAN"), e1); //$NON-NLS-1$
		}
		Set<GraphSequence> res = new TreeSet<GraphSequence>();
		for (ObjectName o : os) {
			MBeanInfo m;
			try {
				m = msc.getMBeanInfo(o);
				MBeanAttributeInfo[] iatts = m.getAttributes();
				String[] satts = new String[iatts.length];
				for (int i = 0; i < iatts.length; i++) {
					satts[i] = iatts[i].getName();
				}
				for (Attribute i : getAttributes(msc,o, satts)) {
					try {
						Object v = i.getValue();
						searchNumberValue(res, o, i.getName(), v);
					} catch (RuntimeMBeanException e) {
						// ignore because we cannot get attribute.
                    } 
				}
			} catch (javax.management.InstanceNotFoundException e1) {
				// ignore because mbean does not exist anymore
			} catch (IOException e1) {
				throw new JConsoleGraphPluginException(
						Messages.getString("Utils.ERROR_ATTRIBUTE") + o.getCanonicalName(), e1); //$NON-NLS-1$
			} catch (JMException e1) {
				throw new JConsoleGraphPluginException(
						Messages.getString("Utils.ERROR_ATTRIBUTE") + o.getCanonicalName(), e1); //$NON-NLS-1$
			}
		}
		return res;
	}

    private static java.util.List<Attribute> getAttributes(MBeanServerConnection msc, ObjectName o, String[] atts) throws javax.management.InstanceNotFoundException {
        try{
            return msc.getAttributes(o,atts).asList();
        }catch(IOException e) {
            //we cannot retreive an attribute, test with each attributes
        }catch(javax.management.ReflectionException e){
            //ignore
        }
        java.util.List<Attribute> attributes=new java.util.ArrayList<Attribute>();
        for(String name: atts){
            try{
                Object v=msc.getAttribute(o,name);
                attributes.add(new Attribute(name,v));
            }catch(javax.management.JMException e){
             //ignore;
            }catch(IOException e){
                //ignore?
            }
        }
        return attributes;
    }
    
	private static void searchNumberValue(Set<GraphSequence> res, ObjectName o, String i, Object v) {
		if (v instanceof Number) {
			res.add(new GraphSequence(o, i));
		} else if (v instanceof CompositeData) {
			final CompositeData data = ((CompositeData) v);
			CompositeType ct = data.getCompositeType();
			for (String key : ct.keySet()) {
				searchNumberValue(res, o, String.format("%s.%s", i, key), data.get(key)); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Retrieve value of sequence.
	 * 
	 * @param msc
	 *            connection.
	 * @param o
	 *            objectname.
	 * @param n
	 *            attribute name.
	 * @return value.
	 * @throws JConsoleGraphPluginException
	 *             exception during retrieve.
	 */
	public static long getValue(MBeanServerConnection msc, ObjectName o, String n) throws MBeanException {
		Object v;
		try {
			String[] s = n.split("\\."); //$NON-NLS-1$
			v = msc.getAttribute(o, s[0]);
			if (v instanceof CompositeData) {
				for (int j = 1; j < s.length; j++) {
					v = ((CompositeData) v).get(s[j]);
				}
			}
			if (v instanceof Number) {
				return ((Number) v).longValue();
			} else {
				return Long.parseLong(String.valueOf(v));
			}
		} catch (JMException e) {
			throw new MBeanException(e);
		} catch (IOException e) {
			throw new MBeanException(e);
		}

	}
}
