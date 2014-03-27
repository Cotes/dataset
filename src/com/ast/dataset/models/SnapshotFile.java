package com.ast.dataset.models;

import com.ast.dataset.states.State;

public class SnapshotFile extends SimpleFile {

	private State state;
	
	public SnapshotFile(String filename, int size, State state) {
		super(filename, size);
		this.state = state;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

}
