package ch.pschatzmann.starschema.templates;

import java.util.ArrayList;
import java.util.Collection;

public class Template {
	String template;
	Collection<String> parameters;
	
	public Template(String input, ArrayList empty) {
		this.template = input;
		this.parameters = empty;
	}
	/**
	 * @return the template
	 */
	public String getTemplate() {
		return template;
	}
	/**
	 * @param template the template to set
	 */
	public void setTemplate(String template) {
		this.template = template;
	}
	/**
	 * @return the parameters
	 */
	public Collection<String> getParameters() {
		return parameters;
	}
	/**
	 * @param parameters the parameters to set
	 */
	public void setParameters(Collection<String> parameters) {
		this.parameters = parameters;
	}
	
	@Override
	public String toString() {
		String result = "";
		for (String par : this.getParameters()) {
			result = result.replace("@%", par);
		}
		return result;
	}
}
