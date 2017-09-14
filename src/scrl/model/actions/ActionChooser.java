package scrl.model.actions;

import scrl.model.State;

public interface ActionChooser {

	Action getAction(State state);
}
