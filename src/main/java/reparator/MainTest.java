package reparator;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.util.Set;
import java.util.Vector;


public class MainTest {
	

	static List<Class<?>> projectClasses = new ArrayList<Class<?>>();
	static List<Class<?>> testsClasses = new ArrayList<Class<?>>(); 
	
	public static void main(String[] args) throws Exception {
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		
		String repertoire = "org.jsoup";
		final URL[] urls = { new File(repertoire).toURI().toURL()};
		URLClassLoader cl = URLClassLoader.newInstance(urls);

		Reflections reflections = new Reflections(new ConfigurationBuilder()
			    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
			    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
			    .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(repertoire))));
			
		
		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		 for(Class<?> c : allClasses){
			 if(c.getName().endsWith("Test")){
				 testsClasses.add(c);
			 }else{
				 projectClasses.add(c);
			 }
		 }
		 
		 
		 
		 for(Class<?>c : projectClasses){
			 System.out.println("MODIF CLASS "+c.getName());

			 for(Method m : c.getDeclaredMethods()){
				 try{
					 Field vfield = c.getField((m.getName()+"_version"));
				     Field vmaxfield = c.getField(m.getName()+"_version_max");

					 System.out.println("MODIF METHOD "+m.getName());
				      while(vfield.getInt(null) < vmaxfield.getInt(null)){
				    	  runTests();
				        vfield.setInt(null, vfield.getInt(null)+1);
				      }
				 }catch(NoSuchFieldException e){
					 //ne rien faire
				 }
				 
			 }
		 }
		 
	}
	
	
	public static void runTests() {
		Class[] classes = new Class[testsClasses.size()];
		classes = testsClasses.toArray(classes);
		
		Result result = JUnitCore.runClasses(classes);
		System.out.println("runCount = "+result.getRunCount());
		System.out.println("FailureCount = "+result.getFailureCount());
		System.out.println("");

		
		
    }
}
