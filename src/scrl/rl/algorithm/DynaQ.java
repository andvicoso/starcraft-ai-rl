package scrl.rl.algorithm;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import scrl.model.SCMDP;
import scrl.model.State;
import scrl.model.actions.Action;
import scrl.tests.Main;
import scrl.utils.CollectionsUtils;

public class DynaQ extends QLearning {

	private static final long serialVersionUID = 1L;
	public static final int HALLUCINATION = 24;

	private Map<State, Set<Action>> visited;
	Random generator = new Random();

	public DynaQ(SCMDP model) {
		super(model);
	}

	@Override
	protected double computeQ(State state, State next, Action action, double reward) {
		if (!visited.containsKey(state))
			visited.put(state, new HashSet<Action>());
		visited.get(state).add(action);

		return super.computeQ(state, next, action, reward);
	}

	protected void updateQTable(State state, State next, Action action, double newQValue) {
		super.updateQTable(state, next, action, newQValue);

		if (Main.match > 0) {
			// Halucinate
			for (int i = 0; i < HALLUCINATION; i++) {
				State nextState = CollectionsUtils.getRandom(visited.keySet());
				Action nextAction = CollectionsUtils.getRandom(visited.get(nextState));

				State nextNextState = null;// config;
				double nextReward = q.get(nextState).get(nextAction);

				double newQValueH = super.computeQ(nextState, nextNextState, nextAction, nextReward);
				// atualiza a tabela baseada na alucinacao
				super.updateQTable(nextState, nextNextState, nextAction, newQValueH);
			}
		}
	}
}
