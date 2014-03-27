package com.ast.dataset.generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.ast.dataset.util.FilesOp;

public class TreeFSGenerator {
	
	private Logger logger = Logger.getLogger(TreeFSGenerator.class.getName());

	private String rootFolder;
	private int levels;
	private int foldersPerLevel;
	private boolean foldersCreated;
	private Random random;
	private ArrayList<String> folders;
	private HashMap<String, Integer> files;
	private int totalData;
	
	public TreeFSGenerator(String rootFolder, int levels, int foldersPerLevel, int totalData) {
		this.rootFolder = rootFolder;
		if (!this.rootFolder.endsWith("/")) {
			this.rootFolder += "/";
		}
		
		this.levels = levels;
		this.foldersPerLevel = foldersPerLevel;
		this.totalData = totalData;
		
		this.foldersCreated = false;
		this.random = new Random();
		this.random.setSeed(System.currentTimeMillis());
		this.folders = new ArrayList<String>();
		this.files = new HashMap<String, Integer>();
		
	}
	
	public void createFolders(){
		logger.debug("Creating FS folders.");
		this.folders.add(this.rootFolder.substring(this.rootFolder.length()-1));
		createFSFolders(1, this.rootFolder);
		logger.debug("All folders created.");
		this.foldersCreated = true;
	}
	
	public void createFSFolders(int currentLevel, String currentPath){
		
		if (currentLevel > this.levels) {
			return;
		}
		
		for(int i=0; i<this.foldersPerLevel; i++) {
			try {
				
				String folderName = "l"+currentLevel+"-c"+(i+1);
				FileUtils.forceMkdir(new File(currentPath+folderName));
				String newPath = currentPath +folderName;
				
				//Save new folder path
				this.folders.add(newPath.substring(this.rootFolder.length()-1));
				createFSFolders(currentLevel+1, newPath+"/");
				
			} catch (IOException e) {
				logger.error("Error creating a folder.");
				e.printStackTrace();
			}
		}
	}
	
	/* This function assumes:
	 * 	1: totalData > maxFileSize > minFileSize
	 * 	2: totalData > 2*maxFileSize
	 */
	public HashMap<String, Integer> fillFoldersWithFiles(int minFileSize, int maxFileSize) throws FoldersNotCreated, IOException{
		
		if (! this.foldersCreated) {
			throw new FoldersNotCreated();
		}
		
		FileWriter fstream = new FileWriter("files_created.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		
		int freeSpace = totalData;
		boolean fsFilled = false;
		int totalFilled = 0;
		int totalFiles = 0;
		
		while (!fsFilled) {
			
			int newFileSize = this.random.nextInt(maxFileSize - minFileSize +1) + minFileSize;
			freeSpace -= newFileSize;
			totalFilled += newFileSize;
			totalFiles++;
			
			boolean created = this.createFile(newFileSize, totalFiles, out);
			
			if (!created) {
				totalFiles--;
				freeSpace += newFileSize;
				totalFilled -= newFileSize;
			}
			
			if (maxFileSize*2 > freeSpace) {
				logger.debug("Total data almost done.");
				fsFilled = true;
			}
		}
		
		logger.debug("Create two files of "+(freeSpace/2));
		this.createFile(freeSpace/2, totalFiles+1, out);
		this.createFile(freeSpace/2, totalFiles+2, out);
		totalFilled += freeSpace;
		logger.debug("Created "+totalFilled+" kbytes from the total "+totalData);
		totalFiles += 2;
		logger.debug("Total files created: "+totalFiles);
		
		out.close();
		fstream.close();
		
		return this.files;
		
	}

	private boolean createFile(int newFileSize, int fileNum, BufferedWriter out) {
		
		int numFolders = this.folders.size();
		String home = System.getProperty("user.home");
		String tmpPath = home+"/tmpFiles";
		
		int folderIndex = this.random.nextInt(numFolders);
		String path = this.folders.get(folderIndex);
		String fileName = "file"+fileNum+".dat";
		
		boolean success = false;
		
		try {
			String completePath = this.rootFolder+path.substring(1);
			FileUtils.forceMkdir(new File(tmpPath));
			FilesOp.generateRandomFile(tmpPath+"/"+fileName, newFileSize);
			FilesOp.moveFile(tmpPath+"/"+fileName, completePath+"/"+fileName);
			
			logger.debug("File "+completePath+"/"+fileName+" created of size -> "+newFileSize);
			this.files.put(path+"/"+fileName, newFileSize);
			out.write(path+"/"+fileName+" "+newFileSize+"\n");
			
			success = true;
		} catch (IOException e) {
			logger.error("Error creating file "+path+"/"+fileNum+" of size "+newFileSize);
			e.printStackTrace();
		}
		
		return success;
	}
	
	public HashMap<String, Integer> readFilesFromFile(String path) throws IOException {
		
		logger.debug("Reading files created.");
        
        BufferedReader br = new BufferedReader( new FileReader( path ) );
        String line;
        
        while ((line = br.readLine()) != null ) {
        	
        	String lineSplitted[] = line.split(" ");
        	this.files.put(lineSplitted[0], Integer.parseInt(lineSplitted[1]));
        }
        
        br.close();
        
        return this.files;
		
	}

	public String getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(String rootFolder) {
		this.rootFolder = rootFolder;
	}

	public int getLevels() {
		return levels;
	}

	public void setLevels(int levels) {
		this.levels = levels;
	}

	public int getFolderPerLevel() {
		return foldersPerLevel;
	}

	public void setFolderPerLevel(int folderPerLevel) {
		this.foldersPerLevel = folderPerLevel;
	}
	
}
