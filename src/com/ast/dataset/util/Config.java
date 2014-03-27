package com.ast.dataset.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {

	private static Properties properties;
	private static Logger logger = Logger.getLogger(Config.class);

	public static void loadProperties(String configPath) throws IOException {

		properties = new Properties();
		
		String path = (configPath == null) ? Constants.PROP_FILENAME : configPath;
		
		try {
			properties.load(new FileInputStream(path));
			validateProperties();
			logger.info("config.properties file loaded.");
		} catch (IOException e) {
			// properties file does not exist
			Logger.getLogger(Properties.class.getName()).warn("Properties file not found.");

			validateProperties();

			properties.store(new FileOutputStream(path), null);
			Logger.getLogger(Properties.class.getName()).info("New properties file created with default values.");
		}
		displayConfiguration();
	}
	
	public static Properties getProperties(){
		return properties;
	}
	
	private static void validateProperties() {
		checkProperty(Constants.PROP_TREE_LEVELS, Constants.DEFAULT_TREE_LEVELS);
		checkProperty(Constants.PROP_TREE_CHILDS, Constants.DEFAULT_TREE_CHILDS);
		checkProperty(Constants.PROP_TREE_TOTAL_DATA, Constants.DEFAULT_TREE_TOTAL_DATA);
		checkProperty(Constants.PROP_FS_PATH, Constants.DEFAULT_FS_PATH);
		checkProperty(Constants.PROP_FOLDER_PATH, Constants.DEFAULT_FOLDER_PATH);
		checkProperty(Constants.PROP_PERCENTAGE_MODIFIED, Constants.DEFAULT_PERCENTAGE_MODIFIED);
		
		// files
		checkProperty(Constants.PROP_MIN_SIZE, Constants.DEFAULT_MIN_SIZE);
		checkProperty(Constants.PROP_MAX_SIZE, Constants.DEFAULT_MAX_SIZE);
		
		//Dataset
		checkProperty(Constants.PROP_MIN_WAIT_TIME, Constants.DEFAULT_MIN_WAIT_TIME);
		checkProperty(Constants.PROP_MAX_WAIT_TIME, Constants.DEFAULT_MAX_WAIT_TIME);
		checkProperty(Constants.PROP_MODIFICATION_SIZE, Constants.DEFAULT_MODIFICATION_SIZE);
		checkProperty(Constants.PROP_NUM_OPERATIONS, Constants.DEFAULT_NUM_OPERATIONS);
		checkProperty(Constants.PROP_ADD_PATH_FOLDER, Constants.DEFAULT_ADD_PATH_FOLDER);
		
		//Check whether PROP_FS_PATH ends with / or not
		String fsPath = properties.getProperty(Constants.PROP_FS_PATH); 
		if(!fsPath.endsWith("/")){
			properties.setProperty(Constants.PROP_FS_PATH, fsPath + "/");
		}
		
	}
	
	private static void checkProperty(String key, Object defaultValue) {
		if (!properties.containsKey(key)) {
			Logger.getLogger(Properties.class.getName()).warn("Property " + key + " not found, using default value.");
			properties.setProperty(key, defaultValue.toString());
		}
	}

	private static void displayConfiguration() {
		logger.info("Configuration loaded:");

		Enumeration<?> e = properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			logger.info("\"" + key + "\":\"" + properties.getProperty(key) + "\"");
		}
	}
	
	public static int getTreeLevels() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_TREE_LEVELS));
	}
	
	public static int getTreeChilds() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_TREE_CHILDS));
	}
	
	public static int getTreeTotalData() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_TREE_TOTAL_DATA));
	}
	
	public static String getFolderPath() {
		return properties.getProperty(Constants.PROP_FOLDER_PATH);
	}
	
	public static String getFSPath() {
		return properties.getProperty(Constants.PROP_FS_PATH);
	}
	
	public static int getPercentageModified() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_PERCENTAGE_MODIFIED));
	}
	
	/* FILES */
	public static int getMinSize() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_MIN_SIZE));
	}
	
	public static int getMaxSize() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_MAX_SIZE));
	}
	
	public static int getMinWaitTime() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_MIN_WAIT_TIME));
	}
	
	public static int getMaxWaitTime() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_MAX_WAIT_TIME));
	}
	
	public static int getModificationSize() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_MODIFICATION_SIZE));
	}
	
	public static int getNumOperations() {
		return Integer.parseInt(properties.getProperty(Constants.PROP_NUM_OPERATIONS));
	}
	
	public static String getAddPathFolder() {
        return properties.getProperty(Constants.PROP_ADD_PATH_FOLDER);
    }
	
}
