package net.spjelkavik.emit;


import org.apache.log4j.Logger;

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
public class Reader167 implements Runnable, SerialPortEventListener {

    static final Logger log = Logger.getLogger(Reader167.class);

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
        Reader167.findPort(defaultPort);
        Reader167 t = new Reader167();
        t.setCallback(new AsciiBadgeListener() {
            @Override
            public void setBadgeAndStationNumber(int badge, int station) {
                System.out.println("Callback! : " + badge+", " + station);
            }

            @Override
            public void setFrame(AsciiFrame frame) {
                System.out.println("Callback with frame");
            }
        });

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
    public Reader167() {
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
        System.out.println("run");
    }

    int totNr = 0;

    AsciiFrame frame = new AsciiFrame();
    AsciiFrame prevFrame = null;
    private AsciiBadgeListener asciiBadgeListener;

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
        //System.out.println("event! " + event);
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

                if ( (now-lastEvent) > 500) {
                    frame = new AsciiFrame();
                    log.debug("-- New frame - more than 500 ms...");
                }

                try {
                    int numBytes = 0;
                    while (inputStream.available() > 0) {
                        int delta = inputStream.read(readBuffer);
                        numBytes +=delta;
                    }

                    log.debug("Read: " + numBytes);
                    for (int i = 0; i < numBytes; i++) {
                        byte c = readBuffer[i];
                        int c2 = (int) (c&0xFF);
                        int c3 = c2 ; //^ 223;

                        if (log.isDebugEnabled()) {
                            String s = ("The Byte # " + c3 + "  (pos = " + i);
                            if (c3>32 && c3<127) {
                                s= s + (", char = " + ((char)c3));
                            }
                            s = s+(")");
                            log.debug(s);
                        }

                        if (c3 == 10 && prev == 13) {
                            if (frame!=null && frame.isReady()) {
                                //System.out.println("Previous was: " + frame.getBadgeNo());
                                this.asciiBadgeListener.setBadgeAndStationNumber(frame.getBadgeNo(), frame.getStation());
                                if (frame.isComplete()) {
                                    this.asciiBadgeListener.setFrame(frame);
                                    log.debug(frame.getBadgeNo() + " - " + frame);
                                }
                                
                                prevFrame = frame;
                            }
                            frame = new AsciiFrame();
                        } else {
                            frame.add(c3);
                        }
                        //System.out.println("#"+i+" :" + c3 + " ( " +( totNr )  + ") " + prev);
                        prev = c3;
                        totNr++;
                    }

                    if (frame.isComplete()) {
                        this.asciiBadgeListener.setFrame(frame);
                        log.debug(frame.getBadgeNo() + " - " + frame);
                    }

                    //System.out.print("Read: " + new String(readBuffer));
                } catch (IOException e) {}

                lastEvent = now;

                break;
        }
    }

    public void setCallback(AsciiBadgeListener af) {
        this.asciiBadgeListener = af;
    }

}



