package com.ast.dataset.executor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import com.ast.dataset.actions.Action;
import com.ast.dataset.util.Utils;
//import java.net.InetSocketAddress;

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
			logger.debug("Sleeping " + time2sleep + " seconds.");
			Utils.sleep(time2sleep);

			this.currentSecond = time;
			this.performAction(action);

		}
		logger.debug("Dataset executed!");
	}

	public void executeGenericDataset(ArrayList<Action> actions) throws Exception {

		logger.debug("Executing generic dataset...");
		for (Action action : actions) {
			// int time = action.getSecondToExecute();
			// int time2sleep = time - this.currentSecond;
			// logger.debug("Sleeping "+time2sleep+" seconds.");
			// Utils.sleep(time2sleep);

			// this.currentSecond = time;
			this.performAction(action);
			Utils.sleep(6500);

		}
		logger.debug("Dataset executed!");
	}

	private void performAction(Action action) {

		try {
			System.out.println(action);
			action.performAction(this);
		} catch (IOException ex) {
			logger.error("Error applying the action.");
			ex.printStackTrace();
		}

	}

	public Random getRandom() {
		return random;
	}

	public void setRandom(Random random) {
		this.random = random;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

}
