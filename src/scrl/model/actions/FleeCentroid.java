package scrl.model.actions;

import java.util.List;
import java.util.Objects;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import scrl.model.range.RangeDistance;
import scrl.utils.LocationUtils;
import scrl.utils.Log;
import scrl.utils.UnitUtils;

public class FleeCentroid extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public void execute(Game game) {
		List<Unit> units = game.self().getUnits();
		List<Unit> enemies = UnitUtils.getEnemies(game);
		Position centroid = LocationUtils.getCentroidAllies(units);
		Position pos = centroid;

		if (!enemies.isEmpty()) {
			int x = 0;
			int y = 0;
			for (Unit unit : enemies) {
				Position p = unit.getPosition();
				x += p.getX() - centroid.getX();
				y += p.getY() - centroid.getY();
			}
			x += centroid.getX();
			y += centroid.getY();

			pos = new Position(x, y);
		} else {
			int vx = LocationUtils.getRandomLocation(RangeDistance.MARINE_ATTACK_RANGE * 2);
			int vy = LocationUtils.getRandomLocation(RangeDistance.MARINE_ATTACK_RANGE * 2);
			pos = new Position(centroid.getX() + vx, centroid.getY() + vy);
		}

		if (Log.VISUAL_DEBUG) {
			game.drawCircleMap(pos, 5, Color.Yellow);
			game.drawTextScreen(pos, getClass().getSimpleName());
		}

		for (Unit unit : units) {
			unit.move(pos, false);
		}
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof FleeCentroid))
			return false;
		return Objects.equals(this.getClass().getSimpleName(), other.getClass().getSimpleName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getClass().getSimpleName());
	}
}
