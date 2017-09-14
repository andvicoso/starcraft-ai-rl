package scrl.rl;

import java.io.File;
import java.io.Serializable;

import scrl.model.SCMDP;
import scrl.model.State;
import scrl.model.actions.Action;
import scrl.model.actions.EGreedyActionChooser;
import scrl.rl.algorithm.AbstractLearning;
import scrl.rl.algorithm.DynaQ;
import scrl.rl.algorithm.QLearning;

public class SCRL implements Serializable {

	private static final long serialVersionUID = 7537131060045100702L;

	private SCMDP model;
	private AbstractLearning learning;
	private EGreedyActionChooser actionChooser;

	public SCRL() {
		model = new SCMDP();
		
		// duas opcoes de aprendizado dispniveis no momento
		learning = new QLearning(model);
		//learning = new DynaQ(model);
		actionChooser = new EGreedyActionChooser(learning.getQTable()); // instancia o objeto que busca a nova acao
	}

	// deserializa a Q tabela para Marine
	public void init(int matchNumber) {
		File f = new File("marineTable.ser");
		if (f.exists())
			learning.deserialize();
	}

	// atualiza a tabela Q
	public void updateState(Action action, State curState, State newState) {
		learning.updateQ(curState, newState, action);
	}

	// seleciona a proxima acao
	public Action getNextAction(State pState) {
		return actionChooser.getAction(pState); // seleciona a proxima acao
	}

	public void end() {
		learning.serialize();
	}

	public SCMDP getModel() {
		return model;
	}

	public void setModel(SCMDP model) {
		this.model = model;
	}

	public AbstractLearning getLearning() {
		return learning;
	}

	public void setLearning(DynaQ learning) {
		this.learning = learning;
	}

}
