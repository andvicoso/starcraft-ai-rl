package scrl.model.actions;

import java.util.List;
import java.util.Random;
import java.util.Set;

import scrl.model.QTable;
import scrl.model.SCMDP;
import scrl.model.State;
import scrl.utils.CollectionsUtils;

public class EGreedyActionChooser implements ActionChooser {
	private Random rand = new Random();
	private double epsilon = 0.1;
	private QTable qTable;

	public EGreedyActionChooser(QTable qTable) {
		this.qTable = qTable;
	}

	@Override
	public Action getAction(State state) {
		Action action = null;
		double rnd = rand.nextDouble();
		// escolhe de forma randomica entre tentar nova acao ou usar a melhor
		// acao atualmente conhecida
		if (rnd < epsilon) {
			// Log.log("random actions");
			List<Action> actions = SCMDP.getValidActions();
			action = actions.get(rand.nextInt(actions.size()));
		} else {
			Set<Action> maxActions = qTable.getMaxActions(state);
			action = CollectionsUtils.getRandom(maxActions);
		}
		return action;
	}
}
