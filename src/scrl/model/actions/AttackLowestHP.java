package scrl.model.actions;

import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import scrl.model.range.RangeDistance;
import scrl.utils.LocationUtils;
import scrl.utils.Log;
import scrl.utils.UnitUtils;

public class AttackLowestHP extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public void execute(Game game) {
		List<Unit> units = game.self().getUnits();
		Position centroid = LocationUtils.getCentroid(units);

		Unit lowestUnit = UnitUtils.getLowestHPEnemy(game, centroid);

		if (lowestUnit != null) {
			if (Log.VISUAL_DEBUG) {
				game.drawTextScreen(lowestUnit.getPosition(), getClass().getSimpleName());
				game.drawCircleMap(centroid, RangeDistance.MARINE_ATTACK_RANGE, Color.Red);
				game.drawCircleMap(lowestUnit.getPosition(), 5, Color.Red);
			}

			for (Unit me : units) {
				me.attack(lowestUnit);
			}
		}
	}

	public int getNumberOfFrames(Game game) {
		List<Unit> units = game.self().getUnits();
		Position centroid = LocationUtils.getCentroid(units);
		return UnitUtils.getLowestHPEnemy(game, centroid) != null ? 10 : 0;// 8
	}
}
