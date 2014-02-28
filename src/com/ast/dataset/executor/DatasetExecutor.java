package com.ast.dataset.executor;

import java.io.IOException;
import java.io.RandomAccessFile;
//import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ast.dataset.actions.Action;
import com.ast.dataset.actions.Add;
import com.ast.dataset.actions.ByteRange;
import com.ast.dataset.actions.Remove;
import com.ast.dataset.actions.Update;
import com.ast.dataset.util.Config;
import com.ast.dataset.util.FilesOp;
import com.ast.dataset.util.Utils;

public class DatasetExecutor {
	
	private Logger logger = Logger.getLogger(DatasetExecutor.class.getName());
	
	private int currentSecond;
	private Random random;
	private String rootPath;
	
	public DatasetExecutor(String rootPath) {
		this.currentSecond = 0;
		this.rootPath = rootPath;
		this.random = new Random();
	}
	
	public void executeDataset(ArrayList<Action> actions) {
		
		logger.debug("Executing dataset...");
		for (Action action : actions) {
			int time = action.getSecondToExecute();
			int time2sleep = time - this.currentSecond;
			logger.debug("Sleeping "+time2sleep+" seconds.");
			Utils.sleep(time2sleep);
			
			this.currentSecond = time;
			this.performAction(action);
			
		}
		logger.debug("Dataset executed!");
	}
	
	public void executeGenericDataset(ArrayList<Action> actions) throws Exception {
	    
		logger.debug("Executing generic dataset...");
		for (Action action : actions) {
			//int time = action.getSecondToExecute();
			//int time2sleep = time - this.currentSecond;
			//logger.debug("Sleeping "+time2sleep+" seconds.");
			//Utils.sleep(time2sleep);
			
			//this.currentSecond = time;
			this.performAction(action);
			Utils.sleep(6500);
			
		}
		logger.debug("Dataset executed!");
	}

    private void performAction(Action action) {
		
		try {
			if (action instanceof Add) {
				Add add = (Add)action;
				System.out.println(add.toString());
				this.performAdd(add);
			} else if (action instanceof Remove) {
				Remove remove = (Remove)action;
				System.out.println(remove.toString());
				this.performRemove(remove);
			} else if (action instanceof Update) {
				Update update = (Update)action;
				System.out.println(update.toString());
				this.performUpdate(update);
			}
		} catch (IOException ex) {
			logger.error("Error applying the action.");
			ex.printStackTrace();
		}
		
	}
	
	private void performAdd(Add add) throws IOException {
		String srcPath = Config.getAddPathFolder()+add.getSrcPath();
		String dstPath = Config.getFolderPath()+add.getDestPath()+add.getSrcPath().substring(1);
		FilesOp.moveFile(srcPath, dstPath);
	}
	
	private void performRemove(Remove remove) throws IOException {
		String removePath = Config.getFolderPath()+remove.getFilePath();
		FilesOp.removeFile(removePath);
	}
	
	private void performUpdate(Update update) throws IOException {
		
		logger.debug("Perform an update action.");
		
		String filePath = update.getFilePath();
		filePath = filePath.substring(1);
		RandomAccessFile fileStream = new RandomAccessFile(this.rootPath+"/"+filePath, "rw");
		
		ArrayList<ByteRange> modifications = update.getModifications();
		
		for (ByteRange modification : modifications) {
			//Change from KB to B
			int dataSize = (modification.getByteEnd() - modification.getByteStart())*1024;
			byte data[] = new byte[dataSize];
			
			this.random.nextBytes(data);
			
			int byteStart = modification.getByteStart()*1024;
			fileStream.seek(byteStart);
			fileStream.write(data);
		}
		
		fileStream.close();
		
	}
	
}
