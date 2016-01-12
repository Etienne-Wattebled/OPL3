package reparator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.support.reflect.code.CtBlockImpl;

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


	private void createFonctionDAppel(CtClass ctClass, CtMethod methodeSource, List<CtMethod> methodeDeVersions) {

		CtMethod originalMethod = (CtMethod) ctClass.getMethodsByName(methodeSource.getSimpleName()).get(0);

		// create b1 = block vide de ctmethod
		//CtBlock originalMethodBlock = originalMethod.getBody();
		CtBlock nwMethBody = new CtBlockImpl();


		// pour meth in ctmethods :
		for (CtMethod methodVersion : methodeDeVersions) {

			// add try catch in b1
			CtTry ctTry = getFactory().Core().createTry();
			ctTry.insertAfter(nwMethBody.getLastStatement());
			// catch
			CtCatch ctCatch = getFactory().Code().createCtCatch("allCatch", Throwable.class, nwMethBody);
			((CtExpression<?>) ctCatch).setTypeCasts((List) new ArrayList(methodVersion.getThrownTypes()));
			getFactory().Core().createContinue().insertBefore(ctCatch.getBody().getLastStatement());
			// Associate catch to try
			ctTry.addCatcher(ctCatch);

			// add return appels de meth in try -> return methodeDeVersion(...);
			// Invocation methode de version
			List<CtExpression<?>> arguments = methodVersion.getParameters();
			CtInvocation invocation = getFactory().Code().createInvocation((CtExpression<?>) ctTry, methodVersion.getReference().getOverridingExecutable(), arguments);
			// Ajout du return
			CtReturn ctReturn = getFactory().Core().createReturn();
			ctReturn.insertBefore(invocation);
		}

		// On remplace la dernière methode
		originalMethod.getBody().replace(nwMethBody);

	}

}
