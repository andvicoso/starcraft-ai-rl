package scrl.model.actions;

import java.util.List;

import bwapi.Color;
import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import scrl.utils.LocationUtils;
import scrl.utils.Log;
import scrl.utils.UnitUtils;

public class FleeCentroid extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public void execute(Game game) {
		List<Unit> units = game.self().getUnits();
		List<Unit> enemies = UnitUtils.getEnemies(game);
		Position centroid = LocationUtils.getCentroid(units);
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

			if (Log.VISUAL_DEBUG) {
				game.drawCircleMap(pos, 5, Color.Yellow);
				game.drawTextScreen(pos, getClass().getSimpleName());
			}

			for (Unit unit : units) {
				unit.move(pos, false);
			}
		} else {
			new ExploreCentroid().execute(game);
		}
	}

	public int getNumberOfFrames(Game game) {
		return UnitUtils.getEnemies(game).size() > 0 ? 10 : 0;// 8
	}
}
