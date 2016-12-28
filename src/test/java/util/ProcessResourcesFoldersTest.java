package util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.Constants;
import util.FunctionsUtils;

public class ProcessResourcesFoldersTest {
	
	private File testFolder = new File(new StringBuilder().append("resources").append(File.separator)
			.append("testProcessResourcesFolders").toString());
	private File testFolderSrcTestResources = new File(new StringBuilder().append(testFolder).append(File.separator)
			.append(Constants.srcTestResources).toString());
	private File testFolderSrcMainResources = new File(new StringBuilder().append(testFolder).append(File.separator)
			.append(Constants.srcMainResources).toString());

	// To deplace :
	private File fileResourcesMain = new File(new StringBuilder().append(testFolderSrcMainResources)
			.append(File.separator).append("fileResourcesMain.txt").toString());

	private File folderResourcesMain = new File(new StringBuilder().append(testFolderSrcMainResources)
			.append(File.separator).append("folderResourcesMain").toString());

	private File fileResourcesTest = new File(new StringBuilder().append(testFolderSrcTestResources)
			.append(File.separator).append("fileResourcesTest.txt").toString());

	private File folderResourcesTest = new File(new StringBuilder().append(testFolderSrcTestResources)
			.append(File.separator).append("folderResourcesTest").toString());

	
	// Outputs (expected) :
	private File fileMain = new File(new StringBuilder().append(Constants.targetMainPath).append(File.separator)
			.append(fileResourcesMain.getName()).toString());
	
	private File fileTest = new File(new StringBuilder().append(Constants.targetTestPath).append(File.separator)
			.append(fileResourcesTest.getName()).toString());
	
	private File folderMain = new File(new StringBuilder().append(Constants.targetMainPath).append(File.separator)
			.append(folderResourcesMain.getName()).toString());
	
	private File folderTest = new File(new StringBuilder().append(Constants.targetTestPath).append(File.separator)
			.append(folderResourcesTest.getName()).toString());
	
	@Before
	public void init() throws IOException {
		testFolder.mkdirs();
		testFolderSrcMainResources.mkdirs();
		testFolderSrcTestResources.mkdirs();
		fileResourcesMain.createNewFile();
		fileResourcesTest.createNewFile();
		folderResourcesMain.mkdirs();
		folderResourcesTest.mkdirs();
	}

	@After
	public void clean() throws Exception {
		FileUtils.deleteDirectory(testFolder);
		FileUtils.deleteDirectory(testFolderSrcMainResources);
		FileUtils.deleteDirectory(testFolderSrcTestResources);
		fileMain.delete();
		fileTest.delete();
		FileUtils.deleteDirectory(folderMain);
		FileUtils.deleteDirectory(folderTest);
	}

	@Test
	public void testProcessResourcesFolders() throws Exception {
		
		FunctionsUtils.processResourcesFolders(testFolder.toString(), Constants.srcMainJava, Constants.srcTestJava,
				Constants.targetMainPath, Constants.targetTestPath);
		
		assertTrue(fileMain.exists());
		assertTrue(fileTest.exists());
		assertTrue(folderMain.exists());
		assertTrue(folderTest.exists());
	}
}
