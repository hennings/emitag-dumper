package net.spjelkavik.emit;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.builder.ToStringBuilder;

public class AsciiFrame {

    private int[] frameBytes = new int[220];
    int curByte = 0;

    public void add(int i) {
        // System.out.println("Curbyte: " + curByte);
        if (curByte < 16) {
            //System.out.println("Byte # " + curByte + " = " + i);
            frameBytes[curByte++]=i;
        }
    }

    public boolean isReady() {
        if (curByte>9)
            return true;
        return false;
    }
    public boolean isComplete() {
        if (curByte==16)
            return true;
        return false;
    }

    public int getBadgeNo() {
        if (curByte>6) {
            StringBuilder sb = new StringBuilder();
            for (int i = 2; i<8; i++ ) {
                sb.append((char)frameBytes[i]);
            }
            return NumberUtils.toInt(sb.toString());
        }
        return -1;
    }

    public int getStation() {
        if (curByte>12) {
            StringBuilder sb = new StringBuilder();
            for (int i = 10; i<=12; i++ ) {
                sb.append((char)frameBytes[i]);
            }
            return NumberUtils.toInt(sb.toString());
        }
        return -1;
    }


    public String toString() {
        if (this.isComplete())
            return new ToStringBuilder(this).append("badge", getBadgeNo())
                    .append("station", getStation()).toString();
        return new ToStringBuilder(this).append("" + curByte).toString();
    }

    public void setBytes(int[] bytes) {
        frameBytes = bytes;
        curByte = bytes.length;
    }
}
