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

public class ExploreCentroid extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void execute(Game game) {
		List<Unit> units = game.self().getUnits();
		Position centroid = LocationUtils.getCentroidAllies(units);
		Position p = null;
		// ir em dire��o dos inimigos
		// if (game.elapsedTime() < 120) {
		// p = new Position(centroid.getX() + 100, centroid.getY() + (centroid.getY() > 1000 ? -100 : 100));
		// } else {
		int vx = LocationUtils.getRandomLocation(RangeDistance.MARINE_ATTACK_RANGE * 3);
		int vy = LocationUtils.getRandomLocation(RangeDistance.MARINE_ATTACK_RANGE * 3);
		p = new Position(centroid.getX() + vx, centroid.getY() + vy);
		// }

		if (Log.VISUAL_DEBUG) {
			game.drawCircleMap(p, 5, Color.Blue);
			game.drawTextScreen(p, getClass().getSimpleName());
		}

		for (Unit unit : units) {
			unit.move(p, false);
		}
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof ExploreCentroid))
			return false;
		return Objects.equals(this.getClass().getSimpleName(), other.getClass().getSimpleName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getClass().getSimpleName());
	}
}
