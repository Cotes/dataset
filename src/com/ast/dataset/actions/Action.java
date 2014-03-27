package com.ast.dataset.actions;

import java.io.IOException;

import com.ast.dataset.executor.DatasetExecutor;

public abstract class Action {
    
    private int secondToExecute;
    
    public Action(int secondToExecute) {
        this.secondToExecute = secondToExecute;
    }

    public abstract void performAction(DatasetExecutor executor) throws IOException; 
    
    public int getSecondToExecute( ) {
        return secondToExecute;
    }

    public void setSecondToExecute( int secondToExecute ) {
        this.secondToExecute = secondToExecute;
    }
    
}
