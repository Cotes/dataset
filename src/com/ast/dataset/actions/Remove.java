package com.ast.dataset.actions;

import java.io.IOException;

import com.ast.dataset.executor.DatasetExecutor;
import com.ast.dataset.util.Config;
import com.ast.dataset.util.FilesOp;

public class Remove extends Action {

	private String filePath;

	public Remove(String filePath) {
		this(null, filePath);
	}
	
	public Remove(Integer secondToExecute, String filePath) {
		super(secondToExecute);
		this.filePath = filePath;
	}

	@Override
	public void performAction(DatasetExecutor executor) throws IOException {
		String removePath = Config.getFolderPath() + this.getFilePath();
		FilesOp.removeFile(removePath);
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		String str = this.getSecondToExecute() + " REMOVE " + filePath + "\n";
		return str;
	}

}
