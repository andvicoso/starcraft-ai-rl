package scrl.model;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import scrl.model.actions.Action;

public class QTable extends ConcurrentHashMap<State, Map<Action, Double>> {

	private static final long serialVersionUID = 3826717973754083254L;

	// cria a tabela Q
	public QTable(Collection<State> states, Collection<Action> Action) {
		super(states.size());
		for (State state : states) {
			Map<Action, Double> actionValues = new ConcurrentHashMap<Action, Double>();
			for (Action action : Action)
				actionValues.put(action, 0.);
			put(state, actionValues);
		}
	}

	// recupera qual a melhor acao na tabela para um dado estado
	public Action getMaxAction(State pState) {
		Map<Action, Double> map = this.get(pState);
		double max = Double.NEGATIVE_INFINITY;
		Action ret = null;
		for (Action act : map.keySet()) {
			if (map.get(act) > max) {
				max = map.get(act);
				ret = act;
			}
		}
		return ret;
	}
	// recupera qual o maior valor na tabela para um dado estado
	public double getMax(State pState) {
		Map<Action, Double> map = this.get(pState);
		double max = Double.NEGATIVE_INFINITY;
		for (Action action : map.keySet()) {
			double value = get(pState).get(action);
			if (value > max) {
				max = value;
			}
		}
		return max;
	}

	// cria uma estrutura contendo a politica otima baseado na tabela atual
	public Policy getPolicy() {
		Policy p = new Policy();
		for (State state : keySet()) {
			Action bestAction = getMaxAction(state);
			StateAction policyData = new StateAction(state, bestAction);
			p.add(policyData);
		}
		return p;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (State state : this.keySet()) {
			Collection<Double> vet = this.get(state).values();
			if (vet.toArray()[0].equals(0.0) && vet.toArray()[1].equals(0.0) && vet.toArray()[2].equals(0.0)) {
				// do not add them
			} else {
				builder.append("   ");
				builder.append(state.toCSV());
				builder.append(" =  Explore: " + vet.toArray()[0]);
				builder.append(" - Flee: " + vet.toArray()[1]);
				builder.append(" - Atack: " + vet.toArray()[2]);
				builder.append("\n");
			}
		}
		return builder.toString();
	}
}
