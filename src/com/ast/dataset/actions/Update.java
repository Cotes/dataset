package com.ast.dataset.actions;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ast.dataset.executor.DatasetExecutor;

public class Update extends Action {

	private Logger logger = Logger.getLogger(Update.class.getName());

	private String filePath;
	ArrayList<ByteRange> modifications;

	public Update(int secondToExecute, String filePath, ArrayList<ByteRange> modifications) {
		super(secondToExecute);
		this.filePath = filePath;
		this.modifications = modifications;
	}

	@Override
	public void performAction(DatasetExecutor executor) throws IOException {
		logger.debug("Perform an update action.");

		String filePath = this.getFilePath();
		filePath = filePath.substring(1);
		RandomAccessFile fileStream = new RandomAccessFile(executor.getRootPath() + "/" + filePath, "rw");

		ArrayList<ByteRange> modifications = this.getModifications();

		for (ByteRange modification : modifications) {
			// Change from KB to B
			int dataSize = (modification.getByteEnd() - modification.getByteStart()) * 1024;
			byte data[] = new byte[dataSize];

			executor.getRandom().nextBytes(data);

			int byteStart = modification.getByteStart() * 1024;
			fileStream.seek(byteStart);
			fileStream.write(data);
		}

		fileStream.close();
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public ArrayList<ByteRange> getModifications() {
		return modifications;
	}

	public void setModifications(ArrayList<ByteRange> modifications) {
		this.modifications = modifications;
	}

	@Override
	public String toString() {
		String str = this.getSecondToExecute() + " UPDATE " + filePath + " ";
		for (ByteRange range : this.modifications) {
			str += (range.toString() + " ");
		}
		str += "\n";

		return str;
	}
}
