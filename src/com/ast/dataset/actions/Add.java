package com.ast.dataset.actions;

import java.io.IOException;

import com.ast.dataset.executor.DatasetExecutor;
import com.ast.dataset.util.Config;
import com.ast.dataset.util.FilesOp;

public class Add extends Action {
    
    private String srcPath;
    private String destPath;
    
    public Add(int secondToExecute, String srcPath, String destPath) {
        super(secondToExecute);
        this.srcPath = srcPath;
        this.destPath = destPath;
    }

	@Override
	public void performAction(DatasetExecutor executor) throws IOException {
		String srcPath = Config.getAddPathFolder()+this.getSrcPath();
		String dstPath = Config.getFolderPath()+this.getDestPath()+this.getSrcPath().substring(1);
		FilesOp.moveFile(srcPath, dstPath);
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
