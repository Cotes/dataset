package com.ast.dataset.actions;

public class ByteRange {
	
	private int byteStart;
	private int byteEnd;
	
	public ByteRange(int start, int end) {
		byteStart = start;
		byteEnd = end;
	}

	public int getByteStart() {
		return byteStart;
	}

	public void setByteStart(int byteStart) {
		this.byteStart = byteStart;
	}

	public int getByteEnd() {
		return byteEnd;
	}

	public void setByteEnd(int byteEnd) {
		this.byteEnd = byteEnd;
	}
	
	@Override
	public String toString() {
		String str = byteStart + " " + byteEnd;
		return str;
	}

}
