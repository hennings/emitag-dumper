package net.spjelkavik.emit.emitag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.google.common.io.Files;
import org.apache.log4j.Logger;
import org.jperipheral.PeripheralChannelGroup;
import org.jperipheral.PeripheralConfigurationException;
import org.jperipheral.PeripheralInUseException;
import org.jperipheral.PeripheralNotFoundException;
import org.jperipheral.SerialChannel;
import org.jperipheral.SerialPort;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

public final class EmitagReader {

    final static private Logger log = Logger.getLogger(EmitagReader.class);

    InputStream inputStream;
    final SerialPort serialPort;
    Thread		      readThread;

    ByteBuffer target;

    /**
     * Constructor declaration
     *
     *
     * @see
     * @param af
     */
    public EmitagReader(final String port, final EmitagMessageListener af) throws PeripheralInUseException, PeripheralNotFoundException, PeripheralConfigurationException, ExecutionException, InterruptedException {
        this.badgeListener = af;
        serialPort = new SerialPort(port);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        SerialChannel channel = serialPort.newAsynchronousChannel(new PeripheralChannelGroup(pool), 100, MILLISECONDS);
        channel.configure(SerialPort.BaudRate._115200, SerialPort.DataBits.EIGHT,  SerialPort.Parity.NONE, SerialPort.StopBits.ONE, SerialPort.FlowControl.NONE);


        while (true) {
            serialEvent(channel);
        }
//        pool.awaitTermination(1, SECONDS);
//        log.info("returning");
    }

    /**
     * Method declaration
     *
     *
     * @see
     */
    public void run() {
        System.out.println("run");
        try {
            Thread.sleep(1000);
            log.info(target.hasRemaining());
        } catch (InterruptedException e) {}
        System.out.println("run..exit");
    }

    int totNr = 0;

    List<EmitagFrame> frames = new ArrayList<EmitagFrame>();
    EmitagFrame frame = new EmitagFrame();
    EmitagFrame prevFrame = null;
    private EmitagMessageListener badgeListener;

    int prev = -1;

    long lastEvent;

    public void serialEvent(SerialChannel channel) {
        long now = System.currentTimeMillis();
        try {

            target = ByteBuffer.allocate(10000);

            Future<Integer> res = channel.read(target);
            int numBytes = res.get(60, TimeUnit.SECONDS);
            if ((now - lastEvent) > 1000) {
                frame = new EmitagFrame();
              //  System.out.println("-- Clear frame - more than one second since last read...");
            }



/*                    int numBytes = 0;

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    while (byteBuffer.hasRemaining()) {
                        int size = byteBuffer.remaining();
                        byte[] tmpBuffer = new byte[size];
                        byteBuffer.get(tmpBuffer);
                        bos.write(tmpBuffer);
                        numBytes +=size;
                    }
                    byte[] readBuffer = bos.toByteArray();
                                */
            //log.debug("Read: " + numBytes);
            for (int i = 0; i < numBytes; i++) {
                byte c = target.get(i);
                int c2 = (int) (c & 0xFF);

                if (c2 != 0 || prev != 0)
                    //log.debug(String.format(" * %3d (p: %3d)  - %c ", c2, prev, (char) c > 13 ? c : '*'));

                    if (c2 == 10 && prev == 13 && frame.isReady()) {
                        frame = submitFrame(frame);
                    }
                if (c2 == 2) {
                    //log.debug("STX received - new frame");
                    frame = new EmitagFrame();
                } else if (c2 == 3) {
                    //log.debug("ETS received - new frame");
                    frame = submitFrame(frame);
                } else {
                    if (c2 != 10 && c2 != 13) {
                        frame.add(c2);
                    }
                }
                //System.out.println("#"+i+" :" + c3 + " ( " +( totNr )  + ") " + prev);
                prev = c2;
                totNr++;
            }


            //System.out.print("Read: " + new String(readBuffer));

        } catch (TimeoutException timeout) {
            log.info("no data the last 60 seconds");
        } catch (Exception e) {
            log.warn("something bad", e);
        }


        lastEvent = now;


    }

    EmitagMessageParser parser = new EmitagMessageParser();

    private EmitagFrame submitFrame(EmitagFrame frame) {
        prevFrame = frame;
        log.info("Frame: " + frame.getFrame());
        try {
            ECBMessage m = parser.parse(frame);
            badgeListener.handleECBMessage(m);
        } catch (Exception e) {
            log.info("problems ",e);
        }
        try {
            Files.append(frame.toString()+"\r\n", new File("log-emitag.log"), Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        frames.add(frame);

        frame = new EmitagFrame();
        return frame;
    }


}



