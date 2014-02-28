package com.ast.dataset.util;

import org.apache.log4j.Logger;

public class Utils {
	
	private static Logger logger = Logger.getLogger(Utils.class.getName());

	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			logger.error("Sleep problem.");
		}
	}
	
}
