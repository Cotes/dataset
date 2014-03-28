package com.ast.dataset.actions;

import java.io.IOException;

import com.ast.dataset.executor.DatasetExecutor;

public abstract class Action {
    
    private Integer secondToExecute;
    
    public Action(Integer secondToExecute) {
        this.secondToExecute = secondToExecute;
    }

    public abstract void performAction(DatasetExecutor executor) throws IOException; 
    
    public Integer getSecondToExecute( ) {
        return secondToExecute;
    }

    public void setSecondToExecute( Integer secondToExecute ) {
        this.secondToExecute = secondToExecute;
    }
    
}
