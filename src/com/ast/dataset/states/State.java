package com.ast.dataset.states;

import java.util.Random;

import com.ast.dataset.actions.Action;

public interface State {
	
	public State nextState(Random random);

}
