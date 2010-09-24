package net.spjelkavik.emit;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.HeadMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * User: hennings
 * Date: 23.sep.2010
 */
public class Publisher implements Runnable {
    private PublishChanges pc;

    static Logger log = Logger.getLogger(Publisher.class);

    public Publisher(PublishChanges pc) {
        this.pc = pc;
    }

    String baseUrl = "http://timing.spjelkavik.net/online/receive.php";
    HttpClient client = new HttpClient();

    static volatile boolean shouldFinish = false;

    public void finish() {
        shouldFinish = true;
    }

    @Override
    public void run() {

        DateFormat sdf = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM);

        while (!shouldFinish) {

            SplitTime t = null;
            int statusCode = 0;
            try {
                t = pc.getQueue().take();
                HeadMethod method = new HeadMethod(baseUrl+"?ecardno="+t.getBadge()+"&station="+t.getStation()+"timems="+t.getDate().getTime()+"&timestr="+sdf.format(t.getDate()));

                method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                        new DefaultHttpMethodRetryHandler(3, false));
                statusCode = client.executeMethod(method);

                if (statusCode != HttpStatus.SC_OK) {
                    log.warn("Method failed: " + method.getStatusLine() + " for " + method.getURI());
                } else {
                    t.ok();
                }
                
            } catch (Exception e) {
                log.warn("Error in publish: " , e);
            } finally {
                if (statusCode != HttpStatus.SC_OK && t!=null && !t.isOk()) {
                    log.info("Reentering into queue : " + t);
                    t.retry();
                    if (t.getRetries()< 2 * 60 ) {
                        pc.add(t);
                    }
                    log.debug("Sleep 1000 ms before retry");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                }
            }



        }
    }

}