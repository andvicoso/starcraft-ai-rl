package scrl.model.actions;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import scrl.model.range.RangeDistance;

public class Explore extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Random generator = new Random();

	// Executor da acao
	@Override
	public void execute(Game game) {
		List<Unit> units = game.self().getUnits();
		for (Unit unit : units) {
			Position exploreLocation = getExploreLocation(game, unit); // escolhe lugar para exploração
			unit.move(exploreLocation, false);
		}
	}

	// ( OBJETIVA-SE MANTER CLUSTERS de UNIDADES ALIADAS)
	private Position getExploreLocation(Game game, Unit unit) {
		Position exploreLocation = null;
		int myUnitX = unit.getPosition().getX();
		int myUnitY = unit.getPosition().getY();
		int numberofAlliedUnitsOnUpperRight = 0;
		int numberofAlliedUnitsOnLowerRight = 0;
		int numberofAlliedUnitsOnUpperLeft = 0;
		int numberofAlliedUnitsOnLowerLeft = 0;
		int numberOfAlliedUnits = 0;
		int alliedUnitX = 0;
		int alliedUnitY = 0;
		double dist = 0.0;

		// lista as unidades em 3 Ranges
		for (Unit unitToVerify : unit.getUnitsInRadius(3 * RangeDistance.MARINE_ATTACK_RANGE)) {
			if (unitToVerify.exists()) {
				// verifica se a unidade é aliada
				if (unitToVerify.getPlayer().isAlly(game.self())) {
					numberOfAlliedUnits++;
					// pego X e Y da unidade aliada
					alliedUnitX = unitToVerify.getPosition().getX();
					alliedUnitY = unitToVerify.getPosition().getY();
					dist += unit.getDistance(unitToVerify);

					// Cria 4 quadrantes, tendo a unidade como centro,
					// Quadrantes são:
					// Direito Cima, Direito Baixo e espelho
					if (alliedUnitX > myUnitX) {
						if (alliedUnitY > myUnitY) {
							numberofAlliedUnitsOnUpperRight++;
						} else {
							numberofAlliedUnitsOnLowerRight++;
						}
					} else {
						if (alliedUnitY > myUnitY) {
							numberofAlliedUnitsOnUpperLeft++;
						} else {
							numberofAlliedUnitsOnLowerLeft++;
						}
					}
				}
			}
		}

		int low = -RangeDistance.MARINE_ATTACK_RANGE;
		int high = RangeDistance.MARINE_ATTACK_RANGE;
		int aux1;
		int aux2;

		// Faço até 10 tentativas de encontrar um bom lugar para explorar
		for (int cont = 0; cont < 11; cont++) {
			// gera valores aleatórios entre o high e low
			aux1 = generator.nextInt(high - low) + low;
			aux2 = generator.nextInt(high - low) + low;
			// cria a variavel de posicao do mapa com esses valores aleatórios
			exploreLocation = new bwapi.Position(myUnitX + aux1, myUnitY + aux2);
			// tenta criar a variavel de posicao do mapa com valores baseados na quantidade de unidades nos quadrantes
			// criados anteriormente
			// Verifica onde tem mais unidades aliadas, na direita ou na esquerda
			if (numberofAlliedUnitsOnUpperRight > numberofAlliedUnitsOnUpperLeft || numberofAlliedUnitsOnLowerRight > numberofAlliedUnitsOnLowerLeft) {
				// Direita
				// verifica se tem mais unidades aliadas para cima ou para baixo
				if (numberofAlliedUnitsOnLowerLeft > numberofAlliedUnitsOnUpperLeft || numberofAlliedUnitsOnLowerRight > numberofAlliedUnitsOnUpperRight) {
					// Baixo
					// exploreLocation = new bwapi.Position(myUnitX + (int) (dist / numberOfAlliedUnits), myUnitY -
					// (int) (dist / numberOfAlliedUnits));
					// desloca a unidade em valores aleatórios em até 1 RANGE
					exploreLocation = new bwapi.Position(myUnitX + (int) (aux1), myUnitY - (int) (aux2));
				} else if (numberofAlliedUnitsOnUpperLeft > numberofAlliedUnitsOnLowerLeft
						|| numberofAlliedUnitsOnUpperRight > numberofAlliedUnitsOnLowerRight) {
					// Cima
					// desloca a unidade em valores aleatórios em até 1 RANGE
					// exploreLocation = new bwapi.Position(myUnitX + (int) (dist / numberOfAlliedUnits), myUnitY +
					// (int) (dist / numberOfAlliedUnits));
					exploreLocation = new bwapi.Position(myUnitX + (int) (aux1), myUnitY + (int) (aux2));
				}

			} else if (numberofAlliedUnitsOnUpperLeft > numberofAlliedUnitsOnUpperRight || numberofAlliedUnitsOnLowerLeft > numberofAlliedUnitsOnLowerRight) {
				// esquerda
				if (numberofAlliedUnitsOnLowerLeft > numberofAlliedUnitsOnUpperLeft || numberofAlliedUnitsOnLowerRight > numberofAlliedUnitsOnUpperRight) {
					// baixo
					// exploreLocation = new bwapi.Position(myUnitX - (int) (dist / numberOfAlliedUnits), myUnitY -
					// (int) (dist / numberOfAlliedUnits));
					// desloca a unidade em valores aleatórios em até 1 RANGE
					exploreLocation = new bwapi.Position(myUnitX - (int) (aux1), myUnitY - (int) (aux2));
				} else if (numberofAlliedUnitsOnUpperLeft > numberofAlliedUnitsOnLowerLeft
						|| numberofAlliedUnitsOnUpperRight > numberofAlliedUnitsOnLowerRight) {
					// cima
					// exploreLocation = new bwapi.Position(myUnitX - (int) (dist / numberOfAlliedUnits), myUnitY +
					// (int) (dist / numberOfAlliedUnits));
					// desloca a unidade em valores aleatórios em até 1 RANGE
					exploreLocation = new bwapi.Position(myUnitX - (int) (aux1), myUnitY + (int) (aux2));
				}
			}

			// verifica se o local é um local valido na visao do analisador de terreno do jogo.
			if (exploreLocation.isValid())
				return exploreLocation;
		}
		return exploreLocation;
	}

	// fora de uso, Objetivo Clusterizar a acao de explorar
	Boolean willUnitsBeKeptClose(Game game, Unit unit, Position exploreLocation) {
		List<Unit> unitsIn3Range = unit.getUnitsInRadius(3 * RangeDistance.MARINE_ATTACK_RANGE);
		int cont = 0;
		Integer distSum = 0;
		for (Unit unit2 : unitsIn3Range) {
			if (unit2.exists()) {
				if (unit2.getPlayer().isAlly(game.self())) {
					if (unit2.getID() != unit.getID()) {
						if (unit2.getDistance(exploreLocation) <= 3 * RangeDistance.MARINE_ATTACK_RANGE) {
							cont++;
							distSum += unit2.getDistance(exploreLocation);
						}
					}
				}

			}
		}
		if (distSum / cont <= 3 * RangeDistance.MARINE_ATTACK_RANGE) {
			return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Explore))
			return false;
		return Objects.equals(this.getClass().getSimpleName(), other.getClass().getSimpleName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getClass().getSimpleName());
	}
}
