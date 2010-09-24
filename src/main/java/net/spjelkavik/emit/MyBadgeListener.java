package net.spjelkavik.emit;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
* User: hennings
* Date: 23.sep.2010
*/
class MyBadgeListener implements AsciiBadgeListener {
    final private PublishChanges pc;
    final private File logFile;

    static final Logger log = Logger.getLogger(MyBadgeListener.class);

    public MyBadgeListener(PublishChanges pc, File logFile) {
        this.pc = pc;
        this.logFile = logFile;
    }

    @Override
    public void setBadgeAndStationNumber(int badge, int station) {
        log.debug("Preview: " + badge+", " + station);
    }

    @Override
    public void setFrame(AsciiFrame frame) {
        SplitTime st = new SplitTime(frame.getBadgeNo(), new Date(), frame.getStation());
        log.debug("Logged new: " + st);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logFile, true));
            bw.write(st.toLog());
            bw.close();
        } catch (IOException e) {
            log.warn("Cannot write log: ", e);
        }
        pc.add(st);
    }
}
