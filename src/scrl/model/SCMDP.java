package scrl.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import scrl.model.actions.Action;
import scrl.model.actions.AttackLowestHP;
import scrl.model.actions.ExploreCentroid;
import scrl.model.actions.FleeCentroid;
import scrl.model.range.RangeHP;
import scrl.model.range.RangeUnits;

public class SCMDP {
	List<Action> actions;
	Set<State> states;

	public SCMDP() {
		actions = getValidActions(); // invoca a criacao das acoes possiveis
		states = createStates();
	}

	// cria o conjunto de estados da representacao do mundo
	public static final Set<State> createStates() {
		final Set<State> sts = new HashSet<State>();
		for (RangeHP mediumHpFromNearbyEnemies : RangeHP.values()) {
			for (RangeUnits numberOfEnemiesUnitsNearby : RangeUnits.values()) {
				for (RangeHP hpFromNearbyAllies : RangeHP.values()) {
					for (RangeUnits numberOfAlliesUnitsNearby : RangeUnits.values()) {
						State newUnit = new State(mediumHpFromNearbyEnemies, numberOfEnemiesUnitsNearby, hpFromNearbyAllies, numberOfAlliesUnitsNearby);
						sts.add(newUnit);
					}
				}
			}
		}
		return sts;
	}

	// cria a relacao de acoes possiveis
	public static final List<Action> getValidActions() {
		return Arrays.asList(new ExploreCentroid(), new FleeCentroid(), new AttackLowestHP());
		// return Arrays.asList(new Explore(), new Flee(), new Attack());
	}

	public Set<State> getStates() {
		return states;
	}

	public List<Action> getActions() {
		return actions;
	}
}
