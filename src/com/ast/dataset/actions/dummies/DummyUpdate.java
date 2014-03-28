package com.ast.dataset.actions.dummies;

import java.util.ArrayList;

import com.ast.dataset.actions.ByteRange;

public class DummyUpdate extends DummyAction {
	
	private String filename;
	ArrayList<ByteRange> modifications;
	
	public DummyUpdate(String filename, ArrayList<ByteRange> modifications) {
		this.filename = filename;
		this.modifications = modifications;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public ArrayList<ByteRange> getModifications() {
		return modifications;
	}

	public void setModifications(ArrayList<ByteRange> modifications) {
		this.modifications = modifications;
	}

	@Override
	public String toString() {
		String str = "UPDATE " + this.filename + " ";
		for (ByteRange range : this.modifications) {
			str += (range.toString() + " ");
		}
		str += "\n";
		return str;
	}

}
