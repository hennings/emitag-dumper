package net.spjelkavik.emit;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: hennings
 * Date: 23.sep.2010
 */
public class SplitTime {
    final private Date date;
    final private int badge;
    final private int id;
    final private int station;

    static final AtomicInteger globalId = new AtomicInteger(0);

    public SplitTime(int badge, Date date, int station) {
        this.badge = badge;
        this.date = date;
        this.station = station;
        this.id = globalId.incrementAndGet();
    }

    public int getBadge() {
        return badge;
    }

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    volatile boolean ok = false;
    volatile int retries = 0;

    public boolean isOk() {
        return ok;
    }

    public void ok() {
        this.ok = true;
    }

    public int getStation() {
        return station;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this).toString();
    }

    public String toLog() {
        return "C:" + getStation()+",N:" +getBadge()+",D="+ getDate()+";\n";
    }

    public int getRetries() {
        return retries;
    }

    public void retry() {
        retries++;
    }
}
