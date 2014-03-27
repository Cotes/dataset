package com.ast.dataset.generator;

import java.util.ArrayList;
import java.util.List;

import com.ast.dataset.models.SimpleFile;
import com.ast.dataset.models.SnapshotFile;
import com.ast.dataset.states.New;
import com.ast.dataset.states.State;

public class RealisticDatasetGenerator extends DatasetGenerator {
	
	public enum FileState {NEW, MODIFIED, UNMODIFIED, DELETED};
	
	// Home dataset probabilites in percents of file state transitions
	private static final float P_N = 4F;
	private static final float P_D = 0.5F;
	
	private int numSnapshots;

	public RealisticDatasetGenerator(int numSnapshots) {
		super();
		this.numSnapshots = numSnapshots;
	}

	@Override
	public void generateDataset( String datasetFile, List<SimpleFile> simpleFiles ) {
		
		List<SnapshotFile> files = convertToSnapshotFiles(simpleFiles);
		
		for (int i=0; i < this.numSnapshots; i++) {
			nextSnapshot(files);
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
			State newState = file.getState().nextState();
			file.setState(newState);
		}
		
	}

}
