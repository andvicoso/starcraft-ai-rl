package scrl.model.actions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import bwapi.Game;
import bwapi.Unit;
import scrl.model.range.RangeDistance;

public class Attack extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	// Automato de execucao da acao
	public void execute(Game game, Unit me) {
		List<Tuple> tuples = new LinkedList<>();
		for (Unit unit : me.getUnitsInRadius(3 * RangeDistance.MARINE_ATTACK_RANGE)) {
			if (unit.exists() && unit.getPlayer().isEnemy(me.getPlayer())) {
				// relaciona as unidades inimigas em uma listaLigada,
				// Insere informações como:
				// unidade esta em alcance de atack direto?
				// quantidade de HP da unidade
				// distancia entre a unidade
				tuples.add(new Tuple(unit, me.isInWeaponRange(unit), unit.getHitPoints(), me.getDistance(unit)));
			}
		}

		// se a list nao estiver vazia, ORDENE
		// Como resultado da ordenação, teremos em ordem crescente, os melhores alvos
		// ( 0, melhor algo, 999, pior algo)
		if (!tuples.isEmpty()) {
			Collections.sort(tuples);
			me.attack(tuples.get(0).unit);
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Attack))
			return false;
		return Objects.equals(this.getClass().getSimpleName(), other.getClass().getSimpleName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getClass().getSimpleName());
	}

	class Tuple implements Comparable<Tuple> {
		Unit unit;
		Boolean inRange;
		Integer hp;
		Integer distance;

		public Tuple(Unit unit, Boolean inRange, Integer hp, Integer distance) {
			this.unit = unit;
			this.inRange = inRange;
			this.hp = hp;
			this.distance = distance;
		}

		@Override
		public int compareTo(Tuple o) {
			int diff = inRange.compareTo(o.inRange);
			if (diff != 0)
				return -diff;

			diff = hp.compareTo(o.hp);
			if (diff != 0)
				return diff;

			diff = distance.compareTo(o.distance);
			if (diff != 0)
				return diff;

			return 0;
		}

		@Override
		public String toString() {
			return "Tuple [unit=" + unit + ", inRange=" + inRange + ", hp=" + hp + ", distance=" + distance + "]";
		}

	}
}
