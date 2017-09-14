package scrl.model.range;

public enum RangeUnits {
	SMALL(0, 2), MEDIUM(2, 6), LARGE(6, 200);
	double min;
	double max;

	RangeUnits(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public static RangeUnits get(double value) {
		for (RangeUnits hp : values()) {
			if (hp.in(value))
				return hp;
		}
		return SMALL;
	}

	public boolean in(double v) {
		return v > min && v <= max;
	}
}