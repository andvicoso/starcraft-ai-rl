package scrl.model.range;

public enum RangeDistance {
	CLOSE, MEDIUM, FAR;
	public static final int MARINE_ATTACK_RANGE = 75; // VALOR Avaliado por olho
	public static final int MARINE_MOVE_RANGE = 32; // valor por tile

	public static RangeDistance get(int distanceToClosestEnemyUnit) {
		if (distanceToClosestEnemyUnit <= MARINE_ATTACK_RANGE)
			return CLOSE;
		else if (distanceToClosestEnemyUnit <= 2 * MARINE_ATTACK_RANGE)
			return MEDIUM;
		else
			return FAR;
	}
}
