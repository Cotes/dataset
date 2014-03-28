package com.ast.dataset.states;

import java.util.Random;

import com.ast.dataset.util.Utils;

public class Deleted implements State {

	private static final int P_DN = 6;
	
	@Override
	public State nextState(Random random) {
		State newState;
		int value = random.nextInt(100);
		
		if (Utils.isBetween(value, 0, P_DN)) {
			newState = new New();
		} else {
			newState = new Deleted();
		}
		
		return newState;
	}

}
