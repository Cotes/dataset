package com.ast.dataset;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ast.dataset.generator.FoldersNotCreated;
import com.ast.dataset.generator.TreeFSGenerator;
import com.ast.dataset.util.Config;
import com.ast.dataset.util.Constants;

/**
 * 
 * @author Sergi Toda <sergi.toda@estudiants.urv.cat>
 * 
 */

public class MyTest {
	@BeforeClass
	public static void init() {
		URL configFileResource = Dataset.class.getResource("/com/ast/dataset/resources/log4j.xml");
		DOMConfigurator.configure(configFileResource);
	}

	@Test
	public void testFSAddsSlash() throws IOException {
		Config.loadProperties("test.properties");
		assertTrue(Config.getFSPath().endsWith("/"));
	}

	@Test
	public void testTreeFSGenerator() throws IOException, FoldersNotCreated {
		String path = "TestTreeFSGenerator/";
		File directory = new File(path);
		TreeFSGenerator fsGenerator = new TreeFSGenerator(path, 0, 0, Constants.DEFAULT_TREE_TOTAL_DATA);

		// Create fs
		Map<String, Integer> map = generateFS(fsGenerator, directory, path);

		// Destroy and check
		int numFiles = directory.listFiles().length;

		FileUtils.forceDelete(directory);

		assertEquals(numFiles, map.keySet().size() + 1);
	}

	@Test
	public void testReadFileFromPath() throws IOException, FoldersNotCreated {
		String path = "TestTreeFSGenerator/";
		File directory = new File(path);
		TreeFSGenerator fsGenerator = new TreeFSGenerator(path, 0, 0, Constants.DEFAULT_TREE_TOTAL_DATA);

		// Create fs
		Map<String, Integer> writeMap = generateFS(fsGenerator, directory, path);

		Map<String, Integer> readMap = fsGenerator.readFilesFromFolder(path);

		// Destroy folder
		FileUtils.forceDelete(directory);

		assertEquals(writeMap.keySet().size(), readMap.keySet().size());

	}

	@Test
	public void testCopyDirectory() throws IOException, FoldersNotCreated {
		String path = "TestTreeFSGenerator/";
		String copy = "TestCopy/";
		File directory = new File(path);
		TreeFSGenerator fsGenerator = new TreeFSGenerator(path, 0, 0, Constants.DEFAULT_TREE_TOTAL_DATA);

		// Create fs
		generateFS(fsGenerator, directory, path);

		// Copy it to
		fsGenerator.copyFSFolder(copy);

		// Check both folders has the same
		File cp = new File(copy);

		// length - 1 to remove files_generated.txt
		assertEquals(directory.list().length - 1, cp.list().length);

		FileUtils.forceDelete(directory);
		FileUtils.forceDelete(cp);
	}
	
	@Test
	public void testCopyDirectory2() throws IOException, FoldersNotCreated{
		String path = "TestTreeFSGenerator/";
		String copy = "TestCopy/";
		File directory = new File(path);
		TreeFSGenerator fsGenerator = new TreeFSGenerator(path, 2, 0, Constants.DEFAULT_TREE_TOTAL_DATA);
		
		// Create fs
		generateFS(fsGenerator, directory, path);
		
		// Copy it to
		fsGenerator.copyFSFolder(copy);
		
		// Check both folders has the same
		File cp = new File(copy);
		
		// length - 1 to remove files_generated.txt
		assertEquals(directory.list().length - 1, cp.list().length);
		
		FileUtils.forceDelete(directory);
		FileUtils.forceDelete(cp);
	}
	
	private Map<String, Integer> generateFS(TreeFSGenerator fsGenerator, File directory, String path) throws IOException, FoldersNotCreated{
		FileUtils.forceMkdir(directory);
		fsGenerator.createFolders();
		return fsGenerator.fillFoldersWithFiles(Constants.DEFAULT_MIN_SIZE, Constants.DEFAULT_MAX_SIZE);
	}
}
