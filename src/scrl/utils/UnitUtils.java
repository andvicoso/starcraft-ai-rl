package scrl.utils;

import java.util.LinkedList;
import java.util.List;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import scrl.model.range.RangeDistance;

public abstract class UnitUtils {

	// verifica se a unidade existe e se esta "parada"
	public static boolean isAlive_Idle(Unit unit) {
		return unit.exists() && !unit.isMoving() && !unit.isStartingAttack() && !unit.isAttacking();
	}

	public static List<Unit> getEnemies(Game game) {
		List<Unit> enemies = new LinkedList<Unit>();
		for (Unit unit : game.getAllUnits()) {
			if (unit.exists() && unit.getPlayer().isEnemy(game.self())) {
				enemies.add(unit);
			}
		}

		return enemies;
	}

	public static Unit getLowestHPEnemy(Game game, Position centroid) {
		Unit lowestUnit = null;

		for (Unit unit : game.getUnitsInRadius(centroid, 3 * RangeDistance.MARINE_ATTACK_RANGE)) {
			if (unit.exists() && unit.getPlayer().isEnemy(game.self())) {
				if (lowestUnit == null || unit.getHitPoints() < lowestUnit.getHitPoints())
					lowestUnit = unit;
			}
		}
		return lowestUnit;
	}

}
