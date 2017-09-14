package scrl.model;

import java.io.Serializable;

import scrl.model.range.Distance;
import scrl.model.range.HP;
import scrl.model.range.RangeDistance;
import scrl.model.range.RangeHP;
import scrl.model.range.RangeUnits;
import scrl.model.range.Units;

// representacao do estado do ambiente
public class State implements Serializable {
	private static final long serialVersionUID = 7588180712283449263L;
	private HP hpFromNearbyEnemies; 
	private Units numberOfEnemiesUnitsNearby;
	private HP hpFromNearbyAllies;
	private Units numberOfAlliesUnitsNearby;
	private Distance distanceFromClosestEnemy;

	// construtor para quando a informacao vem em Objetos ja certos
	public State(HP hpFromNearbyEnemies, Units numberOfEnemiesUnitsNearby, HP hpFromNearbyAllies,
			Units numberOfAlliesUnitsNearby, Distance minDistanceFromEnemy) {
		this.hpFromNearbyEnemies = hpFromNearbyEnemies;
		this.numberOfEnemiesUnitsNearby = numberOfEnemiesUnitsNearby;
		this.hpFromNearbyAllies = hpFromNearbyAllies;
		this.numberOfAlliesUnitsNearby = numberOfAlliesUnitsNearby;
		this.distanceFromClosestEnemy = minDistanceFromEnemy;
	}

	// construtor para quando a informacao vem em valores inteiros e doubles
	public State(double hpFromNearbyEnemies, int numberOfEnemiesUnitsNearby, double hpFromNearbyAllies,
			int numberOfAlliesUnitsNearby, int minDistanceFromEnemy) {
		this.hpFromNearbyEnemies = new HP(hpFromNearbyEnemies);
		this.numberOfEnemiesUnitsNearby = new Units(numberOfEnemiesUnitsNearby);
		this.hpFromNearbyAllies = new HP(hpFromNearbyAllies);
		this.numberOfAlliesUnitsNearby = new Units(numberOfAlliesUnitsNearby);
		this.distanceFromClosestEnemy = new Distance(minDistanceFromEnemy);
	}

	// construtor para quando a informacao vem em RANGES
	public State(RangeHP hpFromNearbyEnemies, RangeUnits numberOfEnemiesUnitsNearby, RangeHP hpFromNearbyAllies,
			RangeUnits numberOfAlliesUnitsNearby, RangeDistance minDistanceFromEnemy) {
		this.hpFromNearbyEnemies = new HP(hpFromNearbyEnemies);
		this.numberOfEnemiesUnitsNearby = new Units(numberOfEnemiesUnitsNearby);
		this.hpFromNearbyAllies = new HP(hpFromNearbyAllies);
		this.numberOfAlliesUnitsNearby = new Units(numberOfAlliesUnitsNearby);
		this.distanceFromClosestEnemy = new Distance(minDistanceFromEnemy);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HpEnemies: ").append(hpFromNearbyEnemies).append(" N Enemies: ")
				.append(numberOfEnemiesUnitsNearby).append(" HpAllies: ").append(hpFromNearbyAllies)
				.append(" N Allies: ").append(numberOfAlliesUnitsNearby).append(" dist to closest: ")
				.append(distanceFromClosestEnemy);
		return builder.toString();
	}

	public Object toCSV() {
		StringBuilder builder = new StringBuilder();
		builder.append(hpFromNearbyEnemies).append(";").append(numberOfEnemiesUnitsNearby).append(";")
				.append(hpFromNearbyAllies).append(";").append(numberOfAlliesUnitsNearby).append(";")
				.append(distanceFromClosestEnemy).append(";");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((distanceFromClosestEnemy == null) ? 0 : distanceFromClosestEnemy.hashCode());
		result = prime * result + ((hpFromNearbyAllies == null) ? 0 : hpFromNearbyAllies.hashCode());
		result = prime * result + ((hpFromNearbyEnemies == null) ? 0 : hpFromNearbyEnemies.hashCode());
		result = prime * result + ((numberOfAlliesUnitsNearby == null) ? 0 : numberOfAlliesUnitsNearby.hashCode());
		result = prime * result + ((numberOfEnemiesUnitsNearby == null) ? 0 : numberOfEnemiesUnitsNearby.hashCode());
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
		State other = (State) obj;
		if (distanceFromClosestEnemy == null) {
			if (other.distanceFromClosestEnemy != null)
				return false;
		} else if (!distanceFromClosestEnemy.equals(other.distanceFromClosestEnemy))
			return false;
		if (hpFromNearbyAllies == null) {
			if (other.hpFromNearbyAllies != null)
				return false;
		} else if (!hpFromNearbyAllies.equals(other.hpFromNearbyAllies))
			return false;
		if (hpFromNearbyEnemies == null) {
			if (other.hpFromNearbyEnemies != null)
				return false;
		} else if (!hpFromNearbyEnemies.equals(other.hpFromNearbyEnemies))
			return false;
		if (numberOfAlliesUnitsNearby == null) {
			if (other.numberOfAlliesUnitsNearby != null)
				return false;
		} else if (!numberOfAlliesUnitsNearby.equals(other.numberOfAlliesUnitsNearby))
			return false;
		if (numberOfEnemiesUnitsNearby == null) {
			if (other.numberOfEnemiesUnitsNearby != null)
				return false;
		} else if (!numberOfEnemiesUnitsNearby.equals(other.numberOfEnemiesUnitsNearby))
			return false;
		return true;
	}

	public HP getHpFromNearbyEnemies() {
		return hpFromNearbyEnemies;
	}

	public Units getNumberOfEnemiesUnitsNearby() {
		return numberOfEnemiesUnitsNearby;
	}

	public HP getHpFromNearbyAllies() {
		return hpFromNearbyAllies;
	}

	public Units getNumberOfAlliesUnitsNearby() {
		return numberOfAlliesUnitsNearby;
	}

	public Distance getDistanceFromClosestEnemy() {
		return distanceFromClosestEnemy;
	}

}
