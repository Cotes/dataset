package com.ast.dataset.actions.dummies;

public class DummyAdd extends DummyAction {

	private String filename;
	private int size;
	
	public DummyAdd(String filename, int size) {
		this.filename = filename;
		this.size = size;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		String str = "ADD " + this.filename + " " + this.size + "\n";
		return str;
	}
	
}
