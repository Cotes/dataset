package com.ast.dataset.util;

public class Constants {
	
	public static final String PROP_FILENAME = "config.properties";
	
	// Tree constants
	public static final String PROP_TREE_LEVELS = "TreeLevels";
	public static final String PROP_TREE_CHILDS = "TreeChilds";
	public static final String PROP_TREE_TOTAL_DATA = "TreeTotalData";
	public static final String PROP_FS_PATH = "FSPath";
	public static final String PROP_FOLDER_PATH = "FolderPath";
	public static final String PROP_PERCENTAGE_MODIFIED = "PercentageModified";
	
	// Files
	public static final String PROP_MIN_SIZE = "SizeMin";
	public static final String PROP_MAX_SIZE = "SizeMax";
	
	// Dataset constants
	public static final String PROP_MIN_WAIT_TIME = "MinWaitTime";
	public static final String PROP_MAX_WAIT_TIME = "MaxWaitTime";
	public static final String PROP_MAX_MODIFICATION_SIZE = "MaxModificationSize";
	public static final String PROP_NUM_OPERATIONS = "NumOp";
	public static final String PROP_ADD_PATH_FOLDER = "AddPathFolder";
	
	
	// Default values
	public static final int DEFAULT_TREE_LEVELS = 3;
	public static final int DEFAULT_TREE_CHILDS = 2;
	public static final int DEFAULT_TREE_TOTAL_DATA = 76800;
	public static final String DEFAULT_FS_PATH = "/home/sergi/test/fs/";
	//TODO check this later
	public static final String DEFAULT_FOLDER_PATH = "/home/cotes/test/dataset/";
	public static final int DEFAULT_PERCENTAGE_MODIFIED = 5;
	
	// Files
	public static final int DEFAULT_MIN_SIZE = 512;
	public static final int DEFAULT_MAX_SIZE = 4096;
	
	// Dataset
	public static final int DEFAULT_MIN_WAIT_TIME = 3000; 	// ms
	public static final int DEFAULT_MAX_WAIT_TIME = 6000; 	// ms
	public static final int DEFAULT_MAX_MODIFICATION_SIZE = 250;	// Bytes
	public static final int DEFAULT_NUM_OPERATIONS = 50;
	public static final String DEFAULT_ADD_PATH_FOLDER = "/home/cotes/test/add_folder/";
	
}
