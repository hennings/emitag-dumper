package net.spjelkavik.emit.emitag;

import com.google.common.io.Files;
import net.spjelkavik.emit.BadgeListener;

import javax.comm.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TooManyListenersException;

public class ReadEmitagPrototype implements Runnable, SerialPortEventListener {

    static CommPortIdentifier portId;
    static Enumeration portList;
    InputStream inputStream;
    SerialPort serialPort;
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
        ReadEmitagPrototype.findPort(defaultPort);
        ReadEmitagPrototype re = new ReadEmitagPrototype();



    }
    static String defaultPort = "COM29";

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
    public ReadEmitagPrototype() {
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
            serialPort.setSerialPortParams(115200, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
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

    List<EmitagFrame> frames = new ArrayList<EmitagFrame>();
    EmitagFrame frame = new EmitagFrame();
    EmitagFrame prevFrame = null;
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

                if ( (now-lastEvent) > 1000) {
                    frame = new EmitagFrame();
                    System.out.println("-- New frame - more than two seconds...");
                }

                try {
                    int numBytes = 0;
                    while (inputStream.available() > 0) {
                        int delta = inputStream.read(readBuffer);
                        numBytes +=delta;
                    }

                    System.out.println("Read: " + numBytes);
                    for (int i = 0; i < numBytes; i++) {
                        byte c = readBuffer[i];
                        int c2 = (int) (c&0xFF);

                        System.out.println(String.format(" * %3d (p: %3d)  - %c ", c2,prev,  (char) c>13?c:'*'));

                        if (c2 == 2) {
                            System.out.println("STX received - new frame");
                            frame = new EmitagFrame();

                        }  else if (c2 == 3) {
                            System.out.println("ETC received - new frame");
                            prevFrame = frame;
                            System.out.println("Complete frame: " + frame.toString());
                            Files.append(frame.toString()+"\r\n", new File("c:/tmp/log-emitag.log"), Charset.forName("UTF-8"));
                            frame = new EmitagFrame();
                            frames.add(frame);
                        } else {
                            frame.add((char)c2);
                        }
                        //System.out.println("#"+i+" :" + c3 + " ( " +( totNr )  + ") " + prev);
                        prev = c2;
                        totNr++;
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



