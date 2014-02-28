package com.ast.dataset.actions;

public class Action {
    
    private int secondToExecute;
    
    public Action(int secondToExecute) {
        this.secondToExecute = secondToExecute;
    }

    public int getSecondToExecute( ) {
        return secondToExecute;
    }

    public void setSecondToExecute( int secondToExecute ) {
        this.secondToExecute = secondToExecute;
    }
    
}
