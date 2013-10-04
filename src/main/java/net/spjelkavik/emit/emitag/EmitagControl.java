package net.spjelkavik.emit.emitag;

import org.apache.commons.lang.math.NumberUtils;

public class EmitagControl {
    private final int nr;
    private final int code;
    private final long millistamp;
    private final String timestamp;
    private final String timeofday;
    private final int last;

    public EmitagControl(String info) {
        String[] cline = info.split("-");
        if (cline.length>4) {
            this.nr = NumberUtils.toInt(cline[0]);
            this.code = NumberUtils.toInt(cline[1]);
            this.millistamp = NumberUtils.toLong(cline[2]);
            this.timestamp = cline[3];
            this.timeofday = cline[4];
            this.last = NumberUtils.toInt(cline[5]) ;
        } else {
            throw new IllegalStateException("Illegal Emitag dump: " + info);
        }

    }
    /*
    Millistamp - code from emit
                        if (num < 5475L)
                    {
                      if (num > 2192L)
                        this.labelProdUke.BackColor = EmitVersion.red;
                      else if (num > 1827L)
                        this.labelProdUke.BackColor = EmitVersion.yellow;
                      else
                        this.labelProdUke.BackColor = Color.Empty;
                      this.labelProdUke.Text = string.Format("{0:y}", (object) DateTime.Now.AddDays((double) (num * -1L)));
                      continue;
                    }


     */

    public int getNr() {
        return nr;
    }

    public int getCode() {
        return code;
    }

    public long getMillistamp() {
        return millistamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTimeofday() {
        return timeofday;
    }

    public int getLast() {
        return last;
    }
}
