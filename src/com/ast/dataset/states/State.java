package com.ast.dataset.states;

import java.util.Random;

public interface State {
	
	public State nextState(Random random);

}
