
public class Logger {
	// Синглтон
	static public Logger GetLogger() {
		if( pLogger == null ) {
			pLogger = new Logger();
		}
		return pLogger;
	}

	public void WriteLine( String text ) {
		pLogger.writeLine( text );
	}
	
	// -------- PRIVATE ---------------------
	private Logger() {}
	static private Logger pLogger = null;
	private void writeLine( String text ) {
		System.out.println( text );
	}
}
