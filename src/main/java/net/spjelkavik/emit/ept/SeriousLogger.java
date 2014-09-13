package net.spjelkavik.emit.ept;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: hennings
 * Date: 13.09.2014
 * Time: 18:20
 */
public final class SeriousLogger {

    final private Logger log = Logger.getLogger(SeriousLogger.class);

    final File logfile;
    final File logfile2;
    public SeriousLogger(File logfile, File logfile2) {
        this.logfile = logfile;
        this.logfile2 = logfile2;
        log.debug(logfile.getAbsolutePath());
        log.debug(logfile2.getAbsolutePath());
    }

    public void logMessageToDisk(String logMessage) {
        FileWriter logfw;
        try {
            logfw = new FileWriter(logfile, true);
            IOUtils.write(logMessage + "\r\n", logfw);
            logfw.close();
        } catch (IOException e) {
            log.error("Could not write log: ", e);
        }

        try {
            logfw = new FileWriter(logfile2, true);
            IOUtils.write(logMessage + "\r\n", logfw);
            logfw.close();
        } catch (IOException e) {
            log.error("Could not write log: ", e);
        }
    }

}
