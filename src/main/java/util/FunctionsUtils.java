package util;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class FunctionsUtils {
	public static void cleanFiles(String projectPath) {
		Queue<File> files = new LinkedList<File>();
		File f = new File(projectPath);
		
		if (!f.exists()) {
			return;
		}
		File parent = f.getParentFile();
		File subFiles[] = parent.listFiles();
		String projectName = f.getName();
		
		for (File file : subFiles) {
			if (file.isDirectory() && (file.getName().startsWith(projectName))) {
				files.add(file);
			}
		}
		
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
}
