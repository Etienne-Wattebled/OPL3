package reparator;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;

import reparator.App;
import util.CmdTools;

public class DownloadVersionsTest {
	// Si les tests échouent, il y a de forte chance que le projet demoproject n'est pas présent dans resources ou n'est plus lié à git
	
	private File demoproject = new File(new StringBuilder().append("resources").append(File.separator).append("demoproject").toString());
	private File demoproject_0 = new File(demoproject.getPath() + "_0");
	private File demoproject_1 = new File(demoproject.getPath() + "_1");
	private File demoproject_2 = new File(demoproject.getPath() + "_2");
	
	private int demoproject_0_size = 28890;
	private int demoproject_1_size = 28742;
	private int demoproject_2_size = 28463;
	
	@After
	public void removeAll() throws Exception {
		FileUtils.deleteDirectory(demoproject_0);
		FileUtils.deleteDirectory(demoproject_1);
		FileUtils.deleteDirectory(demoproject_2);
	}

	@Test
	public void testDownload2() throws Exception {
		CmdTools.executeSH(App.jouvenceDir, App.jouvenceFile, demoproject.toString(), "2",
				App.jouvenceBranch);
		assertTrue(demoproject_0.exists());
		assertEquals(FileUtils.sizeOfDirectory(demoproject_0),demoproject_0_size);
		assertTrue(demoproject_1.exists());
		assertEquals(FileUtils.sizeOfDirectory(demoproject_1),demoproject_1_size);
		assertFalse(demoproject_2.exists()); // NO
	}

	@Test
	public void testDownload3() {
		CmdTools.executeSH(App.jouvenceDir, App.jouvenceFile, demoproject.toString(), "3",
				App.jouvenceBranch);
		assertTrue(demoproject_0.exists());
		assertEquals(FileUtils.sizeOfDirectory(demoproject_0),demoproject_0_size);
		assertTrue(demoproject_1.exists());
		assertEquals(FileUtils.sizeOfDirectory(demoproject_1),demoproject_1_size);
		assertTrue(demoproject_2.exists());
		assertEquals(FileUtils.sizeOfDirectory(demoproject_2),demoproject_2_size);
	}
}
