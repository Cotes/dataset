package com.ast.dataset.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

public class RealisticDatasetGenerator extends DatasetGenerator {
	
	private Logger logger = Logger.getLogger( RealisticDatasetGenerator.class.getName() );
	
	// Home dataset probabilites in percents of file state transitions
	// TODO !!!!
	/*private static final float P_N = 4F;
	private static final float P_D = 0.5F;*/
	
	private int numSnapshots;
	private int training;
	private Random random;

	public RealisticDatasetGenerator(int trainingSnpashots, int numSnapshots) {
		super();
		this.training = trainingSnpashots;
		this.numSnapshots = numSnapshots;
		this.random = new Random();
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
		
		for (SnapshotFile file : files) {
			State newState = file.getState().nextState(this.random);
			file.setState(newState);
		}
		
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
		} else if (state instanceof Modified) {
			ArrayList<ByteRange> modifications = generateUpdate(file.getSize());
			action = new DummyUpdate(file.getFilename(), modifications);
		} else if (state instanceof Unmodified) {
			action = null;
		} else if (state instanceof Deleted) {
			Deleted deleted = (Deleted) state;
			if (!deleted.isWritten()) {
				action = new DummyRemove(file.getFilename());
				deleted.setWritten(true);
			}
		}
		
		return action;
	}

}
