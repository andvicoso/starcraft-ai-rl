package scrl.model;

import java.util.Collections;
import java.util.Set;

import scrl.model.actions.Action;

public class StateAction {
	public Set<Action> actions;
	public State state;

	public StateAction(State state, Action action) {
		this.actions = Collections.singleton(action);
		this.state = state;
	}

	public StateAction(State state, Set<Action> actions) {
		this.actions = actions;
		this.state = state;
	}

	public Set<Action> getActions() {
		return actions;
	}

	public State getState() {
		return state;
	}

	@Override
	public String toString() {
		return "[" + actions + ", " + state + "]";
	}

}
