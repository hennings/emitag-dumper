package net.spjelkavik.emit.ept;


import java.io.*;
import java.util.*;
import javax.comm.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version 1.8, 08/03/00
 */
public class Read250 implements Runnable, SerialPortEventListener {
	static CommPortIdentifier portId;
	static Enumeration	      portList;
	InputStream		      inputStream;
	SerialPort		      serialPort;
	Thread		      readThread;

	/**
	 * Method declaration
	 *
	 *
	 * @param args
	 *
	 * @see
	 */
	public static void main(String[] args) {
		boolean		      portFound = false;

		if (args.length > 0) {
			defaultPort = args[0];
		} 

	} 
	static String defaultPort = "COM2";
	
	public static boolean findPort(String defaultPort) {
		portList = CommPortIdentifier.getPortIdentifiers();
		System.out.println("Enumerator: " + portList);
		int n = 0;
		boolean portFound = false;
		while (portList.hasMoreElements()) {
			n++;
			portId = (CommPortIdentifier) portList.nextElement();
			System.out.println("Port: " + portId + " - " + portId.getName());
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				if (portId.getName().equals(defaultPort)) {
					portFound  = true;
					return portFound;
				} 
			} 
		} 
		if (!portFound) {
			System.out.println("port " + defaultPort + " not found.");
			System.exit(-1);
		} 
		return portFound;
	}

	/**
	 * Constructor declaration
	 *
	 *
	 * @see
	 */
	public Read250() {
		try {
			System.out.println("Opening " +portId+", "+ portId.getName());
			serialPort = (SerialPort) portId.open("SimpleReadApp",32000);
		} catch (PortInUseException e) {
			System.err.println("ProblemS: " + e);
		}

		try {
			inputStream = serialPort.getInputStream();
		} catch (IOException e) {}

		try {
			serialPort.addEventListener(this);
		} catch (TooManyListenersException e) {}

		serialPort.notifyOnDataAvailable(true);

		try {
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, 
					SerialPort.STOPBITS_2, 
					SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {}

		readThread = new Thread(this);
		readThread.setDaemon(true);
		readThread.start();
	}

	/**
	 * Method declaration
	 *
	 *
	 * @see
	 */
	public void run() {
		System.err.println("run");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {}
		System.err.println("run..exit");
	} 

	int totNr = 0;
	
	List<Frame> frames = new ArrayList<Frame>();
	Frame frame = new Frame();
	Frame prevFrame = null;
	private BadgeListener badgeListener;
	
	int prev = -1;
	
	long lastEvent;
	
	/**
	 * Method declaration
	 *
	 *
	 * @param event
	 *
	 * @see
	 */
	public void serialEvent(SerialPortEvent event) {
		switch (event.getEventType()) {

		case SerialPortEvent.BI:

		case SerialPortEvent.OE:

		case SerialPortEvent.FE:

		case SerialPortEvent.PE:

		case SerialPortEvent.CD:

		case SerialPortEvent.CTS:

		case SerialPortEvent.DSR:

		case SerialPortEvent.RI:

		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
			break;

		case SerialPortEvent.DATA_AVAILABLE:
			byte[] readBuffer = new byte[400];

			long now = System.currentTimeMillis();
			
			if ( (now-lastEvent) > 2 * 1000) {
				frame = new Frame();
				System.out.println("-- New frame - more than two seconds...");
			}
			
			try {
				int numBytes = 0;
				while (inputStream.available() > 0) {
					 int delta = inputStream.read(readBuffer);
					 numBytes +=delta;
				} 

				 //System.out.println("Read: " + numBytes);
				for (int i = 0; i < numBytes; i++) {
					byte c = readBuffer[i];
					int c2 = (int) (c&0xFF);
					int c3 = c2 ^ 223;
					
					if (c3 == 255 && prev == 255) {
						if (frame!=null && frame.isReady()) {
							//System.out.println("Previous was: " + frame.getBadgeNo());
							this.badgeListener.setBadgeNumber(frame.getBadgeNo());
							prevFrame = frame;
						}
						frame = new Frame();
						frames.add(frame);
						frame.add(255);
					}
					if (frame.isReady() && frame.curByte==10) {
//						this.badgeListener.setBadgeNumber(frame.getBadgeNo());
					}
					frame.add(c3);
					//System.out.println("#"+i+" :" + c3 + " ( " +( totNr )  + ") " + prev);
					prev = c3;
					totNr++;
				} 
				
				if (frame.isComplete()) {
					this.badgeListener.setFrame(frame);
					System.out.println(frame.getBadgeNo() + " - " + frame);
				}
				
				//System.out.print("Read: " + new String(readBuffer));
			} catch (IOException e) {}

			lastEvent = now;
			
			break;
		}
	}

	public void setCallback(BadgeListener af) {
		this.badgeListener = af;
	} 

}



