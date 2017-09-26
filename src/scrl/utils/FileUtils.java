package scrl.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import scrl.model.Policy;
import scrl.model.QTable;
import scrl.model.SCMDP;
import scrl.model.State;
import scrl.model.StateAction;

public abstract class FileUtils {

	public static void printOrganizedStateList() {
		Collection<State> stado = SCMDP.createStates();
		PrintWriter qwriter;
		try {
			qwriter = new PrintWriter("states.txt", "UTF-8");
			for (State unitState : stado) {
				qwriter.println(unitState.toCSV());
			}
			qwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void qTableToFile() {
		QTable qT;
		try {
			FileInputStream fis = new FileInputStream("marineTable.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			qT = (QTable) ois.readObject();
			try {
				PrintWriter qwriter = new PrintWriter("qTable.txt", "UTF-8");
				qwriter.println(qT.toString());
				qwriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void policyToFile() {
		List<StateAction> policyDataList = new ArrayList<>();
		QTable q;
		try {
			FileInputStream fis = new FileInputStream("marineTable.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			q = (QTable) ois.readObject();
			Policy p = q.getPolicy();
			for (StateAction stateAction : p) {
				policyDataList.add(stateAction);
			}
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			PrintWriter qwriter = new PrintWriter("policy.txt", "UTF-8");
			for (StateAction stateAction : policyDataList) {
				qwriter.println(stateAction.getState().toCSV() + " : " + stateAction.getActions().getClass().getSimpleName());
			}
			qwriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void statesCounterToFile(Map<State, Long> statesCounter) {
		PrintWriter scwriter;
		Iterator<?> it = statesCounter.entrySet().iterator();
		try {
			scwriter = new PrintWriter("statesCounter.txt", "UTF-8");
			while (it.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry pair = (Map.Entry) it.next();
				scwriter.println(pair.getKey() + " = " + pair.getValue());
				it.remove();
			}
			scwriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public static void statesSerialize(Map<State, Long> statesCounter) {
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

	public static Map<State, Long> deserializeStates() {
		Map<State, Long> map = null;
		try {
			FileInputStream fis = new FileInputStream("states.ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			map = (Map<State, Long>) ois.readObject();
			ois.close();
			fis.close();
		} catch (IOException | ClassNotFoundException ioe) {
			map = new HashMap<>();
		}
		return map;
	}
}
