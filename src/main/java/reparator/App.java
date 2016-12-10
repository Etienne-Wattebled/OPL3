package reparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import com.martiansoftware.jsap.ParseException;

import spoon.Launcher;
import util.CmdTools;
import util.FunctionsUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class App {


	private static final String jouvenceDir = "./ressources";
	private static String jouvenceFile = "jouvence.bat";
	private static String jouvenceBranch = "master";
	
	private static ArrayList<VersionSniper> snipers = new ArrayList<VersionSniper>();
	private static List<Class<?>> projectClasses = new ArrayList<Class<?>>();
	private static List<Class<?>> testsClasses = new ArrayList<Class<?>>(); 
	
	//example of args:
	// -nbrCommit 3 -projectPath ressources/demoproject -sourcePath src -classPath ...junit-3.8.1.jar -packages demoproject
	// -nbrCommit 3 -projectPath ressources/jsoup -sourcePath src -classPath ...junit-4.5.jar -packages com.jsoup
	public static void main(String[] args) throws Exception {

		
		// create Options object
    	Options options = new Options();
    	// add t option
    	options.addOption("projectPath", true, "path to the project");
    	options.addOption("sourcePath",true,"Relative source path from folder position (ex: if the project path is /tmp/project and the sources are in /tmp/project/src, the sourcepath may be src");
    	options.addOption("classPath", true, " An optional classpath to be passed to the internal Java compiler when building or compiling the input sources.");
    	options.addOption("nbrCommit", true, " Number of commits to use to generate the new project");
    	options.addOption("packages", true, "Packages of the projects (or parts). Use ';' to separate all packages. You can use the name of classes too... (ex: if you have com.lille1.hello and com.lille1.hello2 you can put 'com.lille1' or 'lille1' or 'com.lille1.hello;com.lille1.hello2' or 'hello1;hello2'. Try to be precise in order to prevent conflicts.");

    	CommandLineParser parser = new DefaultParser();
    	try {
    		CommandLine cmd = parser.parse( options, args);
        	if(!cmd.hasOption("-projectPath")) {
        		HelpFormatter formatter = new HelpFormatter();
        		formatter.printHelp( "list of parameters", options );
        		System.exit(0);
        	}
        	downloadVersionsAndRunSpoonTransformations(Integer.parseInt(cmd.getOptionValue("nbrCommit")),cmd.getOptionValue("projectPath"),cmd.getOptionValue("sourcePath"),cmd.getOptionValue("classPath"));
        	runAllTests(cmd.getOptionValue("packages").trim().split(";"),cmd.getOptionValue("classPath"));
        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
	}


	public static void downloadVersionsAndRunSpoonTransformations(int nbr, String projectPath, String pathToSourceFromFolder, String classPath ){

		System.out.println("number of commits = "+nbr);
		System.out.println("projectPath = "+projectPath);
		System.out.println("pathToSourceFromFolder = "+pathToSourceFromFolder);
		System.out.println("classPath = "+classPath);
		System.out.println("execute git to generate "+nbr+" folders");
		
		// Download versions
		CmdTools.executeSH(jouvenceDir, jouvenceFile, projectPath, (nbr+""), jouvenceBranch);
		
		FunctionsUtils.cleanFiles(projectPath);
		
		for(int i=0;i<nbr;i++){
			snipers.add(new VersionSniper(projectPath, pathToSourceFromFolder, classPath, i));
		}

		System.out.println("Spoon the last commit as the new project template = "+projectPath+"/"+pathToSourceFromFolder);
		System.out.println("with classPath = "+classPath);
		
		// Run Spoon transformations
		Launcher spoon = new Launcher();
		spoon.addProcessor(new MethodVersioningProcessor(snipers));
        spoon.run(new String[]{
        		"-i",projectPath+"/"+pathToSourceFromFolder,
        		"--source-classpath",classPath,
        		"-d","./target/classes/",
        		"--compile"
        });
	}
	
	public static void runAllTests(String[] packages, String classPath) throws Exception {
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		
		Reflections reflections = new Reflections(new ConfigurationBuilder()
			    .setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
			    .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))));
		
		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		boolean take = false;
		String currentPackage = null;
		for(Class<?> c : allClasses){
			take = false;
			currentPackage = c.getName();
			for (String p : packages) {
				if (currentPackage.contains(p)) {
					take = true;
					break;
				}
			}
			if (take) {
				 if(c.getName().endsWith("Test")){
					 testsClasses.add(c);
				 }else{
					 projectClasses.add(c);
				 }
			}
		}
		 System.out.println(testsClasses.size());
		 System.out.println(projectClasses.size());
		 for(Class<?>c : projectClasses){
			 System.out.println("MODIF CLASS "+c.getName());
			 for(Method m : c.getDeclaredMethods()){
				 try{
					 Field vfield = c.getDeclaredField((m.getName()+"_version"));
				     Field vmaxfield = c.getDeclaredField(m.getName()+"_version_max");
				     vfield.setAccessible(true);
				     vmaxfield.setAccessible(true);
				     
					 System.out.println("MODIF METHOD "+m.getName());
				      while(((Integer)(vfield.get(Integer.class))).intValue() < ((Integer)(vmaxfield.get(Integer.class))).intValue()){
				    	  runTests();
				    	  vfield.set(Integer.class,new Integer(((Integer)(vfield.get(Integer.class))).intValue()+1));
				      }
				      vfield.set(Integer.class,new Integer(0));
				 }catch(NoSuchFieldException e){
					 //ne rien faire
				 }
				 
			 }
		 }
		 System.out.println("---- End of program ----"); 
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
