package com.ast.dataset.actions;

public class Add extends Action {
    
    private String srcPath;
    private String destPath;
    
    public Add(int secondToExecute, String srcPath, String destPath) {
        super(secondToExecute);
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

    public String getSrcPath( ) {
        return srcPath;
    }

    public void setSrcPath( String srcPath ) {
        this.srcPath = srcPath;
    }

    public String getDestPath( ) {
        return destPath;
    }

    public void setDestPath( String destPath ) {
        this.destPath = destPath;
    }

    @Override
    public String toString() {
        String str = this.getSecondToExecute()+" ADD "+srcPath+" "+destPath+"\n";
        return str;
    }
}
