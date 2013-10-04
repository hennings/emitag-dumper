package net.spjelkavik.emit.emitag;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.text.SimpleDateFormat;

public class EmitagFrame {

    private StringBuilder bytes = new StringBuilder();
	int curByte = 1;

    void initBytes(String b) {
        bytes = new StringBuilder();
        bytes.append(b);
    }

	public void add(int i) {
        bytes.append((char) i);
	}

    public String getFrame() {
        return bytes.toString();
    }

	public boolean isReady() {
        int len = bytes.length();
        if (len>1) { return true; }
		return false;
	}

	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.000");
	

	public String toString() {
        return new ToStringBuilder(this).append("event", bytes).toString();
	}


}
