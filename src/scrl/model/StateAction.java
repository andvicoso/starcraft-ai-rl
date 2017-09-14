package scrl.model;

import scrl.model.actions.Action;

public class StateAction {
	public Action action;
	public State state;

	public StateAction(State state, Action action) {
		this.action = action;
		this.state = state;
	}

	public Action getAction() {
		return action;
	}

	public State getState() {
		return state;
	}

	@Override
	public String toString() {
		return "[" + action + ", " + state + "]";
	}

}
