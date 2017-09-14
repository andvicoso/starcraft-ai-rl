package scrl.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import scrl.model.SCMDP;
import scrl.model.State;
import scrl.model.actions.Action;
import scrl.model.function.RewardFunction;

public class MirrorRewardFunction {

	static Collection<Action> actions = SCMDP.getValidActions();

	public static void main(String[] args) throws IOException {
		Writer o = new BufferedWriter(new FileWriter(new File("mirror_rf.txt")));
		Collection<State> states = SCMDP.createStates();

		for (State state : states) {
			StringBuilder sb = new StringBuilder();
			Map<Action, Integer> map = new HashMap<>();

			for (State state2 : states) {
				for (Action action : getBestActions(state, state2)) {
					map.put(action, map.getOrDefault(action, 0) + 1);
				}
			}

			int max = Collections.max(map.values());
			List<Object> actions = map.entrySet().stream().filter(new Predicate<Entry<Action, Integer>>() {
				@Override
				public boolean test(Entry<Action, Integer> e) {
					return e.getValue() == max;
				}
			}).map(Map.Entry::getKey)
					.collect(Collectors.toList());

			sb.append(state.toCSV()).append(actions).append("\n");
			o.write(sb.toString());
			o.flush();
		}
		o.close();
	}

	private static Collection<Action> getBestActions(State state, State state2) {
		Map<Double, Collection<Action>> map = new HashMap<>();
		for (Action action : actions) {
			double r = RewardFunction.getValue(state, state2, action);
			map.putIfAbsent(r, new LinkedList<>());
			map.get(r).add(action);
		}

		return map.get(Collections.max(map.keySet()));
	}
}
