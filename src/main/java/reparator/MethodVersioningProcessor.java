package reparator;

import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

public class MethodVersioningProcessor extends AbstractProcessor<CtClass> {
	
	
	List<VersionSniper> snipers;

	public MethodVersioningProcessor(ArrayList<VersionSniper> snipers) {
		this.snipers = snipers;
	}

	public void process(CtClass element) {
		//TO DO parcours method & co 
	}

}
