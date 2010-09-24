package net.spjelkavik.emit;

import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: hennings
 * Date: 23.sep.2010
 */
public class ConsoleApp {
    static final Logger log = Logger.getLogger(ConsoleApp.class);
    
    public static void main(String[] args) {

        String com = "COM1";
        if (args.length>0) {
            com=args[0];
            System.out.println("Using port " + com);
        }

        Reader167.findPort(com);
        Reader167 reader = new Reader167();

        PublishChanges pc = new PublishChanges();
        Publisher p = new Publisher(pc);

        AsciiBadgeListener cb = new MyBadgeListener(pc, new File("/tmp/asciiSerialOnline.log"));
        reader.setCallback(cb);

        Thread t = new Thread(p);
        t.setDaemon(true);
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            log.warn("Interrupted during wait: " , e);
        }


    }

}
