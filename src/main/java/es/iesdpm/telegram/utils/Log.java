package es.iesdpm.telegram.utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
	
	private static final Logger LOG = createLogger("MinikBot.log");
			
	private static Logger createLogger(String filename) {
		try {
			Logger logger = Logger.getLogger(Log.class.getName());
			logger.addHandler(new FileHandler(filename, true));
			return logger;
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	private static void log(Level level, String msg, Object ... params) {
		LOG.log(level, msg, params);
	}
	
	public static void info(String msg, Object ... params) {
		log(Level.INFO, msg, params);
	}

	public static void error(String msg, Object ... params) {
		log(Level.SEVERE, msg, params);
	}

	public static void warn(String msg, Object ... params) {
		log(Level.WARNING, msg, params);
	}
	
}
