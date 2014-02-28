package com.ast.dataset.reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ast.dataset.actions.Action;
import com.ast.dataset.actions.Add;
import com.ast.dataset.actions.ByteRange;
import com.ast.dataset.actions.Remove;
import com.ast.dataset.actions.Update;

public class DatasetReader {
	
	public enum ActionsName {ADD, REMOVE, UPDATE};

	private Logger logger = Logger.getLogger(DatasetReader.class.getName());
	private static final int ACTION_INDEX = 1;

    
    public DatasetReader () {
        
    }
    
    public ArrayList<Action> readData(String path2File) throws IOException {
        
    	logger.debug("Reading actions.");
        ArrayList<Action> actions = new ArrayList<Action>();
        
        BufferedReader br = new BufferedReader( new FileReader( path2File ) );
        String line;
        
        while ((line = br.readLine()) != null ) {
        	
        	Action action = this.getAction(line);
            
        	if (action != null) {
        		actions.add(action);
        	}
            
        }
        
        br.close();
        
        return actions;
    }
    
    private Action getAction(String line) {
    	
    	String[] lineSplitted = line.split(" ");
    	int second = Integer.parseInt(lineSplitted[0]);
    	String filePath = lineSplitted[2];
        
        ActionsName a = ActionsName.valueOf(lineSplitted[ACTION_INDEX]);
        Action action;
        
        switch (a) {
        case ADD:
        	String destPath = lineSplitted[3];
        	action = new Add(second, filePath, destPath);
        	break;
        case REMOVE:
        	action = new Remove(second, filePath);
        	break;
        case UPDATE:
        	
        	ArrayList<ByteRange> ranges = new ArrayList<ByteRange>();
        	
        	for (int i=3; i<lineSplitted.length; i+=2) {
        		int byteStart = Integer.parseInt(lineSplitted[i]);
            	int byteEnd = Integer.parseInt(lineSplitted[i+1]);
        		ByteRange range = new ByteRange(byteStart, byteEnd);
        		ranges.add(range);
        	}
        	
        	action = new Update(second, filePath, ranges);
        	break;
        default:
        	action = null;
        }
        
        return action;
    }

}
