package com.ast.dataset.states;

import java.util.Random;

import com.ast.dataset.actions.Action;
import com.ast.dataset.actions.Add;
import com.ast.dataset.util.Utils;

public class New implements State {
	
	private static final int P_NM = 2;
	private static final int P_NU = 78;
	private static final int P_ND = 20;

	@Override
	public State nextState(Random random) {
		
		State newState;
		int value = random.nextInt(100);
		
		if (Utils.isBetween(value, 0, P_NM)) {
			newState = new Modified();
		} else if (Utils.isBetween(value, P_NM, P_NU)) {
			newState = new Unmodified();
		} else {
			newState = new Deleted();
		}
		
		return newState;
	}

}
