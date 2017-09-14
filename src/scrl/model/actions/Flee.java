package scrl.model.actions;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import bwapi.Game;
import bwapi.Position;
import bwapi.Unit;
import scrl.model.range.RangeDistance;

public class Flee extends Action implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private Random generator = new Random();

	public void execute(Game game, Unit unit) {
		Position safePlace = getSaferPlace(game, unit); // busca qual seria o local mais seguro para fugir
		if (unit.exists()) {unit.move(safePlace,false);}
	}

	private Position getSaferPlace(Game game, Unit unit) {
		int myUnitX = unit.getPosition().getX();
		int myUnitY = unit.getPosition().getY();
		int numberofEnemiesOnUpperRight = 0;
		int numberofEnemiesOnLowerRight = 0;
		int numberofEnemiesOnUpperLeft = 0;
		int numberofEnemiesOnLowerLeft = 0;
		int enemyX = 0;
		int enemyY = 0;
		double dist = 0.0;
		int numberOfEnemyUnits;
		numberOfEnemyUnits = 0;

		// lista as unidades em 3 Range
		for (Unit unitToVerify : unit.getUnitsInRadius(3 * RangeDistance.MARINE_ATTACK_RANGE)) {
				if (unitToVerify.exists()) {
					// verifica se a unidade é inimiga
					if (!unitToVerify.getPlayer().isAlly(game.self())) {
						numberOfEnemyUnits++;
						// pega X e y da unidade inimiga
						enemyX = unitToVerify.getPosition().getX();
						enemyY = unitToVerify.getPosition().getY();
						dist += unit.getDistance(unitToVerify);
						// Cria 4 quadrantes, tendo a unidade como centro,
						// Quadrantes são: 
						// Direito Cima, Direito Baixo e espelho
						if (enemyX > myUnitX) {
							if (enemyY > myUnitY) {
								numberofEnemiesOnUpperRight++;
							} else {
								numberofEnemiesOnLowerRight++;
							}
						} else {
							if (enemyY > myUnitY) {
								numberofEnemiesOnUpperLeft++;
							} else {
								numberofEnemiesOnLowerLeft++;
							}
						}
					}
			}
		}

		int low = -4 *RangeDistance.MARINE_ATTACK_RANGE;
		int high = 4 *RangeDistance.MARINE_ATTACK_RANGE;
		int aux1;
		int aux2;
		Position safePlace = null;
		// Faço até 10 tentativas de encontrar um bom lugar para explorar
		for(int cont = 0; cont<11; cont++)
		{
			// gera valores aleatórios entre o high e low
			aux1 = generator.nextInt(high - low) + low;
			aux2 = generator.nextInt(high - low) + low;

			safePlace = new bwapi.Position(myUnitX + aux1, myUnitY + aux2);
			
			// Verifica onde tem mais unidades Inimigas, na direita ou na esquerda
			if (numberofEnemiesOnUpperRight > numberofEnemiesOnUpperLeft
					|| numberofEnemiesOnLowerRight > numberofEnemiesOnLowerLeft) {
				// esquerda
				// verifica se tem mais unidades aliadas para cima ou para baixo
				if (numberofEnemiesOnLowerLeft > numberofEnemiesOnUpperLeft
						|| numberofEnemiesOnLowerRight > numberofEnemiesOnUpperRight) {
					// cima
					safePlace = new bwapi.Position(myUnitX - (int) (dist / numberOfEnemyUnits),
							myUnitY + (int) (dist / numberOfEnemyUnits));
					// verifica se tem mais unidades aliadas para cima ou para baixo
				} else if (numberofEnemiesOnUpperLeft > numberofEnemiesOnLowerLeft
						|| numberofEnemiesOnUpperRight > numberofEnemiesOnLowerRight) {
					// baixo
					safePlace = new bwapi.Position(myUnitX - (int) (dist / numberOfEnemyUnits),
							myUnitY - (int) (dist / numberOfEnemyUnits));
				}
			} else if (numberofEnemiesOnUpperLeft > numberofEnemiesOnUpperRight
					|| numberofEnemiesOnLowerLeft > numberofEnemiesOnLowerRight) {
				// direita
				if (numberofEnemiesOnLowerLeft > numberofEnemiesOnUpperLeft
						|| numberofEnemiesOnLowerRight > numberofEnemiesOnUpperRight) {
					// cima
					safePlace = new bwapi.Position(myUnitX + (int) (dist / numberOfEnemyUnits),
							myUnitY + (int) (dist / numberOfEnemyUnits));
				} else if (numberofEnemiesOnUpperLeft > numberofEnemiesOnLowerLeft
						|| numberofEnemiesOnUpperRight > numberofEnemiesOnLowerRight) {
					// baixo
					safePlace = new bwapi.Position(myUnitX + (int) (dist / numberOfEnemyUnits),
							myUnitY - (int) (dist / numberOfEnemyUnits));
				}
			}
			
			// verifica se o local é um local valido na visao do analisador de terreno do jogo.
			if(safePlace.isValid())
				return safePlace;
		}
		return safePlace;
	}

	// fora de uso, Objetivo Clusterizar a acao de explorar
	Boolean willUnitsBeKeptClose(Game game, Unit unit, Position safePlace) {
		List<Unit> unitsIn3Range = unit.getUnitsInRadius(3*RangeDistance.MARINE_ATTACK_RANGE);
		//List<Unit> myUnits = unit.getUnitsInRadius(2 * RangeDistance.MARINE_ATTACK_RANGE);
//		game.drawCircleMap(unit.getPosition().getX(), unit.getPosition().getY(), 2 * RangeDistance.MARINE_ATTACK_RANGE,
//				Color.Blue);
		int cont = 0;
		int distSum = 0;
		for (Unit unit2 : unitsIn3Range) {
			if (unit2.exists()) {
				if (unit2.getPlayer().isAlly(game.self())) {
					if (unit2.getID() != unit.getID()) {
						if(unit2.getDistance(safePlace) <= 3 *RangeDistance.MARINE_ATTACK_RANGE)
						{
							cont++;
							distSum+=unit2.getDistance(safePlace);
						}
					}
				}
			}
		}
		if(distSum / cont <= 3*RangeDistance.MARINE_ATTACK_RANGE)
		 {
			 return true;
		 }
		return false;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Flee))
			return false;
		return Objects.equals(this.getClass().getSimpleName(), other.getClass().getSimpleName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getClass().getSimpleName());
	}
}
