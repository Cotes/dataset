package com.ast.dataset.actions;

import java.util.ArrayList;

public class Update extends Action {
    
    private String filePath;
    ArrayList<ByteRange> modifications;
    
    public Update(int secondToExecute, String filePath, ArrayList<ByteRange> modifications) {
        super(secondToExecute);
        this.filePath = filePath;
        this.modifications = modifications;
    }

    public String getFilePath( ) {
        return filePath;
    }

    public void setFilePath( String filePath ) {
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
    	String str = this.getSecondToExecute()+" UPDATE "+filePath+" ";
    	for (ByteRange range : this.modifications) {
    		str += (range.toString()+" ");
    	}
    	str += "\n";
    	
    	return str;
    }

}
