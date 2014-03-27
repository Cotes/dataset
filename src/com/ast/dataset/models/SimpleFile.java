package com.ast.dataset.models;

public class SimpleFile {

	private String filename;
	private int size;
	
	public SimpleFile(String filename, int size) {
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

}
