import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;


public class ClientMain {

	private static Logger logger;
	private static Socket s;
	private static String serverIpAddress = "192.168.0.101"; 

	public static void main(String[] args) {
		
		logger = Logger.GetLogger();
		
		int serverPort[] = { 12346, 12347 };	
		try{
			if( !attemptToOpen( serverPort[0] ) ) {
				if( !attemptToOpen( serverPort[1] ) ) {
					throw new Exception("Cannot open connection");					
				}
			}
			
			InputStream is = s.getInputStream();
			int bufLength = 36;
			byte buf[] = new byte[bufLength];
			int r;
			System.out.println("start:");
			while(true){
				
				if(s.isClosed() || !s.isConnected()){
					logger.WriteLine("socket is closed");
					break;
				}
				r = is.read(buf);
				if(r == -1 ) continue; // nothing to read

				logger.WriteLine("readed package length = " + r);
				Byte length = new Byte(buf[0]);
				logger.WriteLine("real package length = " + length);
				
				// Sensor id, for example 426 = accelerometer
				byte[] tempId = {buf[1], buf[2]};
				ByteBuffer wrappedId = ByteBuffer.wrap(tempId); // big-endian by default
				short idSensor = wrappedId.getShort();
				String sensorName = getSensorName(idSensor);
				logger.WriteLine("idSensor = " + sensorName);
				
				// time stamp of sensor data
				byte[] tempTimeStamp = {buf[3], buf[4], buf[5], buf[6], buf[7], buf[8], buf[9], buf[10]};
				ByteBuffer wrappedTimeStamp = ByteBuffer.wrap(tempTimeStamp); // big-endian by default
				long timeStamp = wrappedTimeStamp.getLong();
				logger.WriteLine("timeStamp = " + timeStamp);
				
				// coordinates: x y z of gyroscope and accelerometer
				byte[] tempX = new byte[8];
				for(int i = 11; i <=18; ++i ) tempX[i-11] = buf[i];		
				ByteBuffer wrappedX = ByteBuffer.wrap(tempX); // big-endian by default
				double x = wrappedX.getDouble();
				logger.WriteLine("x = " + x + ", ");
				
				byte[] tempY = new byte[8];
				for(int i = 19; i <= 26; ++i ) tempY[i-19] = buf[i];
				ByteBuffer wrappedY = ByteBuffer.wrap(tempY); // big-endian by default
				double y = wrappedY.getDouble();
				logger.WriteLine("y = " + y + ", ");
				
				byte[] tempZ = new byte[8];
				for(int i = 27; i <= 34; ++i ) tempZ[i-27] = buf[i];
				ByteBuffer wrappedZ = ByteBuffer.wrap(tempZ); // big-endian by default
				double z = wrappedZ.getDouble();
				logger.WriteLine("z = " + z);
				
				// count checksum and verify
				byte checkSumFromPackage = buf[35];
				byte realCheckSun = getCheckSum(buf, bufLength - 1 );
				if( checkSumFromPackage == realCheckSun ) logger.WriteLine("CheckSum is OK");
				else logger.WriteLine("CheckSum is WRONG");
				
				logger.WriteLine("");
				//Thread.sleep(4000);
			}
		} catch(Exception e) {
			logger.WriteLine( e.getMessage() );
		}
		System.out.println("end");
	}
	
    private static byte getCheckSum( byte[] buffer, int length )
    {
        byte xor = buffer[0];
        for( int i = 1; i < length; ++i ) {
            xor ^= buffer[i];
        }
        return xor;
    }
	
	private static String getSensorName( short idSensor)
	{
		if( idSensor == 0x1A9 ) return "GYROSCOPE";
		if( idSensor == 0x1AA ) return "ACCELEROMETER";
		return "WRONG SENSOR";
	}
	
	private static boolean attemptToOpen( int port ) {
		try{
			s = new Socket(serverIpAddress, port);
		}catch(Exception e) {
			logger.WriteLine( e.getMessage() );
			return false;
		}	
		logger.WriteLine("port " + Integer.toString(port) + " connected");
		return true;
	}

}
