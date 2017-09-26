package scrl.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import scrl.model.actions.Action;
import scrl.utils.CollectionsUtils;

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
	public Set<Action> getMaxActions(State state) {
		Map<Action, Double> map = this.get(state);
		double max = Collections.max(map.values());
		return CollectionsUtils.getKeysForValue(map, max);
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
			Set<Action> bestActions = getMaxActions(state);
			StateAction policyData = new StateAction(state, bestActions);
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
				builder.append(" - Attack: " + vet.toArray()[2]);
				builder.append("\n");
			}
		}
		return builder.toString();
	}
}
