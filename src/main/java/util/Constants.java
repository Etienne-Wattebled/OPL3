package util;

import java.io.File;

public class Constants {
	
	public static final String srcMainJava = new StringBuilder().append("src").append(File.separator).append("main")
			.append(File.separator).append("java").toString();
	
	public static final String srcTestJava = new StringBuilder().append("src").append(File.separator).append("test")
			.append(File.separator).append("java").toString();
	
	public static final String srcMainResources = new StringBuilder().append("src").append(File.separator).append("main")
			.append(File.separator).append("resources").toString();
	
	public static final String srcTestResources = new StringBuilder().append("src").append(File.separator).append("test")
			.append(File.separator).append("resources").toString();

	public static final String targetMainPath = new StringBuilder().append("target").append(File.separator)
			.append("classes").toString();
	
	public static final String targetTestPath = new StringBuilder().append("target").append(File.separator)
			.append("test-classes").toString();
}
