package scrl.rl.algorithm;

import scrl.model.SCMDP;
import scrl.model.State;
import scrl.model.actions.Action;

public class QLearning extends AbstractLearning {

	private static final long serialVersionUID = 1L;

	public QLearning(SCMDP model) {
		super(model);
	}
	// calcula valor Q 
	protected double computeQ(State current, State next, Action action, double reward) {
		double oldQ = q.get(current).get(action); // Q(s,a)
		double bracedValue = reward + (GAMA * q.getMax(next)) - oldQ;
		return oldQ + ALPHA * bracedValue;
	}
}
