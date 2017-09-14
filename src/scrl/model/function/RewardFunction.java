package scrl.model.function;

import scrl.model.State;
import scrl.model.actions.Action;
import scrl.model.actions.Attack;
import scrl.model.actions.Explore;
import scrl.model.actions.Flee;

// FUNCAO DE CALCULO DE RECOMPENSA
public class RewardFunction {
	private static final double DEFAULT_REWARD = 100d;

	public static double getValue(final State state, State next, final Action action) {
		double diffAlliesUnits;
		double diffEnemyUnits;
		boolean noEnemiesNearby;
		noEnemiesNearby = state.getHpFromNearbyEnemies().getValue() == 0;
		
		// se acao escolhida for atacar
		if (action instanceof Attack) {
			// se nao tem nenhum inimigo por perto
			if (noEnemiesNearby) {
				return -DEFAULT_REWARD; // recompensa padrao negativa
			} else { // caso constrario, avaliar o dano da acao
				diffAlliesUnits = (state.getNumberOfAlliesUnitsNearby().getValue() - next.getNumberOfAlliesUnitsNearby().getValue());
				diffEnemyUnits = (state.getNumberOfEnemiesUnitsNearby().getValue() - next.getNumberOfEnemiesUnitsNearby().getValue());
				
				//
				if(diffAlliesUnits>0 || diffEnemyUnits<0)
				{ // Recompensa negativa
					return -hpDiff(state, next);
				}else{
					// Recompensa positiva
					return hpDiff(state, next);
				}
			}
			// se acao for explorar
		} else if (action instanceof Explore) { 
			return noEnemiesNearby ? DEFAULT_REWARD : -DEFAULT_REWARD ; // recompensa positiva caso nao haja unidades inimigas por perto, 
			//ruim caso contrario
			// se acao for fugir
		} else if (action instanceof Flee) {
			if (noEnemiesNearby) {
				return -DEFAULT_REWARD; // recompensa negativa caso nao haja unidades inimigas por perto,
				
				// Caso existam inimigos 
			} else {
				// VISANDO decidir se vale a pena fugir em uma dada situacao é

				// 
				if(state.getHpFromNearbyEnemies().getValue() > (1.5*state.getHpFromNearbyAllies().getValue()))
				{
					// Se FLEE && a vida de meus inimigos for maior doq 1,5 Vezes a minha, recompensa ruim
					// QUAL a ideia por tras disso ? 
					// Dar uma recompensa ruim pois talvez seja melhor "sacrificar" essas unidades
					// em detrimento a que elas morram enquanto fogem
					// Melhor que inflinjam algum dano do que apenas morrram correndo
					return -DEFAULT_REWARD;
				}
				else
				{
					return DEFAULT_REWARD;
				}

			}
		}
		return -DEFAULT_REWARD;
	}

	private static double hpDiff(final State state, State next) {
		double diffAllies = (state.getHpFromNearbyAllies().getValue() - next.getHpFromNearbyAllies().getValue());
		double diffEnemy = (state.getHpFromNearbyEnemies().getValue() - next.getHpFromNearbyEnemies().getValue());
		return (diffEnemy - diffAllies) * DEFAULT_REWARD;
	}
}