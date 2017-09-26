package scrl.model.actions;

import java.util.Objects;

import bwapi.Game;

public abstract class Action {

	public abstract void execute(Game game);

	public abstract int getNumberOfFrames(Game game);

	@Override
	public String toString() { // Usado para obter apenas o nome simples da acao, ATTACK, FLEE, EXPLORE
		return getClass().getSimpleName();
	}

	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof ExploreCentroid))
			return false;
		return Objects.equals(this.getClass().getSimpleName(), other.getClass().getSimpleName());
	}

	public int hashCode() {
		return Objects.hash(this.getClass().getSimpleName());
	}

}
