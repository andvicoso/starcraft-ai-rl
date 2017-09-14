package scrl.model.range;

public enum RangeHP  {
	LOW(0, 10), MEDIUM_LOW(10, 20), MEDIUM_HIGH(20, 30), HIGH(30, 40);
	double min;
	double max;

	private RangeHP(double min, double max) {
		this.min = min;
		this.max = max;
	}

	public static RangeHP get(double value) {
		for (RangeHP hp : values()) {
			if (hp.in(value))
				return hp;
		}
		return LOW;
	}

	public boolean in(double v) {
		return v > min && v <= max;
	}

}
