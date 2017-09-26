package scrl.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import bwapi.Position;
import bwapi.Unit;
import scrl.model.range.RangeDistance;

public abstract class LocationUtils {

	private static final Random r = new Random();

	public static int getRandomLocation() {
		return (int) (r.nextDouble() * RangeDistance.MARINE_ATTACK_RANGE * (r.nextBoolean() ? 1 : -1));
	}

	public static int getRandomLocation(int base) {
		return (int) (r.nextDouble() * base * (r.nextBoolean() ? 1 : -1));
	}

	public static int getRandomLocationFixed(int base) {
		return base * (r.nextBoolean() ? 1 : -1);
	}

	public static Position getCentroid(List<Unit> units) {
		if (units.isEmpty())
			return new Position(0, 0);

		List<Position> pos = new ArrayList<Position>(units.size());
		for (Unit me : units) {
			pos.add(me.getPosition());
		}

		int centroidX = 0, centroidY = 0;
		for (Position p : pos) {
			centroidX += p.getX();
			centroidY += p.getY();
		}

		return new Position(centroidX / pos.size(), centroidY / pos.size());
	}
}
