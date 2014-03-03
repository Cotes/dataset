package com.ast.dataset;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.ast.dataset.actions.Action;
import com.ast.dataset.executor.DatasetExecutor;
import com.ast.dataset.generator.DatasetGenerator;
import com.ast.dataset.generator.FoldersNotCreated;
import com.ast.dataset.generator.TreeFSGenerator;
import com.ast.dataset.reader.DatasetReader;
import com.ast.dataset.util.Config;

public class Dataset {

	private static final Logger logger = Logger.getLogger(Dataset.class.getName());

	private TreeFSGenerator fsGenerator;
	private DatasetGenerator datasetGenerator;
	private DatasetReader reader;
	private DatasetExecutor executor;
	private HashMap<String, Integer> files;

	public Dataset(String configPath) {
		this.reader = new DatasetReader();
		init(configPath);
	}

	public void init(String configPath) {
		URL configFileResource = Dataset.class.getResource("/com/ast/dataset/resources/log4j.xml");
		DOMConfigurator.configure(configFileResource);

		// Read config.properties file
		try {
			logger.debug("Reading config.properties");
			Config.loadProperties(configPath);
		} catch (IOException e) {
			logger.error("Impossible to read/create config.properties file.");
			e.printStackTrace();
			System.exit(1);
		}

		// Initializations
		this.executor = new DatasetExecutor(Config.getFolderPath());
		this.datasetGenerator = new DatasetGenerator(Config.getTreeTotalData(), Config.getPercentageModified());

		String folder = Config.getFSPath();
		int levels = Config.getTreeLevels();
		int childs = Config.getTreeChilds();
		int totalData = Config.getTreeTotalData();
		this.fsGenerator = new TreeFSGenerator(folder, levels, childs, totalData);
	}

	public void generateFileSystem() {

		fsGenerator.createFolders();

		int minFileSize = Config.getMinSize();
		int maxFileSize = Config.getMaxSize();

		try {

			this.files = fsGenerator.fillFoldersWithFiles(minFileSize, maxFileSize);
		} catch (FoldersNotCreated e) {
			logger.error("Folders not created yet.");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("Error writing to file.");
			e.printStackTrace();
		}

	}

	public void readFilesCreatedFromFile(String path) throws IOException {

		this.files = this.fsGenerator.readFilesFromFolder(path);

	}

	public void copyFStoRootFolder(String rootFolder) throws IOException {
		fsGenerator.copyFSFolder(rootFolder);
	}

	public void generateDatasetUpdates(String path) {

		try {
			this.datasetGenerator.generateOnlyUpdates(path, this.files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateGenericDataset(String path) {

		try {
			this.datasetGenerator.generateGenericDataset(path, this.files, Config.getNumOperations());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateDatasetFromBeginning(String path) {
		try {
			this.datasetGenerator.generateDatasetFromBeginning(path, new HashMap<String, Integer>(), Config.getNumOperations());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void executeDataset(String path, String test) throws Exception {
		ArrayList<Action> actions = this.reader.readData(path);
		if (test.equals("update")) {
			this.executor.executeDataset(actions);
		} else {
			this.executor.executeGenericDataset(actions);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String configPath = null;
		//String sharedFolder = null;
		String datasetFolderPath = null;
		String datasetExecutePath = null;
		String readFilePath = null;
		String testName = null;

		URL configFileResource = Dataset.class.getResource("/com/ast/dataset/resources/log4j.xml");
		DOMConfigurator.configure(configFileResource);

		Options options = getOptions();
		CommandLineParser parser = new GnuParser();

		// 0. Read parameters!
		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("help") || cmd.hasOption('h')) {
				showHelp(options);
				System.exit(0);
			}

			//TODO maybe make this
			//sharedFolder = getParameterValue(cmd, "shared-folder", 's');
			configPath = getParameterValue(cmd, "read-config", 'c');
			datasetFolderPath = getParameterValue(cmd, "generate-dataset", 'g');
			readFilePath = getParameterValue(cmd, "read-fs", 'r');
			testName = getParameterValue(cmd, "test", 't');
			datasetExecutePath = getParameterValue(cmd, "execute-wl", 'e');

		} catch (ParseException e1) {
			e1.printStackTrace();
			System.exit(1);
		}

		if (testName == null) {
			System.out.println("Need to specify a testName!");
			System.exit(1);
		}

		Dataset dataset = new Dataset(configPath);

		// 1. Read a file or create a FS
		if (readFilePath != null) {
			try {
				dataset.readFilesCreatedFromFile(readFilePath);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(2);
			}
		} else {
			//TODO ensure generated folders are able to have several layers
			dataset.generateFileSystem();
		}

		// Generate the dataset
		if (datasetFolderPath != null) {
			if (testName.equalsIgnoreCase("update")) {
				dataset.generateDatasetUpdates(datasetFolderPath);
			} else if (testName.equalsIgnoreCase("beginning")) {
				dataset.generateDatasetFromBeginning(datasetFolderPath);
			} else {
				dataset.generateGenericDataset(datasetFolderPath);
			}
		}

		try {
			//TODO I'm not sure about this. Change properties folder path to an argument? 
			dataset.copyFStoRootFolder(Config.getFolderPath());
		} catch (IOException e1) {
			logger.debug("Error while copying the dataset.");
			e1.printStackTrace();
		}
		
		//TODO wait until all files are uploaded

		// Execute it
		if (datasetExecutePath != null) {

			try {
				logger.debug("Executing dataset.");
				dataset.executeDataset(datasetExecutePath, testName);
			} catch (Exception e) {
				logger.debug("Error executing the dataset.");
				e.printStackTrace();
			}

		}

	}

	private static String getParameterValue(CommandLine cmd, String longName, char shortName) {

		String value = null;
		if (cmd.hasOption(longName)) {
			value = cmd.getOptionValue(longName);
		} else if (cmd.hasOption(shortName)) {
			value = cmd.getOptionValue(shortName);
		}

		return value;
	}

	public static Options getOptions() {
		Options options = new Options();

		options.addOption("h", "help", false, "Print this message.");
		//TODO maybe make this
		//options.addOption("s", "shared-folder", false, "Indicate the shared folder where FS will be copied");
		options.addOption("c", "read-config", false, "Read specific config file");
		options.addOption("r", "read-fs", true, "Read FS from file");
		options.addOption("g", "generate-dataset", true, "Generate dataset files and workload.");
		options.addOption("e", "execute-wl", true, "Execute the workload");
		options.addOption("t", "test", true, "Test type.");

		return options;
	}

	private static void showHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ant", options);

	}

}
