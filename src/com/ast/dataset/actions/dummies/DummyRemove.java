package com.ast.dataset.actions.dummies;

public class DummyRemove extends DummyAction {
	
	private String filename;
	
	public DummyRemove(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Override
	public String toString() {
		String str = "REMOVE " + this.filename + "\n";
		return str;
	}

}
