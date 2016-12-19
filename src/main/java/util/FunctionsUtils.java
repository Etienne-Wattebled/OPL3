package util;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.io.FileUtils;

public class FunctionsUtils {
	/**
	 * Supprime les fichiers ou dossiers bloquants la compilation et inutile (exemple: fichier package-info.java de la javadoc)
	 * @param projectPath
	 */
	public static void processCleanFiles(String projectPath) {
		Queue<File> files = new LinkedList<File>();
		File subFiles[] = null;
		files.add(new File(projectPath));
		
		File file = null;
		while ((file = files.poll()) != null) {
			if (file.isDirectory()) {
				subFiles = file.listFiles();
				if (subFiles != null) {
					for (File subFile : subFiles) {
						files.add(subFile);
					}
				}
			} else if (file.getName().equals("package-info.java")) {
				file.delete();
			}
		}
	}
	/**
	 * Remet en place les dossiers ressources
	 */
	public static void processResourcesFolders(
			String projectPath, 
			String sourceMainPath, String sourceTestPath,
			String targetMainPath, String targetTestPath) {
		
		File sourceMain = new File(projectPath+File.separator+sourceMainPath);
		File sourceTest = new File(projectPath+File.separator+sourceTestPath);
		sourceMain = sourceMain.getParentFile();
		sourceTest = sourceTest.getParentFile();
		
		File targetMain = new File(targetMainPath);
		File targetTest = new File(targetTestPath);
		
		File subFiles[] = null;
		while (!sourceMain.equals(sourceTest)) {
			subFiles = sourceMain.listFiles();
			for (File f : subFiles) {
				if (f.isDirectory() && f.getName().equals("resources")) {
					putAllFilesInFolder(f.listFiles(),targetMain);

				}
			}
			subFiles = sourceTest.listFiles();
			for (File f : subFiles) {
				if (f.isDirectory() && f.getName().equals("resources")) {
					putAllFilesInFolder(f.listFiles(),targetTest);
				}
			}
			sourceMain = sourceMain.getParentFile();
			sourceTest = sourceTest.getParentFile();
		}
	}
	private static void putAllFilesInFolder(File files[], File targetDir) {
		for (File f : files) {
			if (f.isDirectory()) {
				try {
					FileUtils.copyDirectoryToDirectory(f,targetDir);
				} catch (IOException e) {
					System.err.println("Erreur déplacement d'un dossier présent dans resources");
					e.printStackTrace();
				}
			} else {
				try {
					FileUtils.copyFileToDirectory(f,targetDir);
				} catch (IOException e) {
					System.err.println("Erreur déplacement d'un fichier présent dans resources");
					e.printStackTrace();
				}
			}
		}
	}
	public static void putAllPropertiesFilesInTargetFolders(
			String projectPath, 
			String sourceMainPath, String sourceTestPath, 
			String targetMainPath, String targetTestPath) {
		String pps = projectPath+File.separator;
		
		File sourceMain = new File(pps+sourceMainPath),
				sourceTest = new File(pps+sourceTestPath),
				targetMain = new File(targetMainPath),
				targetTest = new File(targetTestPath);
		File output = null;
		
		Queue<File> files = new LinkedList<File>();
		files.add(sourceMain);
		files.add(sourceTest);
		
		File file = null;
		File subFiles[] = null;
		String name = null;
		while ((file = files.poll()) != null) {
			name = file.getName();
			if (file.isDirectory()) {
				subFiles = file.listFiles();
				for (File f : subFiles) {
					files.add(f);
				}
			} else {
				if (name.endsWith(".properties")) {
					if (name.contains(sourceMainPath)) {
						output = new File(targetMain+File.separator+sourceMain.toURI().relativize(file.toURI()));
					} else {
						output = new File(targetTest+File.separator+sourceMain.toURI().relativize(file.toURI()));
					}
					try {
						FileUtils.copyFileToDirectory(file,output.getParentFile());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
