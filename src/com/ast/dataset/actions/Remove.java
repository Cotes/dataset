package com.ast.dataset.actions;

public class Remove extends Action {
    
    private String filePath;
    
    public Remove(int secondToExecute, String filePath) {
        super(secondToExecute);
        this.filePath = filePath;
    }

    public String getFilePath( ) {
        return filePath;
    }

    public void setFilePath( String filePath ) {
        this.filePath = filePath;
    }
    
    @Override
    public String toString(){
        String str = this.getSecondToExecute()+" REMOVE "+filePath+"\n";
        return str;
    }

}
