package com.ast.dataset.states;

import java.util.Random;

import com.ast.dataset.util.Utils;

public class Unmodified implements State {

	private static final int P_UM = 14; // (0.14%) => 0.14/100 * 10000 = 14
	private static final int P_UD = 35;
	//private static final int P_UU = 9951;
	
	@Override
	public State nextState(Random random) {
		State newState;
		int value = random.nextInt(10000);
		
		if (Utils.isBetween(value, 0, P_UM)) {
			newState = new Modified();
		} else if (Utils.isBetween(value, P_UM, P_UD)) {
			newState = new Deleted();
		} else {
			newState = new Unmodified();
		}
		
		return newState;
	}

}
