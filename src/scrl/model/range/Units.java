package scrl.model.range;

import java.io.Serializable;

public class Units implements Serializable {
	private static final long serialVersionUID = 1L;
	RangeUnits range;
	int value;

	public Units(RangeUnits range) {
		this.range = range;
	}

	public Units(int value) {
		this.value = value;
		this.range = RangeUnits.get(value);
	}

	public RangeUnits getRange() {
		return range;
	}

	public void setRange(RangeUnits range) {
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
		Units other = (Units) obj;
		if (range != other.range)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Units [range=" + range + ", value=" + value + "]";
	}

}
