package ch.pschatzmann.starschema.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Templater {
	private final static ArrayList empty = new ArrayList();
	private Map<String,List<TemplateRef>> map = new TreeMap<String, List<TemplateRef>>();
	
	public void setTemplate(String input, TemplateUse use) {
		List<String> inputTerms = getTerms(input);
		Collection<TemplateRef> potentialTemplates = findTemplates(inputTerms);
		if (potentialTemplates.isEmpty()) {
			createNewTemplate(input, use, inputTerms);
		} else {
			TemplateRef best = getBestTemplate(potentialTemplates, inputTerms);
		}
	}



	private TemplateRef getBestTemplate(Collection<TemplateRef> potentialTemplates, List<String> inputTerms) {
		Collection <Score> scores = new ArrayList<Score>();
		for (TemplateRef ref : potentialTemplates) {
			List<String> refTerms = getTerms(ref.template.getTemplate());
			int sizeScore = Math.abs(refTerms.size() - inputTerms.size());

		}
		return null;
		
	}



	private void createNewTemplate(String input, TemplateUse use, List<String> inputTerms) {
		// create ref objec
		Template template = new Template(input,empty);
		TemplateRef ref = new TemplateRef(template,use);
		// update index
		mapPut(inputTerms.get(0), ref);
		mapPut(inputTerms.get(1), ref);
		mapPut(inputTerms.get(2), ref);
	}
	
	
	
	private void mapPut(String key, TemplateRef ref) {
		List<TemplateRef> list = map.get(key);
		if (list==null) {
			list = new ArrayList<TemplateRef>();
			map.put(key, list);
		}
		list.add(ref);		
	}



	/**
	 * The first 3 words are considered as search terms and we return the 
	 * best templates which matches most of the words
	 * @param inputTerms
	 * @return
	 */
	private List<TemplateRef> findTemplates(List<String> inputTerms) {
		List<TemplateRef> c1 = map.get(inputTerms.get(0));
		List<TemplateRef> c2 = map.get(inputTerms.get(1));
		List<TemplateRef> c3 = map.get(inputTerms.get(2));
		
		List<TemplateRef> cc1 = new ArrayList<TemplateRef>(c1);
		List<TemplateRef> cc2 = new ArrayList<TemplateRef>(cc1);
		cc2.removeAll(c2);
		List<TemplateRef> cc3 = new ArrayList<TemplateRef>(cc2);
		cc3.removeAll(c3);
		
		if (!cc3.isEmpty()) {
			return cc3;
		}
		if (!cc2.isEmpty()) {
			return cc2;
		}
		return cc1;
	}
	
	
	private List<String> getTerms(String input) {
		return Arrays.asList(input.split(" "));
	}

}
