package scrl.model.range;

import java.io.Serializable;

public class HP implements Serializable {
	private static final long serialVersionUID = 1L;
	RangeHP range;
	double value;

	public HP(double value) {
		this.value = value;
		this.range = RangeHP.get(value);
	}

	public HP(RangeHP range) {
		this.range = range;
		this.value = 0;
	}

	public RangeHP getRange() {
		return range;
	}

	public void setRange(RangeHP range) {
		this.range = range;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
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
		HP other = (HP) obj;
		if (range != other.range)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HP [range=" + range + ", value=" + value + "]";
	}

}
