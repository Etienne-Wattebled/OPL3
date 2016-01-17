package reparator;

import org.apache.commons.cli.*;

import spoon.Launcher;
import util.CmdTools;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class App {


	private static final String jouvenceDir = "./ressources";
	private static String jouvenceFile = "jouvence_linux.sh";
	//private static String jouvenceFile = "jouvence";
	private static String jouvenceBranch = "master";

	private static ArrayList<VersionSniper> snipers;

	static {
		snipers = new ArrayList<VersionSniper>();
	}


	//example of args:
	// -nbrCommit 3 -projectPath ressources/demoproject -sourcePath src/main -classPath ressources/demoproject/target:/home/m2iagl/dufaux/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar
	// -nbrCommit 3 -projectPath ressources/jsoup -sourcePath src/main -classPath ressources/jsoup/target:/home/dufaux/.m2/repository/junit/junit/4.5/junit-4.5.jar
	public static void main(String[] args) {

		
		// create Options object
    	Options options = new Options();
    	// add t option
    	options.addOption("projectPath", true, "path to the project");
    	options.addOption("sourcePath",true,"Relative source path from folder position (ex: if the project path is /tmp/project and the sources are in /tmp/project/src, the sourcepath may be src");
    	options.addOption("classPath", true, " An optional classpath to be passed to the internal Java compiler when building or compiling the input sources.");
    	options.addOption("nbrCommit", true, " Number of commits to use to generate the new project");

    	CommandLineParser parser = new DefaultParser();
    	try {
    		CommandLine cmd = parser.parse( options, args);
        	if(!cmd.hasOption("-projectPath")) {
        		HelpFormatter formatter = new HelpFormatter();
        		formatter.printHelp( "list of parameters", options );
        		System.exit(0);
        	}
        	reparator(Integer.parseInt(cmd.getOptionValue("nbrCommit")),cmd.getOptionValue("projectPath"),cmd.getOptionValue("sourcePath"),cmd.getOptionValue("classPath"));

        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
	}


	public static void reparator(int nbr, String projectPath, String pathToSourceFromFolder, String classPath ){

		System.out.println("number of commits = "+nbr);
		System.out.println("projectPath = "+projectPath);
		System.out.println("pathToSourceFromFolder = "+pathToSourceFromFolder);
		System.out.println("classPath = "+classPath);

		System.out.println("execute git to generate "+nbr+" folders");


		CmdTools.executeSH(jouvenceDir, jouvenceFile, projectPath, (nbr+""), jouvenceBranch);
		
		

		for(int i=0;i<nbr;i++){
			snipers.add(new VersionSniper(projectPath, pathToSourceFromFolder, classPath, i));
		}

		System.out.println("Spoon the last commit as the new project template = "+projectPath+"/"+pathToSourceFromFolder);
		System.out.println("with classPath = "+classPath);
		
		Launcher spoon = new Launcher();
		spoon.addProcessor(new MethodVersioningProcessor(snipers));
        spoon.run(new String[]{"-i",projectPath+"/"+pathToSourceFromFolder,"--source-classpath",classPath});
        System.out.println("---- End of program ----");
        
	}

}
