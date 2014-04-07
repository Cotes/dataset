package com.ast.dataset.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import com.ast.dataset.actions.ByteRange;
import com.ast.dataset.actions.dummies.DummyAction;
import com.ast.dataset.actions.dummies.DummyAdd;
import com.ast.dataset.actions.dummies.DummyRemove;
import com.ast.dataset.actions.dummies.DummyUpdate;
import com.ast.dataset.models.SimpleFile;
import com.ast.dataset.models.SnapshotFile;
import com.ast.dataset.states.Deleted;
import com.ast.dataset.states.Modified;
import com.ast.dataset.states.New;
import com.ast.dataset.states.State;
import com.ast.dataset.states.Unmodified;
import com.ast.dataset.util.Utils;

public class RealisticDatasetGenerator extends DatasetGenerator {
	
	private Logger logger = Logger.getLogger( RealisticDatasetGenerator.class.getName() );
	
	// Home dataset probabilites in percents of file state transitions
	private static final float P_N = 4F;
	// TODO !!!!
	/*private static final float P_D = 0.5F;*/
	
	private int numSnapshots;
	private int training;
	private Random random;
	private long totalBytes;

	public RealisticDatasetGenerator(int trainingSnpashots, int numSnapshots) {
		super();
		this.training = trainingSnpashots;
		this.numSnapshots = numSnapshots;
		this.random = new Random();
		this.totalBytes = 0;
	}

	@Override
	public void generateDataset( String datasetFile, List<SimpleFile> simpleFiles ) {
		
		List<SnapshotFile> files = convertToSnapshotFiles(simpleFiles);
		List<SnapshotFile> initialFiles = trainFiles(files);
		
		try {
			FileWriter fstream = new FileWriter(datasetFile);
			BufferedWriter out = new BufferedWriter(fstream);
			saveChanges(out, initialFiles);
			saveChanges(out, files);
			
			for (int i=0; i < this.numSnapshots; i++) {
				nextSnapshot(files);
				saveChanges(out, files);
			}
			
			out.close();
			fstream.close();
			System.out.println(this.totalBytes);
		} catch (IOException e) {
			logger.error(e);
		}
		
	}
	
	private List<SnapshotFile> trainFiles(List<SnapshotFile> files) {
		
		for (int i=0; i<this.training; i++) {
			nextSnapshot(files);
		}
		
		// After the training, it is necessary to add modify files
		// for the correct execution of the dataset.
		List<SnapshotFile> initialFiles = new ArrayList<SnapshotFile>();
		for (SnapshotFile file : files) {
			State state = file.getState();
			if (!(state instanceof New)) {
				State newState = new New();
				SnapshotFile initial = new SnapshotFile(file.getFilename(), file.getSize(), newState);
				initialFiles.add(initial);
			}
		}
		
		return initialFiles;
	}
	
	private List<SnapshotFile> convertToSnapshotFiles( List<SimpleFile> simpleFiles ) {
		
		List<SnapshotFile> files = new ArrayList<SnapshotFile>();
		
		for (SimpleFile file : simpleFiles) {
			SnapshotFile snapshotFile = new SnapshotFile(file.getFilename(), file.getSize(), new New());
			files.add(snapshotFile);
		}
		
		return files;
	}

	private void nextSnapshot(List<SnapshotFile> files) {
		
		int count = 0;
		
		for (SnapshotFile file : files) {
			if (file.getSize() >= 4194304 && file.getState() instanceof New) {
				// Files >= 4MB and NEW state change to UNMODIFIED
				Unmodified unmodified = new Unmodified();
				file.setState(unmodified);
				count++;
				continue;
			} else if (file.getSize() >= 4194304) {
				// Files >= 4MB don't make changes.
				count++;
				continue;
			} else {
				// Files < than 4MB process as normals.
				State newState = file.getState().nextState(this.random);
				file.setState(newState);
				if (!(newState instanceof Deleted))	count++;
			}
		}
		
		// Create new files
		int newFiles = Math.round(count*P_N/100);
		for (int i=0; i<newFiles; i++) {
			String filename = RandomStringUtils.randomAlphabetic(7) + ".gz";
			SnapshotFile file = new SnapshotFile(filename, getFileSize(), new New());
			files.add(file);
		}
		
		assert (this.totalBytes >= 2147483648L); // 2GB
		
	}
	
	private void saveChanges(BufferedWriter out, List<SnapshotFile> files) {
		
		for (SnapshotFile file : files) {
			try {
				DummyAction action = this.getActionFromSnapshotFile(file);
				if (action != null) {
					out.write(action.toString());
				}
			} catch (IOException e) {
				logger.error("Error getting action from snapshot file or writing to file: ", e);
			}
			
		}
		
	}
	
	private DummyAction getActionFromSnapshotFile(SnapshotFile file) throws IOException {
	
		State state = file.getState();
		DummyAction action = null;
		
		if (state instanceof New) {
			action = new DummyAdd(file.getFilename(), file.getSize());
			this.totalBytes += file.getSize();
		} else if (state instanceof Modified) {
			ArrayList<ByteRange> modifications = generateUpdate(file.getSize());
			int bytesAdded = this.getBytesAdded(modifications);
			file.setSize(file.getSize() + bytesAdded);
			this.totalBytes += bytesAdded;
			action = new DummyUpdate(file.getFilename(), modifications);
		} else if (state instanceof Unmodified) {
			action = null;
		} else if (state instanceof Deleted) {
			Deleted deleted = (Deleted) state;
			if (!deleted.isWritten()) {
				action = new DummyRemove(file.getFilename());
				deleted.setWritten(true);
				this.totalBytes -= file.getSize();
			}
		}
		
		return action;
	}
	
	private int getFileSize() {
		
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
			maxSize = 65536*1024;
		} 
		
		int size = minSize + random.nextInt(maxSize-minSize+1);
		return size;
	}

}
