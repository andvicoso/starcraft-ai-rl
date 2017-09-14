package scrl.model;

public class Model {

	private State nextState;
	private Double reward;

	// estrutura de dados utilizada para calculo
	public Model(State nextState, Double reward) {
		this.nextState = nextState;
		this.reward = reward;
	}

	public State getNextState() {
		return nextState;
	}

	public void setNextState(State nextState) {
		this.nextState = nextState;
	}

	public Double getReward() {
		return reward;
	}

	public void setReward(Double reward) {
		this.reward = reward;
	}

}
