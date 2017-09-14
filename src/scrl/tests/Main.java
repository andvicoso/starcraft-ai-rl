package scrl.tests;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bwapi.Color;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import scrl.model.State;
import scrl.model.StateAction;
import scrl.model.actions.Action;
import scrl.model.range.RangeDistance;
import scrl.rl.SCRL;
import scrl.utils.Log;

public class Main extends DefaultBWListener {

	public static final int MAX_GAMES = 1; // quantas partidas deseja executar
	public static int match = 0; // numero da partida atual

	// objeto para acessar informações do jogo
	private Mirror mirror = new Mirror();
	private Game game;
	private Player self;

	private SCRL rl;

	private Map<Unit, StateAction> units_running = new ConcurrentHashMap<>(); // unidades "em uso"
	private Map<String, Integer> counters = new HashMap<>(); // contadores variados
	public static Map<State, Long> statesCounter; // variavel que armazena o estado e quantas vezes ele foi visitado

	// inicializa o jogo
	public void run() {
		mirror.getModule().setEventListener(this);
		mirror.startGame();
	}

	@Override
	public void onStart() {
		game = mirror.getGame(); // gives you access to players, units as well as general information about the current
									// game
		self = game.self(); // Retrieves the player object that BWAPI is controlling.

		// BWTA: A Terrain Analyzer
		// https://code.google.com/archive/p/bwta/
		BWTA.readMap();
		BWTA.analyze();
		// This process usually takes up to three minutes, however the result is stored in bwapi-data/BWTA/, so next
		// time the analyzed map is played, BWTA fires up instantly

		// game.setGUI(false); // Passing false to this function will disable the GUI, and true will enable it.
		game.setLocalSpeed(15);// This changes the game speed to a given (integer) value. Maximum possible speed
								// corresponds to 0, while typical game speed is around 30.

		statesCounter = deserializeStates(); // deserializa a variavel que armazena o estado e quantas vezes ele foi
												// visitado

		rl = new SCRL(); // inicializa o aprendizado por reforço
		log("match N: " + match);

		rl.init(match); // deserializa a tabela Q e incrementa a variavel match
	}

	@Override
	public void onFrame() { // evento chamado a cada frame
		for (Unit unit : self.getUnits()) { // lista as unidades sob controle de self
			// drawAttackRange(unit);
			if (isAlive_Idle(unit)) { // verifica se a unidade esta viva ou sem acao
				// se chegou aqui, acabou de terminar de executar a acao passada
				// --> calcular a recompensa
				if (units_running.containsKey(unit)) { // verifica se a unidade esta no estrutura que armazena as
														// unidades "em uso"
					State newState = getCurrentState(unit); // le o estado atual
					StateAction sd = units_running.remove(unit); // remove a unidade do estrutura que armazena as
																	// unidades "em uso"
					rl.updateState(sd.action, sd.state, newState); // atualiza o estado, calcula a recompensa
					log(unit, "finished: " + sd.action);
				}
				State curState = getCurrentState(unit);// buscar estado atual
				Long auxCounter = (long) 0; // contador
				auxCounter = statesCounter.get(curState); // contador recebe a quantidade de vezes que o estado foi
															// visitado

				long val = 1;
				// incrementa em 1 a quantidade de visitas ao estado atual
				if (auxCounter != null) {
					val = auxCounter + 1;
				}

				statesCounter.put(curState, val);
				// log(statesCounter);

				Action actionToPerform = rl.getNextAction(curState); // seleciona a proxima acao a ser executada

				log(unit, "state: " + curState + " - frame: " + game.getFrameCount());
				executeAction(actionToPerform, unit); // manda executar a acao

				units_running.put(unit, new StateAction(curState, actionToPerform)); // adiciona a unidade do estrutura
																						// que armazena as unidades "em
																						// uso"
			}
		}
	}

	private void drawAttackRange(Unit unit) {
		game.drawCircleMap(unit.getPosition().getX(), unit.getPosition().getY(), RangeDistance.MARINE_ATTACK_RANGE, Color.Red);
		game.drawCircleMap(unit.getPosition().getX(), unit.getPosition().getY(), 2 * RangeDistance.MARINE_ATTACK_RANGE, Color.Yellow);
		game.drawCircleMap(unit.getPosition().getX(), unit.getPosition().getY(), 3 * RangeDistance.MARINE_ATTACK_RANGE, Color.Blue);
	}

	// verifica se a unidade existe e se esta "parada"
	private boolean isAlive_Idle(Unit unit) {
		return unit.exists() && !unit.isMoving() && !unit.isStartingAttack() && !unit.isAttacking();
	}

	@Override
	public void onEnd(boolean isWinner) { // evento chamado ao final de uma partida
		statesSerialize(); // serializa a variavel que armazena o estado e quantas vezes ele foi visitado
		rl.end(); // serializa a tabela Q
		match++;

		if (isWinner) { // verifica se "eu" ganhei a partida
			inc("winCounter");
		} else {
			inc("lossCounter");
		}

		if (match == MAX_GAMES) { // se cheguei ao numero de partidas desejadas, encerre
			for (String counterName : counters.keySet()) { // imprime os contadores
				System.out.println(counterName + ": " + counters.get(counterName));
			}
			Log.getInstance().endGame(statesCounter);
			System.exit(0);
		}
	}

	// funcao que le o ambiente e formula o estado na visao de uma dada unidade
	private State getCurrentState(Unit givenUnit) {
		Unit me = givenUnit; // unidade
		double contHpEnemyLife = 0.d; // somador da vida das unidades inimigas
		double contHpAlliesLife = 0.d; // somador da vida das unidades aliadas
		double mediumHpFromNearbyEnemies = 0.d; // variavel da vida media das unidades inimigas
		int numberOfEnemiesUnitsNearby = 0; // contador da quantidade de unidades inimigas proximas
		double mediumHpFromNearbyAllies = 0.d; // variavel da vida media das unidades aliada
		int numberOfAlliesUnitsNearby = 0; // contador da quantidade de unidades aliadas proximas
		int distanceToClosestEnemyUnit = 400000; // variavel de distancia em linha reta ( em pixels) para a unidade
													// inimiga mais proxima

		
		List<Unit> units = me.getUnitsInRadius(3 * RangeDistance.MARINE_ATTACK_RANGE); // get all the units that have
																						// any part of them within the
																						// given radius
		for (Unit unit : units) { // itera entre as unidades
			if (unit.exists()) { // checa se ela esta viva
				if (unit.getPlayer().isAlly(self)) { // verifica se é minha aliada
					// incrementa quantidade de unidades aliadas proximas e a vida delas
					contHpAlliesLife += unit.getHitPoints();
					numberOfAlliesUnitsNearby++;
				} else if (unit.getPlayer().isEnemy(self)) {
					// incrementa quantidade de unidades inimigas proximas e a vida delas
					contHpEnemyLife += unit.getHitPoints();
					numberOfEnemiesUnitsNearby++;
					// pega a distancia euclidiana em pixels da unidade inimiga mais proxima
					distanceToClosestEnemyUnit = me.getDistance(unit) < distanceToClosestEnemyUnit ? me.getDistance(unit) : distanceToClosestEnemyUnit;
				}
			}
		}

		// insere a unidade em questao na vida e na quantidade de unidades aliadas proximas
		contHpAlliesLife += me.getHitPoints();
		numberOfAlliesUnitsNearby++;

		// calcula o valor da vida media para inimigos e aliados
		if (numberOfEnemiesUnitsNearby != 0)
			mediumHpFromNearbyEnemies = contHpEnemyLife / numberOfEnemiesUnitsNearby;
		if (numberOfAlliesUnitsNearby != 0)
			mediumHpFromNearbyAllies = contHpAlliesLife / numberOfAlliesUnitsNearby;

		// forma o estado corrente a partir das leituras feitas acima
		State curState = new State(mediumHpFromNearbyEnemies, numberOfEnemiesUnitsNearby, mediumHpFromNearbyAllies, numberOfAlliesUnitsNearby,
				distanceToClosestEnemyUnit);

		return curState;
	}

	public static void main(String[] args) {
		new Main().run(); // registers the listeners and starts waiting for the game by calling mirror.startGame().
	}

	private void inc(String counterName) {
		if (!counters.containsKey(counterName))
			counters.put(counterName, 0);
		counters.put(counterName, counters.get(counterName) + 1);
	}

	public void executeAction(Action actionToPerform, Unit me) {
		log("[" + me.getID() + "] started: " + actionToPerform);
		inc("actionCounter");
		inc(actionToPerform.getClass().getSimpleName() + "OrderCounter");
		actionToPerform.execute(game, me);
	}

	private void statesSerialize() {
		try {
			FileOutputStream fos = new FileOutputStream("states.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(statesCounter);
			oos.close();
			fos.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void log(Unit u, String msg) {
		log("[" + u.getID() + "] " + msg);
	}

	private void log(String msg) {
		Log.log(msg);
	}

	private Map<State, Long> deserializeStates() {
		Map<State, Long> map = null;
		try {
			FileInputStream fis = new FileInputStream("states.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (HashMap) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException | ClassNotFoundException ioe) {
			map = new HashMap<>();
		}
		return map;
	}
}