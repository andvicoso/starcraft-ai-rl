package scrl.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import scrl.Main;
import scrl.model.State;

public class Log {
	private static Log instance = null;

	public static final boolean DEBUG = true;
	public static final boolean VISUAL_DEBUG = Main.GAME_SPEED > 20;
	private static final boolean printer = true;
	private File outFile;
	private BufferedWriter writer;

	private Log() throws IOException {
		outFile = new File("Log.txt");
		writer = new BufferedWriter(new FileWriter(outFile));
	}

	public static Log getInstance() {
		if (instance == null) {
			try {
				instance = new Log();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public static void log(String msg) {
		if (Log.DEBUG) {
			getInstance().write(msg);
			System.out.println(msg);
		}
	}

	private void write(String msg) {
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
