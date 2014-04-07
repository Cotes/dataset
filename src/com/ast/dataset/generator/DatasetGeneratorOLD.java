package com.ast.dataset.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.ast.dataset.actions.Add;
import com.ast.dataset.actions.ByteRange;
import com.ast.dataset.actions.Remove;
import com.ast.dataset.actions.Update;
import com.ast.dataset.util.Config;
import com.ast.dataset.util.FilesOp;

public class DatasetGeneratorOLD {

	private Logger logger = Logger.getLogger(DatasetGenerator.class.getName());

	public enum ModificationPart {
		B, E, M, BE, BM, ME, BEM
	};

	public enum Operation {
		ADD, UPDATE, REMOVE
	};

	private int totalData; // in kbytes
	private int modificationPercentage; // 0..100
	private Random random;
	private int minWaitTime;
	private int maxWaitTime;
	private int modificationSize;
	private int totalTime;

	public DatasetGeneratorOLD(int totalData) {
		this.totalData = totalData;
		this.random = new Random();
		this.minWaitTime = Config.getMinWaitTime();
		this.maxWaitTime = Config.getMaxWaitTime();
		this.modificationSize = Config.getMaxModificationSize();
	}

	public DatasetGeneratorOLD(int totalData, int modificationPercentage) {
		this(totalData);
		// this.totalData = totalData;
		this.modificationPercentage = modificationPercentage;
		/*
		 * this.random = new Random(); this.minWaitTime =
		 * Config.getMinWaitTime(); this.maxWaitTime = Config.getMaxWaitTime();
		 * this.modificationSize = Config.getModificationSize();
		 */
	}

	public void generateOnlyUpdates(String datasetFile, HashMap<String, Integer> files) throws IOException {

		FileWriter fstream = new FileWriter(datasetFile);
		BufferedWriter out = new BufferedWriter(fstream);

		totalTime = 0;
		int dataToModify = (int) (this.totalData * ((float) this.modificationPercentage / 100));

		Object[] filesPath = files.keySet().toArray();
		int numFiles = filesPath.length;

		while (dataToModify > 0) {
			Object fileToModify = filesPath[random.nextInt(numFiles)];
			int fileSize = files.get(fileToModify);
			int dataModified = this.generateUpdate(out, fileToModify, fileSize);
			dataToModify -= dataModified;
		}

		out.close();
		fstream.close();

	}

	private int generateUpdate(BufferedWriter out, Object fileToModify, int fileSize) throws IOException {

		ModificationPart modificationPart = this.getModificationPart();

		int bs1, bs2, bs3, dataModified = 0;

		switch (modificationPart) {
		case B:
		case E:
		case M:
			bs1 = this.getRealisticByteModification(fileSize, modificationPart);
			dataModified = this.modifyFile(out, fileToModify, fileSize, bs1);
			break;
		case BE:
			bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
			bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
			dataModified = this.modifyFile(out, fileToModify, fileSize, bs1, bs2);
			break;
		case BM:
			bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
			bs2 = this.getRealisticByteModification(fileSize, ModificationPart.M);
			dataModified = this.modifyFile(out, fileToModify, fileSize, bs1, bs2);
			break;
		case ME:
			bs1 = this.getRealisticByteModification(fileSize, ModificationPart.M);
			bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
			dataModified = this.modifyFile(out, fileToModify, fileSize, bs1, bs2);
			break;
		case BEM:
			bs1 = this.getRealisticByteModification(fileSize, ModificationPart.B);
			bs2 = this.getRealisticByteModification(fileSize, ModificationPart.E);
			bs3 = this.getRealisticByteModification(fileSize, ModificationPart.M);
			dataModified = this.modifyFile(out, fileToModify, fileSize, bs1, bs2, bs3);
			break;
		}

		return dataModified;
	}

	private int modifyFile(BufferedWriter out, Object fileToModify, int fileSize, int... bytesStart) throws IOException {

		int dataModified = 0;
		ArrayList<ByteRange> ranges = new ArrayList<ByteRange>();

		for (int byteStart : bytesStart) {
			// int byteStart = this.getRealisticByteModification(fileSize);
			int byteEnd = byteStart + this.modificationSize; // Default 40 KB

			if (byteEnd > fileSize) {
				byteEnd = fileSize;
			}

			dataModified = byteEnd - byteStart;
			// This is not tested...
			/*
			 * if (dataModified > dataToModify) { byteEnd -= (dataModified -
			 * dataToModify); }
			 */

			ranges.add(new ByteRange(byteStart, byteEnd));

		}

		int time = this.getWaitTime();
		totalTime += time;

		Update update = new Update(totalTime, (String) fileToModify, ranges);

		out.write(update.toString());

		return dataModified;

	}

	private ModificationPart getModificationPart() {
		ModificationPart part;
		int randomPart = this.random.nextInt(100);

		if (randomPart < 38) {
			// Beginning
			part = ModificationPart.B;
		} else if (randomPart >= 38 && randomPart < 41) {
			// End
			part = ModificationPart.E;
		} else if (randomPart >= 41 && randomPart < 49) {
			// Middle
			part = ModificationPart.M;
		} else if (randomPart >= 49 && randomPart < 59) {
			// Beginning - End
			part = ModificationPart.BE;
		} else if (randomPart >= 59 && randomPart < 70) {
			// Beginning - Middle
			part = ModificationPart.BM;
		} else if (randomPart >= 70 && randomPart < 71) {
			// Middle - End
			part = ModificationPart.ME;
		} else {
			// BEM
			part = ModificationPart.BEM;
		}
		return part;
	}

	private int getRealisticByteModification(int fileSize, ModificationPart part) {

		int filePart = fileSize / 3;
		int multi;

		switch (part) {
		case B:
			multi = 0;
			break;
		case E:
			multi = 2;
			break;
		case M:
			multi = 1;
			break;
		default:
			multi = 2;
			break;
		}

		int byteStart = random.nextInt(filePart);
		byteStart = multi * filePart + byteStart;

		return byteStart;
	}

	public void generateGenericDataset(String datasetFile, HashMap<String, Integer> files, int numOperations) throws IOException {

		FileWriter fstream = new FileWriter(datasetFile);
		BufferedWriter out = new BufferedWriter(fstream);

		int fileNum = files.values().size() + 1;
		int totalFiles = fileNum - 1;

		Object[] filesPath = files.keySet().toArray();
		HashSet<String> folders = this.getFoldersFromFilesList(filesPath);
		int numFolders = folders.size();

		ArrayList<Integer> operations = getOperationsList(numOperations);
		totalTime = 0;

		for (int opNum : operations) {

			switch (opNum % 3) {
			case 0:
				// ADD
				logger.debug("ADD");
				int newFileSize = this.random.nextInt(Config.getMaxSize() - Config.getMinSize() + 1) + Config.getMinSize();
				boolean created = createFile(newFileSize, fileNum, folders);

				// Select folder to copy the new file
				int folderIndex = this.random.nextInt(numFolders);
				Object[] foldersList = folders.toArray();
				String path = (String) foldersList[folderIndex];
				// TODO bug bug bug? fileNum incremented before its name is
				// saved
				// if (created) {
				// fileNum++;
				// totalFiles++;
				// String fileName = "file"+fileNum+".dat";
				// int time = this.getWaitTime();
				// totalTime += time;
				// Add addAction = new Add( totalTime, "/"+fileName, path );
				// files.put( path+fileName, newFileSize );
				// filesPath = files.keySet().toArray();
				// out.write( addAction.toString() );
				// }
				
				if (created) {
					String fileName = "file" + fileNum + ".dat";
					fileNum++;
					totalFiles++;
					totalTime += this.getWaitTime();

					Add addAction = new Add(totalTime, "/" + fileName, path);

					files.put(path + fileName, newFileSize);
					filesPath = files.keySet().toArray();
					out.write(addAction.toString());
				}

				break;
			case 1:
				// UPDATE
				logger.debug("UPDATE");
				Object fileToModify = filesPath[random.nextInt(totalFiles)];
				int fileSize = files.get(fileToModify);
				this.generateUpdate(out, fileToModify, fileSize);
				break;
			case 2:
				// REMOVE
				logger.debug("REMOVE");
				String fileToRemove = (String) filesPath[random.nextInt(totalFiles)];
				totalFiles--;
				files.remove(fileToRemove);
				filesPath = files.keySet().toArray();
				int time = this.getWaitTime();
				totalTime += time;
				Remove remove = new Remove(totalTime, fileToRemove);
				out.write(remove.toString());
				break;
			}

		}

		out.close();
		fstream.close();

	}

	private ArrayList<Integer> getOperationsList(int numOperations) {
		int numActions = Operation.values().length;
		ArrayList<Integer> op = new ArrayList<Integer>();

		for (int i = 0; i < numOperations * numActions; i++) {
			op.add(i);
		}

		Collections.shuffle(op);
		return op;
	}

	private HashSet<String> getFoldersFromFilesList(Object[] files) {
		HashSet<String> folders = new HashSet<String>();

		for (Object file : files) {
			String fileStr = (String) file;
			String folder = this.getFolderFromPath(fileStr);
			folders.add(folder);
		}

		return folders;
	}

	private String getFolderFromPath(String fileStr) {

		int pos = 0, lastPos = 0;
		while (pos != -1) {
			pos = fileStr.indexOf('/', lastPos);
			if (pos != -1)
				lastPos = pos + 1;
		}

		return fileStr.substring(0, lastPos);

	}

	private boolean createFile(int newFileSize, int fileNum, HashSet<String> folders) {

		String home = System.getProperty("user.home");
		String tmpPath = home + "/tmpFiles";
		String fileName = "file" + fileNum + ".dat";

		boolean success = false;

		try {
			FileUtils.forceMkdir(new File(tmpPath));
			FilesOp.generateRandomFile(tmpPath + "/" + fileName, newFileSize);
			FilesOp.moveFile(tmpPath + "/" + fileName, Config.getAddPathFolder() + "/" + fileName);

			logger.debug("File " + Config.getAddPathFolder() + "/" + fileName + " created of size -> " + newFileSize);
			// this.files.put(path+"/"+fileName, newFileSize); NOT NECESSARY??
			// out.write(path+"/"+fileName+" "+newFileSize+"\n");

			success = true;
		} catch (IOException e) {
			logger.error("Error creating file /" + fileNum + " of size " + newFileSize);
			e.printStackTrace();
		}

		return success;
	}

	private int getWaitTime() {
		int time = random.nextInt(this.maxWaitTime - this.minWaitTime) + this.minWaitTime;
		return time;
	}

	public void generateDatasetFromBeginning(String datasetFile, HashMap<String, Integer> files, int numOperations) throws IOException {

		FileWriter fstream = new FileWriter(datasetFile);
		BufferedWriter out = new BufferedWriter(fstream);

		int fileNum = files.values().size() + 1;
		int totalFiles = fileNum - 1;

		Object[] filesPath = files.keySet().toArray();
		HashSet<String> folders = this.getFoldersFromFilesList(filesPath);

		ArrayList<Integer> operations = getOperationsListBeginning(numOperations);
		totalTime = 0;

		for (int opNum : operations) {

			switch (opNum % 3) {
			case 0:
				// ADD
				logger.debug("ADD");
				int newFileSize = this.random.nextInt(Config.getMaxSize() - Config.getMinSize() + 1) + Config.getMinSize();
				boolean created = createFile(newFileSize, fileNum, folders);

				// Select folder to copy the new file
				String path = "/";

				if (created) {
					String fileName = "file" + fileNum + ".dat";
					int time = this.getWaitTime();
					totalTime += time;
					Add addAction = new Add(totalTime, "/" + fileName, path);
					files.put(path + fileName, newFileSize);
					filesPath = files.keySet().toArray();
					out.write(addAction.toString());
					fileNum++;
					totalFiles++;
				}
				break;
			case 1:
				// UPDATE
				logger.debug("UPDATE");
				if (totalFiles <= 0) {
					continue;
				}
				Object fileToModify = filesPath[random.nextInt(totalFiles)];
				int fileSize = files.get(fileToModify);
				this.generateUpdate(out, fileToModify, fileSize);
				break;
			case 2:
				// REMOVE
				logger.debug("REMOVE");
				if (totalFiles <= 0) {
					continue;
				}
				String fileToRemove = (String) filesPath[random.nextInt(totalFiles)];
				totalFiles--;
				files.remove(fileToRemove);
				filesPath = files.keySet().toArray();
				int time = this.getWaitTime();
				totalTime += time;
				Remove remove = new Remove(totalTime, fileToRemove);
				out.write(remove.toString());
				break;
			}

		}

		out.close();
		fstream.close();

	}

	private ArrayList<Integer> getOperationsListBeginning(int numOperations) {
		int numActions = Operation.values().length;
		ArrayList<Integer> op = new ArrayList<Integer>();

		for (int i = 1; i < numOperations * numActions; i++) {
			op.add(i);
		}

		Collections.shuffle(op);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		op.add(0, 0);
		return op;
	}

	public int getTotalData() {
		return totalData;
	}

	public void setTotalData(int totalData) {
		this.totalData = totalData;
	}

	public int getModificationPercentage() {
		return modificationPercentage;
	}

	public void setModificationPercentage(int modificationPercentage) {
		this.modificationPercentage = modificationPercentage;
	}

}