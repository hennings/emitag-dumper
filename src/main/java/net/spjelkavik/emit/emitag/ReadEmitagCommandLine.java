package net.spjelkavik.emit.emitag;

import javax.comm.*;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;

import com.google.common.base.Joiner;
import org.apache.log4j.Logger;

public class ReadEmitagCommandLine {

    static CommPortIdentifier portId;
    static Enumeration portList;
    InputStream inputStream;
    SerialPort serialPort;
    Thread		      readThread;

    final static private Logger log = Logger.getLogger(ReadEmitagCommandLine.class);


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

        List<String> ports = EmitagReader.findSerialPorts();

        System.out.println("Serial ports available:\n\t" + Joiner.on("\n\t").join(ports));
        String defaultPort = "COM1";
        if (ports.size()>0) defaultPort=ports.get(0);

        if (args.length > 0) {
            defaultPort = args[0];
        }


        EmitagReader.findPort(defaultPort);
        EmitagReader re = new EmitagReader(new EmitagMessageListener() {
            public void handleECBMessage(ECBMessage m) {
                log.info("Message: " + m);
            }
        });


    }
    //static String defaultPort = "COM29";

}
