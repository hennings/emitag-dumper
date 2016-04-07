package net.spjelkavik.emit.emitag;


import java.util.concurrent.ExecutionException;

import org.apache.log4j.Logger;
import org.jperipheral.PeripheralConfigurationException;
import org.jperipheral.PeripheralInUseException;
import org.jperipheral.PeripheralNotFoundException;

public class ReadEmitagCommandLine {

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
    public static void main(String[] args) throws PeripheralConfigurationException, PeripheralNotFoundException, PeripheralInUseException, ExecutionException, InterruptedException {
        boolean		      portFound = false;

//        List<String> ports = EmitagReader.findSerialPorts();

        String defaultPort = "COM1";

        if (args.length > 0) {
            defaultPort = args[0];
        }


        //EmitagReader.findPort(defaultPort);
        EmitagReader re = new EmitagReader(defaultPort, new EmitagMessageListener() {
            public void handleECBMessage(ECBMessage m) {
                log.info("Message: " + m);
            }
        });


        log.info("Finished?");

    }
    //static String defaultPort = "COM29";

}
