package rowley.eclipse.notch.bindings.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateVariable;

public class TemplateGroup {

	private List<TemplateWrapper> buffers = new ArrayList<TemplateWrapper>();

	public void addTemplate(int start, int length, TemplateBuffer buffer) {
		TemplateWrapper wrapper = new TemplateWrapper(start, length, buffer);
		int index = getIndex(wrapper);
		shift(index, wrapper);
		getBuffers().add(index, wrapper);
	}

	private void shift(int index, TemplateWrapper shifter) {
		for (int i = index; i < getBuffers().size(); i++) {
			TemplateWrapper wrapper = getBuffers().get(i);
			wrapper.shift = wrapper.shift + shifter.getLength();
		}
	}
	private int getIndex(TemplateWrapper wrapper) {
		for (int i = 0; i < getBuffers().size(); i++) {
			if (getBuffers().get(i).getStart() >= wrapper.getStart()) {
				return i;
			}
		}
		return getBuffers().size();
	}

	public TemplateVariable[] getVariables() {

		Map<String, TemplateVariable> variables = new HashMap<String, TemplateVariable>();
		for (TemplateWrapper wrapper : getBuffers()) {
			int start = wrapper.getStart() + wrapper.shift;
			for (TemplateVariable variable : wrapper.getBuffer().getVariables()) {
				String name = variable.getName();
				TemplateVariable grouped = null;
				if (!variables.containsKey(name)) {
					grouped = new TemplateVariable(variable.getType(), variable.getName(), variable.getDefaultValue(), new int[0]);
					variables.put(name, grouped);
				} else {
					grouped = variables.get(name);
				}
				int[] offsets = new int[grouped.getOffsets().length + variable.getOffsets().length];
				for (int i = 0; i < grouped.getOffsets().length; i++) {
					offsets[i] = grouped.getOffsets()[i];
				}
				for (int i = 0; i < variable.getOffsets().length; i++) {
					offsets[i + grouped.getOffsets().length] = variable.getOffsets()[i] + start;
				}
				grouped.setOffsets(offsets);
			}
		}
		return variables.values().toArray(new TemplateVariable[variables.values().size()]);

	}

	public List<TemplateWrapper> getBuffers() {
		return buffers;
	}

	public void setBuffers(List<TemplateWrapper> buffers) {
		this.buffers = buffers;
	}
}
