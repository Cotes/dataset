package com.ast.dataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

import com.ast.dataset.generator.RealisticDatasetGenerator;
import com.ast.dataset.models.SimpleFile;
import com.ast.dataset.util.Config;
import com.ast.dataset.util.Utils;

public class RealisticDatasetTest {

	public static void main(String[] args) throws IOException {
		
		List<SimpleFile> files = getSimpleFilesList();
		Config.loadProperties("test.properties");
		RealisticDatasetGenerator generator = new RealisticDatasetGenerator(10, 50);
		generator.generateDataset("test.txt", files);

	}
	
	public static List<SimpleFile> getSimpleFilesList() {
		
		List<SimpleFile> files = new ArrayList<SimpleFile>();
		int i = 0;
		while (i < 1000) {
			String filename = RandomStringUtils.randomAlphabetic(7) + ".gz";
			SimpleFile file = new SimpleFile(filename, getFileSize());
			files.add(file);
			i++;
		}
		
		return files;
	}
	
	public static int getFileSize() {
		
		Random random = new Random();
		int value = random.nextInt(100);
		
		int minSize=0, maxSize=0;	// In bytes
		if (Utils.isBetween(value, 0, 40)) {
			minSize = 1;
			maxSize = 4*1024;
		} else if (Utils.isBetween(value, 40, 50)) {
			minSize = 4*1024;
			maxSize = 8*1024;
		} else if (Utils.isBetween(value, 50, 58)) {
			minSize = 8*1024;
			maxSize = 16*1024;
		} else if (Utils.isBetween(value, 58, 66)) {
			minSize = 16*1024;
			maxSize = 32*1024;
		} else if (Utils.isBetween(value, 66, 72)) {
			minSize = 32*1024;
			maxSize = 64*1024;
		} else if (Utils.isBetween(value, 72, 78)) {
			minSize = 64*1024;
			maxSize = 128*1024;
		} else if (Utils.isBetween(value, 78, 81)) {
			minSize = 128*1024;
			maxSize = 256*1024;
		} else if (Utils.isBetween(value, 81, 87)) {
			minSize = 256*1024;
			maxSize = 512*1024;
		} else if (Utils.isBetween(value, 87, 90)) {
			minSize = 512*1024;
			maxSize = 1024*1024;
		} else if (Utils.isBetween(value, 90, 94)) {
			minSize = 1024*1024;
			maxSize = 2048*1024;
		} else if (Utils.isBetween(value, 94, 97)) {
			minSize = 2048*1024;
			maxSize = 4096*1024;
		} else if (Utils.isBetween(value, 97, 98)) {
			minSize = 4096*1024;
			maxSize = 8192*1024;
		} else if (Utils.isBetween(value, 98, 99)) {
			minSize = 8192*1024;
			maxSize = 16384*1024;
		} else if (Utils.isBetween(value, 99, 100)) {
			minSize = 16384*1024;
			maxSize = 262144*1024;
		} 
		
		int size = minSize + random.nextInt(maxSize-minSize+1);
		return size;
	}

}
