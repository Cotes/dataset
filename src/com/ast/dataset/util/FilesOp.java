package com.ast.dataset.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class FilesOp {

	public static void generateRandomFile(String path, int size) throws IOException{
		
		size *= 1024;
		Process p = Runtime.getRuntime().exec("dd if=/dev/urandom of="+path+" count=1 bs="+size);
		boolean finish = false;
		while (!finish) {
			try {
				p.exitValue();
				finish = true;
			} catch (IllegalThreadStateException ex) {
				
			}
		}
	}
	
	public static void moveFile(String src, String dest) throws IOException {
		
		File srcFile = new File(src);
		File destFile = new File(dest);
		
		FileUtils.moveFile(srcFile, destFile);
		
	}
	
	public static void removeFile(String removePath) {
		
		File file = new File(removePath);
		FileUtils.deleteQuietly(file);
	}
	
}
