package scrl.model.actions;

import java.util.List;

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
		Position centroid = LocationUtils.getCentroid(units);
		Position p = null;
		// ir em direção dos inimigos
		// if (game.elapsedTime() < 180) {
		// p = new Position(centroid.getX() + 100, centroid.getY() + (centroid.getY() > 1000 ? -100 : 100));
		// } else {
		int vx = LocationUtils.getRandomLocationFixed(RangeDistance.MARINE_ATTACK_RANGE);
		int vy = LocationUtils.getRandomLocationFixed(RangeDistance.MARINE_ATTACK_RANGE);
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

	@Override
	public int getNumberOfFrames(Game game) {
		return 15;
	}

}
