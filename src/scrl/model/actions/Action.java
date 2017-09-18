package scrl.model.actions;

import bwapi.Game;

public abstract class Action {

	public abstract void execute(Game game);

	public int getNumberOfFrames(Game game) {
		return 12;
	}

	public abstract boolean equals(Object other);

	public abstract int hashCode();

	@Override
	public String toString() { // Usado para obter apenas o nome simples da acao, ATTACK, FLEE, EXPLORE
		return getClass().getSimpleName();
	}

}
