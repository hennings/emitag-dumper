package net.spjelkavik.emit.emitag;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.text.SimpleDateFormat;

public class EmitagFrame {

    private StringBuilder bytes = new StringBuilder();
	int curByte = 1;
	
	public void add(char i) {
        bytes.append(i);
	}

	public boolean isReady() {
        int len = bytes.length();
        if (len>1 && bytes.charAt(len-1) == 10 && bytes.charAt(len-2) == 13) { return true;}
		return false;
	}

	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.000");
	

	public String toString() {
        return new ToStringBuilder(this).append("event", bytes).toString();
	}


}
