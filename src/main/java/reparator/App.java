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
import util.Constants;
import util.FunctionsUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class App {

	public static final String jouvenceDir = "resources";

	private static final String targetMainPath = Constants.targetMainPath;

	private static final String targetTestPath = Constants.targetMainPath;
	// Quick fix for targetTestPath because in target/test-classes Reflections
	// doesn't work

	public static String jouvenceFile = "jouvence.bat";
	// change it for OS, values : jouvence.bat jouvence.sh jouvence_linux.sh

	public static String jouvenceBranch = "master";

	private static LinkedList<VersionSniper> snipers = new LinkedList<VersionSniper>();
	private static List<Class<?>> projectClasses = new LinkedList<Class<?>>();
	private static List<Class<?>> testsClasses = new LinkedList<Class<?>>();

	// example of args:
	// -nbrCommit 3 -projectPath ressources/demoproject -classPath
	// ...junit-3.8.1.jar -packages demoproject
	// -nbrCommit 3 -projectPath ressources/jsoup -classPath ... -packages
	// com.jsoup
	public static void main(String[] args) throws Exception {

		// create Options object
		Options options = new Options();
		// add t option
		options.addOption("projectPath", true, "path to the project");

		options.addOption("sourceMainPath", true, "Relative source path from folder position (default: src/main/java)");
		options.addOption("sourceTestPath", true, "Relative source path from folder position (default: src/test/java)");

		options.addOption("classPath", true,
				" An optional classpath to be passed to the internal Java compiler when building or compiling the input sources.");
		options.addOption("nbrCommit", true, " Number of commits to use to generate the new project");
		options.addOption("packages", true,
				"Packages of the projects (or parts). Use ';' to separate all packages. You can use the name of classes too... (ex: if you have com.lille1.hello and com.lille1.hello2 you can put 'com.lille1' or 'lille1' or 'com.lille1.hello;com.lille1.hello2' or 'hello1;hello2'. Try to be precise in order to prevent conflicts.");

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (!cmd.hasOption("-projectPath") || !cmd.hasOption("-packages")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("list of parameters", options);
				System.exit(0);
			}

			String projectPath = cmd.getOptionValue("projectPath"),
					sourceMainPath = cmd.getOptionValue("sourceMainPath"),
					sourceTestPath = cmd.getOptionValue("sourceTestPath"), classPath = cmd.getOptionValue("classPath"),
					nbrCommit = cmd.getOptionValue("nbrCommit"), packages = cmd.getOptionValue("packages");

			// Default values of sourceMainPath and sourceTestPath
			if (sourceMainPath == null) {
				sourceMainPath = Constants.srcMainJava;
			}
			if (sourceTestPath == null) {
				sourceTestPath = Constants.srcTestJava;
			}

			downloadVersionsAndRunSpoonTransformations(Integer.parseInt(nbrCommit), projectPath, classPath,
					sourceMainPath, sourceTestPath);

			runAllTests(packages.trim().split(File.pathSeparator), classPath);
		} catch (ParseException exp) {
			System.err.println("Parsing failed.  Reason: " + exp.getMessage());
		}
	}

	public static void downloadVersionsAndRunSpoonTransformations(int nbr, String projectPath, String classPath,
			String sourceMainPath, String sourceTestPath) {

		System.out.println(new StringBuilder().append("\nnumber of commits = ").append(nbr).append("\nprojectPath = ")
				.append(projectPath).append("\nsourceMainPath = ").append(sourceMainPath).append("\nclassPath = ")
				.append(classPath).append("\nexecute git to generate ").append(nbr).append(" folders").toString());

		// Download versions
		//CmdTools.executeSH(jouvenceDir, jouvenceFile, projectPath, String.valueOf(nbr), jouvenceBranch);

		FunctionsUtils.processCleanFiles(projectPath);

		for (int i = 0; i < nbr; i++) {
			snipers.add(new VersionSniper(projectPath, sourceMainPath, classPath, i));
		}

		System.out.println(new StringBuilder().append("Spoon the last commit as the new project template = ")
				.append(projectPath).append(File.separator).append(sourceMainPath).append("\nwith classPath = ")
				.append(classPath).toString());

		// Run Spoon transformations
		Launcher spoon = new Launcher();
		spoon.addProcessor(new MethodVersioningProcessor(snipers));
		spoon.run(new String[] { "-i",
				new StringBuilder().append(projectPath).append(File.separator).append(sourceMainPath).toString(),
				"--source-classpath", classPath, "-d", targetMainPath, "--compile" });
		spoon = new Launcher();
		spoon.run(new String[] { "-i",
				new StringBuilder().append(projectPath).append(File.separator).append(sourceTestPath).toString(),
				"--source-classpath",
				new StringBuilder().append(classPath).append(File.pathSeparatorChar).append(targetMainPath).toString(),
				"-d", targetTestPath, "--compile" });

		FunctionsUtils.processResourcesFolders(projectPath, sourceMainPath, sourceTestPath, targetMainPath,
				targetTestPath);
		FunctionsUtils.putAllPropertiesFilesInTargetFolders(projectPath, sourceMainPath, sourceTestPath, targetMainPath,
				targetTestPath);
	}

	public static void runAllTests(String[] packages, String classPath) throws Exception {
		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());
		String resultat = null;
		int nbTestsKO = Integer.MAX_VALUE;

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.setScanners(new SubTypesScanner(
						false /* don't exclude Object.class */), new ResourcesScanner())
				.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0]))));

		Set<Class<?>> allClasses = reflections.getSubTypesOf(Object.class);
		boolean take = false;
		String currentPackage = null;
		for (Class<?> c : allClasses) {
			take = false;
			currentPackage = c.getName();
			for (String p : packages) {
				if (currentPackage.contains(p)) {
					take = true;
					break;
				}
			}
			if (take) {
				if (c.getName().endsWith("Test")) {
					testsClasses.add(c);
				} else {
					projectClasses.add(c);
				}
			}
		}
		System.out.println(testsClasses.size());
		System.out.println(projectClasses.size());

		JUnitCore jUnit = new JUnitCore();
		Class[] classes = new Class[testsClasses.size()];
		classes = testsClasses.toArray(classes);
		Result result = null;
		Field vmaxfield = null;
		LinkedList<Field> fields_version = new LinkedList<Field>();
		Field allFields[] = null;

		for (Class<?> c : projectClasses) {
			System.out.println("MODIF CLASS " + c.getName());
			allFields = c.getDeclaredFields();
			for (Field field : allFields) {
				field.setAccessible(true);
				if (field.getName().endsWith("_version")) {
					fields_version.add(field);
				}
			}
			for (Field vfield : fields_version) {
				try {
					vmaxfield = c.getDeclaredField(vfield.getName() + "_max");
					vmaxfield.setAccessible(true);

					System.out.println("MODIF METHOD ATTRIBUTE " + vfield.getName());
					while (((Integer) (vfield.get(Integer.class)))
							.intValue() <= ((Integer) (vmaxfield.get(Integer.class))).intValue()) {

						result = jUnit.run(classes);
						System.out.println(new StringBuilder().append("runCount = ").append(result.getRunCount())
								.append("\nFailureCount = ").append(result.getFailureCount()).append("\n"));

						if (nbTestsKO > result.getFailureCount()) {
							nbTestsKO = result.getFailureCount();
							resultat = new StringBuilder()
									.append("Afin d'obtenir le maximum de tests OK il faut que la valeur de l'attribut ")
									.append(vfield.getName()).append(" soit égale à ")
									.append(((Integer) (vfield.get(Integer.class))).intValue()).append(" dans la classe ")
									.append(c.getSimpleName()).append(".\nLe nombre d'échecs est de ").append(nbTestsKO)
									.append(".\nVous trouverez en premier le nom de la méthode, puis le type des paramètres. Ne pas tenir compte de la dernière valeur qui est 'version'.\nLe caractère 0 peut être l'un des caractères suivants [ ] , . < > ? ou un espace.")
									.toString();
						}

						vfield.set(Integer.class, new Integer(((Integer) (vfield.get(Integer.class))).intValue() + 1));
					}
					vfield.set(Integer.class, new Integer(0));
				} catch (NoSuchFieldException e) {
					// ne rien faire
				}
			}
		}
		System.out.println("---- End of program ----");
		System.out.println(resultat);
	}
}
