package reparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

public class MethodVersioningProcessor extends AbstractProcessor<CtClass> {
	
	
	List<VersionSniper> snipers;

	public MethodVersioningProcessor(ArrayList<VersionSniper> snipers) {
		this.snipers = snipers;
	}

	public void process(CtClass element) {
		Set<CtMethod<?>> methods = element.getMethods();
		
		//for each method of the last version
		for(CtMethod<?> method : methods){
			List<CtMethod<?>> oldMethods = new ArrayList<CtMethod<?>>();
			for(VersionSniper sniper : snipers){
				System.out.println("SNIPER ===== "+sniper.getId());
				System.out.println("CREATE VERSION METHOD FOR "+method.getSimpleName());
				createVersionMethod(method, sniper.getFactory());
			}
		}
	}

	private void createVersionMethod(CtMethod<?> method, Factory factory) {
		CtType parent = method.getParent(CtType.class);
		for(CtType c : factory.Class().getAll()){
			if(c.getQualifiedName().equals(parent.getQualifiedName())){
				//we found the same class
				//let's try to find the method now
			}
		}
	}

}
