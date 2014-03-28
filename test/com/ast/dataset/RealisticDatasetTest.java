package com.ast.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import com.ast.dataset.generator.RealisticDatasetGenerator;
import com.ast.dataset.models.SimpleFile;
import com.ast.dataset.util.Config;

public class RealisticDatasetTest {

	public static void main(String[] args) throws IOException {
		
		List<SimpleFile> files = getSimpleFilesList();
		Config.loadProperties("test.properties");
		RealisticDatasetGenerator generator = new RealisticDatasetGenerator(10, 50);
		generator.generateDataset("test.txt", files);

	}
	
	public static List<SimpleFile> getSimpleFilesList() {
		
		List<SimpleFile> files = new ArrayList<SimpleFile>();
		Random random = new Random();
		int i = 0;
		while (i < 1000) {
			int size = random.nextInt(4096);
			String filename = RandomStringUtils.randomAlphabetic(7) + ".gz";
			SimpleFile file = new SimpleFile(filename, size + 512);
			files.add(file);
			i++;
		}
		
		return files;
	}

}
