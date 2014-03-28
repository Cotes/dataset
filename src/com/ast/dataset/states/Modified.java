package com.ast.dataset.states;

import java.util.Random;

import com.ast.dataset.util.Utils;

public class Modified implements State {

	private static final int P_MU = 54;
	private static final int P_MD = 10;
	private static final int P_MM = 36;
	
	@Override
	public State nextState(Random random) {
		State newState;
		int value = random.nextInt(100);
		
		if (Utils.isBetween(value, 0, P_MU)) {
			newState = new Unmodified();
		} else if (Utils.isBetween(value, P_MU, P_MD)) {
			newState = new Deleted();
		} else {
			newState = new Modified();
		}
		
		return newState;
	}

}
