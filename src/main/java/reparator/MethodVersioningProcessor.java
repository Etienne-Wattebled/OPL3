package reparator;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtReturnImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class MethodVersioningProcessor extends AbstractProcessor<CtClass> {
	
	
	List<VersionSniper> snipers;
	HashMap<CtMethod,String> fieldsName;
	
	public MethodVersioningProcessor(List<VersionSniper> snipers) {
		this.snipers = snipers;
		this.fieldsName = new HashMap<CtMethod,String>();
	}

	public void process(CtClass element) {
		if (element.isInterface() 
				|| element.getSignature().toUpperCase().contains("enum".toUpperCase()) //quick fix, need to find which is enum.
				|| element.isAnonymous() 
				|| element.getParent(CtClass.class) != null //possède un parent (donc nestedclass)
				|| element.getSimpleName().endsWith("Test")) {
			System.out.println(element.getSignature());
			return;
		}
		Set<CtMethod<?>> methods = new TreeSet<CtMethod<?>>(element.getMethods());
		List<CtMethod> newMethodsVersions;
		CtMethod newMethod;

		for(CtMethod<?> method : methods){
			if ((method.getBody() != null) && (!method.hasModifier(ModifierKind.STATIC))) {
				newMethodsVersions = new LinkedList<CtMethod>();
				//List<CtMethod<?>> oldMethods = new ArrayList<CtMethod<?>>();
				for(VersionSniper sniper : snipers){
					newMethod = createVersionMethod(method, sniper);
					if(newMethod != null){
						if(element.getSignature().contains("UnModifiableCollection")){
							System.out.println("NEW Method = "+newMethod.getSignature());
						}
						newMethodsVersions.add(newMethod);
					}
				}
				createFonctionDAppelSwitch(element, method, newMethodsVersions);
	
			}
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
						
						CtMethod newMethod = getFactory().Core().clone(oldMethod);
						
						//remove annotation (to avoid @override for example)
						newMethod.setAnnotations(new ArrayList<CtAnnotation<?>>());
						
						//CtMethod newMethod = getFactory().Core().createMethod();
						CtBlock newBlock = getFactory().Core().clone(oldMethod.getBody());

						newMethod.setBody(newBlock);
						newMethod.setSimpleName(newMethod.getSimpleName()+"_"+version);
						newMethod.removeModifier(ModifierKind.PUBLIC);
						newMethod.removeModifier(ModifierKind.PROTECTED);
						newMethod.addModifier(ModifierKind.PRIVATE);
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
		
		String fieldName = getFieldName(methodeSource);
			
		//create versionField
		CtField versionField = getFactory().Code().createCtField(fieldName+"_version",refToInt,"0", ModifierKind.PUBLIC, ModifierKind.STATIC);
		ctClass.addField(versionField);
		System.out.println(fieldName);
		//create versionMaxField
		CtField versionMaxField = getFactory().Code().createCtField(fieldName+"_version_max",refToInt,Integer.toString(methodesDeVersions.size()-1), ModifierKind.PUBLIC, ModifierKind.STATIC);
		ctClass.addField(versionMaxField);
		
		// create b1 = block vide de ctmethod
		CtBlock nwMethBody = getFactory().Core().createBlock();
		
		CtSwitch ctSwitch = getFactory().Core().createSwitch();
		
		versionField.setParent(ctClass);
		
		CtFieldRead ctFieldRead = getFactory().Core().createFieldRead().setVariable(versionField.getReference());
		ctSwitch.setSelector(ctFieldRead);
		nwMethBody.addStatement(ctSwitch);
		
		int i =0;
		
		for (CtMethod methodVersion : methodesDeVersions) {
			
			CtCase newCase = getFactory().Core().createCase();
			CtExpression caseExpression = getFactory().Code().createCodeSnippetExpression(Integer.toString(i));
			newCase.setCaseExpression(caseExpression);
			
			List<CtParameter<?>> arguments = methodeSource.getParameters();

			//crée le tableau de paramètres passé à l'appel de fonction. 
			List<CtExpression> exps = new ArrayList<CtExpression>();
			for(CtParameter<?> arg : arguments){ 
				//System.out.println("arg ==="+arg.getSignature());
				CtExpression expArg = getFactory().Code().createCodeSnippetExpression(arg.getSimpleName());
				exps.add(expArg);
				//System.out.println("DEFAULT EXPRESSION =");
				//System.out.println(expArg);
			}
			
			//crée l'appel de fonction
			
			//System.out.println("---");
			//System.out.println(arguments);
			CtInvocation callFunction = getFactory().Core().createInvocation();
			//System.out.println(methodVersion);
			callFunction.setExecutable(methodVersion.getReference());
			
			if (methodVersion.hasModifier(ModifierKind.STATIC)) {
				callFunction.getExecutable().setStatic(true);
				callFunction.setTarget(null);
			}
			//System.out.println(exps.size());
			callFunction.setArguments(exps);
			
			//ajoute "return" devant si la fonction retourne quelque chose
			if(! callFunction.getExecutable().getType().getSimpleName().equals("void")){
				retur = new CtReturnImpl();
				retur.setReturnedExpression(callFunction);
				newCase.addStatement(retur);
			}
			else{
				newCase.addStatement(callFunction);
				newCase.addStatement(getFactory().Core().createBreak());
			}
			//ajoute le case et passe à l'appel suivant
			ctSwitch.addCase(newCase);
			i++;
		}
		//add the last return at the end of method as default
		if(retur != null){
			nwMethBody.addStatement(retur);
		}
		
		methodeSource.setBody(nwMethBody);
		
	}
	
	public String getFieldName(CtMethod methode) {
		StringBuilder sb = new StringBuilder();
		sb.append(methode.getSimpleName());
		List<CtParameter> parametres = methode.getParameters();
		if ((parametres != null) && (!parametres.isEmpty())) {
			for (CtParameter parametre : parametres) {
				sb.append("_");
				sb.append(parametre.getType().toString().replaceAll("[<,>,?, ,.,\\[,\\],\\,]","0"));
			}
		}
		return sb.toString();
	}

}
