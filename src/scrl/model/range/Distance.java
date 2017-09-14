package scrl.model.range;

import java.io.Serializable;

public class Distance implements Serializable {
	private static final long serialVersionUID = 1L;
	RangeDistance range;
	int value;

	public Distance(RangeDistance range) {
		this.range = range;
	}

	public Distance(int value) {
		this.value = value;
		this.range = RangeDistance.get(value);
	}

	public RangeDistance getRange() {
		return range;
	}

	public void setRange(RangeDistance range) {
		this.range = range;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((range == null) ? 0 : range.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Distance other = (Distance) obj;
		if (range != other.range)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Distance [range=" + range + ", value=" + value + "]";
	}

}
