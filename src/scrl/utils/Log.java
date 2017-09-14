package scrl.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import scrl.model.State;

public class Log {
	private static Log instance = null;

	public static final boolean DEBUG = false;
	private static final boolean printer = true;
	private File outFile;
	private BufferedWriter writer;

	public static Log getInstance() {
		if (instance == null) {
			instance = new Log();
		}
		return instance;
	}

	public static void log(String msg) {
		if (Log.DEBUG)
			System.out.println(msg);
		// getInstance().write(msg);
	}

	public void write(String msg) {
		if (DEBUG) {
			if (!outFile.isFile()) {
				try {
					outFile.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				writer.append(msg);
				writer.newLine();
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void endGame(Map<State, Long> statesCounter) {
		if (printer) {
			FileUtils.policyToFile();
			FileUtils.qTableToFile();
			FileUtils.statesCounterToFile(statesCounter);
		}
	}
}
