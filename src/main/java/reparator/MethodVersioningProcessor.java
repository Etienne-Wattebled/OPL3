package reparator;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtReturnImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MethodVersioningProcessor extends AbstractProcessor<CtClass> {
	
	
	List<VersionSniper> snipers;

	public MethodVersioningProcessor(ArrayList<VersionSniper> snipers) {
		this.snipers = snipers;
	}

	public void process(CtClass element) {
		Set<CtMethod<?>> methods = new TreeSet<CtMethod<?>>(element.getMethods());
		List<CtMethod> newMethodsVersions;
		CtMethod newMethod;
		
		
		//for each method of the last version
		for(CtMethod<?> method : methods){
			newMethodsVersions = new ArrayList<CtMethod>();
			List<CtMethod<?>> oldMethods = new ArrayList<CtMethod<?>>();
			for(VersionSniper sniper : snipers){
				newMethod = createVersionMethod(method, sniper);
				if(newMethod != null){
					newMethodsVersions.add(newMethod);
				}
			}

			System.out.println("create");
			createFonctionDAppelSwitch(element, method, newMethodsVersions);

			System.out.println("---------------------------------------------------");
			System.out.println(element);
		}
		
	}

	private CtMethod createVersionMethod(CtMethod<?> method, VersionSniper sniper) {
		Factory factory = sniper.getFactory();
		int version = sniper.getId();
		
		CtType parent = method.getParent(CtType.class);
		for(CtType c : factory.Class().getAll()){
			if(c.getQualifiedName().equals(parent.getQualifiedName())){
				//we found the same class
				//let's try to find the method now
				Set<CtMethod<?>> methodsOfSniper = c.getMethods();
				for(CtMethod<?> oldMethod : methodsOfSniper){
					if(method.getSignature().equals(oldMethod.getSignature())){
						//create new method with clone of actual method and set the block with a clone of old method block
						CtMethod newMethod = getFactory().Core().clone(method);
						//CtMethod newMethod = getFactory().Core().createMethod();
						CtBlock newBlock = getFactory().Core().clone(oldMethod.getBody());
						newMethod.setBody(newBlock);
						newMethod.setSimpleName(newMethod.getSimpleName()+"_"+version);
						parent.addMethod(newMethod);
						return newMethod;
					}
				}
			}
		}
		return null;
	}

	
	private void createFonctionDAppelSwitch(CtClass ctClass, CtMethod<?> methodeSource, List<CtMethod> methodesDeVersions) {
		CtReturn retur = null;
		
		//CtMethod originalMethod = (CtMethod) ctClass.getMethodsByName(methodeSource.getSimpleName()).get(0);

		CtTypeReference refToInt = getFactory().Code().createCtTypeReference(Integer.class);
		
		//create versionField
		CtField versionField = getFactory().Code().createCtField(methodeSource.getSimpleName()+"_version",refToInt,"0", ModifierKind.FINAL, ModifierKind.PRIVATE, ModifierKind.STATIC);
		ctClass.addField(versionField);
		
		//create versionMaxField
		CtField versionMaxField = getFactory().Code().createCtField(methodeSource.getSimpleName()+"_version_max",refToInt,Integer.toString(methodesDeVersions.size()-1), ModifierKind.FINAL, ModifierKind.PRIVATE, ModifierKind.STATIC);
		ctClass.addField(versionMaxField);
		
		// create b1 = block vide de ctmethod
		CtBlock nwMethBody = getFactory().Core().createBlock();
		
		CtSwitch ctSwitch = getFactory().Core().createSwitch();
		
		System.out.println("VersionField ==== "+versionField);
		versionField.setParent(ctClass);
		System.out.println(versionField.getDeclaringType());
		System.out.println(versionField.getParent());
		
		CtFieldRead ctFieldRead = getFactory().Core().createFieldRead().setVariable(versionField.getReference());
		ctSwitch.setSelector(ctFieldRead);
		nwMethBody.addStatement(ctSwitch);
		
		int i =0;
		System.out.println("---------------------------------");
		System.out.println(methodesDeVersions);
		for (CtMethod methodVersion : methodesDeVersions) {
			
			CtCase newCase = getFactory().Core().createCase();
			CtExpression caseExpression = getFactory().Code().createCodeSnippetExpression(Integer.toString(i));
			newCase.setCaseExpression(caseExpression);
			
			List<CtParameter<?>> arguments = methodeSource.getParameters();

			//crée le tableau de parametre passé à l'appel de fonction. 
			List<CtExpression> exps = new ArrayList<CtExpression>();
			for(CtParameter<?> arg : arguments){ 
				System.out.println("arg ==="+arg.getSignature());
				CtExpression expArg = getFactory().Code().createCodeSnippetExpression(arg.getSimpleName());
				exps.add(expArg);
				System.out.println("DEFAULT EXPRESSION =");
				System.out.println(expArg);
			}
			
			//cree l'appel de fonction
			
			//System.out.println("---");
			//System.out.println(arguments);
			CtInvocation callFunction = getFactory().Core().createInvocation();
			//System.out.println(methodVersion);
			callFunction.setExecutable(methodVersion.getReference());
			//System.out.println(exps.size());
			callFunction.setArguments(exps);
			
			//ajoute "return" devant si la fonction retourne quelque chose
			if(! callFunction.getExecutable().getType().getActualClass().equals(void.class)){
				retur = new CtReturnImpl();
				retur.setReturnedExpression(callFunction);
				newCase.addStatement(retur);
			}
			else{
				newCase.addStatement(callFunction);
			}
			//ajoute le case et passe a l'appel suivant
			ctSwitch.addCase(newCase);
			i++;
		}
		//add the last return at the end of method as default. (i don't know how to create default in switch java).
		if(retur != null){
			nwMethBody.addStatement(retur);
		}
		
		methodeSource.setBody(nwMethBody);
		
		/*// pour meth in ctmethods :
		for (CtMethod methodVersion : methodesDeVersions) {

			// add try catch in b1
			CtTry ctTry = getFactory().Core().createTry();
			nwMethBody.addStatement(ctTry);
			//ctTry.insertAfter(nwMethBody.getLastStatement());
			
			// catch
			CtCatch ctCatch = getFactory().Code().createCtCatch("allCatch", Throwable.class, nwMethBody);
			
			//To do (catch can't be cast in expression)
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
		originalMethod.getBody().replace(nwMethBody);*/

	}
	
	
	/*private void createFonctionDAppel(CtClass ctClass, CtMethod methodeSource, List<CtMethod> methodesDeVersions) {

		CtMethod originalMethod = (CtMethod) ctClass.getMethodsByName(methodeSource.getSimpleName()).get(0);

		// create b1 = block vide de ctmethod
		//CtBlock originalMethodBlock = originalMethod.getBody();
		CtBlock nwMethBody = getFactory().Core().createBlock();


		// pour meth in ctmethods :
		for (CtMethod methodVersion : methodesDeVersions) {

			// add try catch in b1
			CtTry ctTry = getFactory().Core().createTry();
			nwMethBody.addStatement(ctTry);
			//ctTry.insertAfter(nwMethBody.getLastStatement());
			
			// catch
			CtCatch ctCatch = getFactory().Code().createCtCatch("allCatch", Throwable.class, nwMethBody);
			
			//To do (catch can't be cast in expression)
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

	}*/

}
