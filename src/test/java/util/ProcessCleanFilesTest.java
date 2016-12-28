package util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.FunctionsUtils;

public class ProcessCleanFilesTest {
	private File testFolder = new File(
			new StringBuilder().append("resources").append(File.separator).append("testProcessCleanFiles").toString());

	private File oneFolderInTestFolder = new File(new StringBuilder().append(testFolder).append(File.separator)
			.append(File.separator).append("oneFolder").toString());;

	@Before
	public void createTestFolders() {
		testFolder.mkdirs();
		oneFolderInTestFolder.mkdirs();
	}

	@After
	public void removeTestFolder() throws Exception {
		FileUtils.deleteDirectory(testFolder);
		FileUtils.deleteDirectory(oneFolderInTestFolder);
	}

	@Test
	public void testPackageInfoInRootFolder() throws Exception {
		File javaPackageInfoFile = new File(
				new StringBuilder().append(testFolder).append(File.separator).append("package-info.java").toString());
		javaPackageInfoFile.createNewFile();

		File javaFile = new File(
				new StringBuilder().append(testFolder).append(File.separator).append("OneJavaFile.java").toString());
		javaFile.createNewFile();

		FunctionsUtils.processCleanFiles(testFolder.toString());
		
		assertFalse(javaPackageInfoFile.exists()); // Deleted
		assertTrue(javaFile.exists());

	}
	
	@Test
	public void testPackageInfoNotInRootFolder() throws Exception {
		File javaPackageInfoFile = new File(
				new StringBuilder().append(oneFolderInTestFolder).append(File.separator).append("package-info.java").toString());
		javaPackageInfoFile.createNewFile();

		File javaFile = new File(
				new StringBuilder().append(oneFolderInTestFolder).append(File.separator).append("OneJavaFile.java").toString());
		javaFile.createNewFile();
		
		FunctionsUtils.processCleanFiles(testFolder.toString());
		
		assertFalse(javaPackageInfoFile.exists()); // Deleted
		assertTrue(javaFile.exists());

	}
}
