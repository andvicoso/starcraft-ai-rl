package scrl;

import java.util.HashMap;
import java.util.Map;

import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Mirror;
import bwapi.Player;
import bwapi.Unit;
import bwta.BWTA;
import scrl.model.State;
import scrl.model.actions.Action;
import scrl.model.range.RangeDistance;
import scrl.rl.SCRL;
import scrl.utils.FileUtils;
import scrl.utils.LocationUtils;
import scrl.utils.Log;

public class Main extends DefaultBWListener {

	public static final int MAX_GAMES = 100; // quantas partidas deseja executar
	public static final int GAME_SPEED = 0; // Maximum speed = 0, typical game speed is around 30.
	public static int match = 0; // numero da partida atual

	// objeto para acessar informações do jogo
	private Mirror mirror = new Mirror();
	private Game game;
	private Player self;

	private SCRL rl;

	private Map<String, Integer> counters = new HashMap<>(); // contadores variados
	private Action currentAction = null;
	private State currentState = null;
	private int framesToGo;
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
		game.setLocalSpeed(GAME_SPEED);// This changes the game speed to a given (integer) value.

		statesCounter = FileUtils.deserializeStates(); // recupera os estados e quantas vezes ele foi visitado

		rl = new SCRL(); // inicializa o aprendizado por reforço
		log("match N: " + match);

		rl.init(match); // deserializa a tabela Q e incrementa a variavel match
	}

	@Override
	public void onFrame() { // evento chamado a cada frame
		if (framesToGo == 0) {
			// log("finished: " + currentAction);
			if (currentAction != null) {
				State newState = getCurrentState(); // le o estado atual
				// atualiza o estado, calcula a recompensa unidades "em uso"
				rl.updateState(currentAction, currentState, newState);
			}

			currentState = getCurrentState();// buscar estado atual
			updateCounters(currentState);

			currentAction = rl.getNextAction(currentState); // seleciona a proxima acao a ser executada
			// log("state: " + currentState + " - frame: " + game.getFrameCount());
			executeAction(currentAction); // manda executar a acao
		} else
			framesToGo--;
	}

	private void updateCounters(State curState) {
		Long auxCounter = (long) 0; // contador
		auxCounter = statesCounter.get(curState); // quantidade de vezes que o estado foi visitado

		long val = 1;
		// incrementa em 1 a quantidade de visitas ao estado atual
		if (auxCounter != null) {
			val = auxCounter + 1;
		}

		statesCounter.put(curState, val);
		// log(statesCounter);
	}

	@Override
	public void onEnd(boolean isWinner) { // evento chamado ao final de uma partida
		FileUtils.statesSerialize(statesCounter); // guarda os estados e quantas vezes ele foi visitado
		rl.end(); // serializa a tabela Q
		match++;

		if (isWinner) { // verifica se "eu" ganhei a partida
			inc("winCounter");
			log("******************************WIN******************************");
		} else {
			inc("lossCounter");
		}

		if (match == MAX_GAMES) { // se cheguei ao numero de partidas desejadas, encerre
			for (String counterName : counters.keySet()) { // imprime os contadores
				log(counterName + ": " + counters.get(counterName));
			}
			Log.getInstance().endGame(statesCounter);
			System.exit(0);
		}
	}

	// funcao que le o ambiente e formula o estado na visao de uma dada unidade
	private State getCurrentState() {
		double contHpEnemyLife = 0.d; // somador da vida das unidades inimigas
		double contHpAlliesLife = 0.d; // somador da vida das unidades aliadas
		double mediumHpFromNearbyEnemies = 0.d; // variavel da vida media das unidades inimigas
		int numberOfEnemiesUnitsNearby = 0; // contador da quantidade de unidades inimigas proximas
		double mediumHpFromNearbyAllies = 0.d; // variavel da vida media das unidades aliada
		int numberOfAlliesUnitsNearby = 0; // contador da quantidade de unidades aliadas proximas

		for (Unit unit : game.getUnitsInRadius(LocationUtils.getCentroidAllies(self.getUnits()), 3 * RangeDistance.MARINE_ATTACK_RANGE)) {
			if (unit.exists()) { // checa se ela esta viva
				if (unit.getPlayer().isAlly(self)) { // verifica se é minha aliada
					// incrementa quantidade de unidades aliadas proximas e a vida delas
					contHpAlliesLife += unit.getHitPoints();
					numberOfAlliesUnitsNearby++;
				} else if (unit.getPlayer().isEnemy(self)) {
					// incrementa quantidade de unidades inimigas proximas e a vida delas
					contHpEnemyLife += unit.getHitPoints();
					numberOfEnemiesUnitsNearby++;
				}
			}
		}

		// calcula o valor da vida media para inimigos e aliados
		if (numberOfEnemiesUnitsNearby != 0)
			mediumHpFromNearbyEnemies = contHpEnemyLife / numberOfEnemiesUnitsNearby;
		if (numberOfAlliesUnitsNearby != 0)
			mediumHpFromNearbyAllies = contHpAlliesLife / numberOfAlliesUnitsNearby;

		// forma o estado corrente a partir das leituras feitas acima
		return new State(mediumHpFromNearbyEnemies, numberOfEnemiesUnitsNearby, mediumHpFromNearbyAllies, numberOfAlliesUnitsNearby);
	}

	public static void main(String[] args) {
		new Main().run(); // registers the listeners and starts waiting for the game by calling mirror.startGame().
	}

	private void inc(String counterName) {
		if (!counters.containsKey(counterName))
			counters.put(counterName, 0);
		counters.put(counterName, counters.get(counterName) + 1);
	}

	public void executeAction(Action actionToPerform) {
		if (Log.VISUAL_DEBUG) {
			game.drawTextScreen(0, 0, actionToPerform.getClass().getSimpleName());
		}
		// log("started: " + actionToPerform);
		inc("actionCounter");
		inc(actionToPerform.getClass().getSimpleName() + "Counter");
		framesToGo = currentAction.getNumberOfFrames(game);
		actionToPerform.execute(game);
	}

	private void log(String msg) {
		Log.log(msg);
	}
}