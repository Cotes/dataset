package com.ast.dataset.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ast.dataset.actions.Action;
import com.ast.dataset.actions.Add;
import com.ast.dataset.actions.ByteRange;
import com.ast.dataset.actions.Remove;
import com.ast.dataset.actions.Update;
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
	private static final float P_N = 4F;
	private static final float P_D = 0.5F;
	
	private int numSnapshots;
	private Random random;

	public RealisticDatasetGenerator(int numSnapshots) {
		super();
		this.numSnapshots = numSnapshots;
		this.random = new Random();
	}

	@Override
	public void generateDataset( String datasetFile, List<SimpleFile> simpleFiles ) {
		
		List<SnapshotFile> files = convertToSnapshotFiles(simpleFiles);
		
		try {
			FileWriter fstream = new FileWriter(datasetFile);
			BufferedWriter out = new BufferedWriter(fstream);
	
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
				Action action = this.getActionFromSnapshotFile(file);
				if (action != null) {
					out.write(action.toString());
				}
			} catch (IOException e) {
				logger.error("Error getting action from snapshot file or writing to file: ", e);
			}
			
		}
		
	}
	
	private Action getActionFromSnapshotFile(SnapshotFile file) throws IOException {
	
		State state = file.getState();
		Action action = null;
		
		if (state instanceof New) {
			// TODO paths???
			action = new Add(file.getFilename(), file.getFilename());
		} else if (state instanceof Modified) {
			ArrayList<ByteRange> modifications = generateUpdate(file.getSize());
			action = new Update(file.getFilename(), modifications);
		} else if (state instanceof Unmodified) {
			action = null;
		} else if (state instanceof Deleted) {
			// TODO check if deleted before
			Deleted deleted = (Deleted) state;
			if (!deleted.isWritten()) {
				action = new Remove(file.getFilename());
				deleted.setWritten(true);
			}
		}
		
		return action;
	}

}
