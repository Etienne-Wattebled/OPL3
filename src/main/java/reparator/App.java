package reparator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import spoon.Launcher;

public class App {

	//example of args:
	// -nbrCommit 10 -projectPath demoProject -sourcePath src -classPath demoProject/target
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
		Launcher spoon;
		
		System.out.println("number of commits = "+nbr);
		System.out.println("projectPath = "+projectPath);
		System.out.println("pathToSourceFromFolder = "+pathToSourceFromFolder);
		System.out.println("classPath = "+classPath);
		
		System.out.println("execute git to generate "+nbr+" folders");
		//TO DO : execute bash script
		
		for(int i=0;i<nbr;i++){
			System.out.println("spoon sources "+projectPath+i+"/"+pathToSourceFromFolder);
			System.out.println("with classPath = "+classPath);
			//TO DO
			//spoon = new Launcher();
			//spoon.run(new String[]{"-i",projectPath+i+"/"+pathToSourceFromFolder,"--source-classpath",classPath});
		}

		System.out.println("Spoon the last commit as the new project template = "+projectPath+"/"+pathToSourceFromFolder);
		System.out.println("with classPath = "+classPath);
		
	}

}
