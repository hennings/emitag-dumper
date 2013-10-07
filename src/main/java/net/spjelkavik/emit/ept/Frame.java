package net.spjelkavik.emit.ept;

import java.text.SimpleDateFormat;

import org.apache.commons.lang.builder.ToStringBuilder;

public class Frame {

	private int[] frameBytes = new int[220];
	int curByte = 1;
	
	public void add(int i) {
		// System.out.println("Curbyte: " + curByte);
		if (curByte < 217) {
			// System.out.println("Byte # " + curByte + " = " + i);
			frameBytes[curByte++]=i;
		}
	}

	public boolean isReady() {
		if (curByte>9)
			return true;
		return false;
	}
	public boolean isComplete() {
		if (curByte==217)
			return true;
		return false;
	}

	public int getBadgeNo() {
		if (curByte>9) {
			return frameBytes[5] * 256*256 + frameBytes[4] * 256 + frameBytes[3];
		}
		return -1;
	}
	
	public int getProductionWeek() {
		return frameBytes[7];
	}

	public int getProductionYear() {
		return frameBytes[8];
	}
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.000");
	
	public String getLogLine() {
		StringBuilder sb = new StringBuilder();
		sb.append("\"X\",\"0\",\"0\"");
		sb.append(String.format(",\"%06d\"", this.getBadgeNo() ));
		sb.append(",\""+sdf.format(new java.util.Date())+"\"");
		sb.append(",\""+sdf.format(new java.util.Date())+"\"");
		sb.append("," + this.getBadgeNo());
		sb.append("," + String.format("%04d",this.getProductionWeek()));
		sb.append("," + String.format("%04d",this.getProductionYear()));
		sb.append("," + this.getSplits());
		sb.append(",0000000\n");
		return sb.toString();
	}

	public String getLogLine(int startNo) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"L\",\"0\",\"0\"");
		sb.append(String.format(",\"%06d\"", startNo ));
		sb.append(",\""+sdf.format(new java.util.Date())+"\"");
		sb.append(",\""+sdf.format(new java.util.Date())+"\"");
		sb.append("," + startNo);
		sb.append("," + String.format("%04d",this.getProductionWeek()));
		sb.append("," + String.format("%04d",this.getProductionYear()));
		sb.append("," + this.getSplits());
		sb.append(",0000000\n");
		return sb.toString();
	}

	public String getSplits() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i<50;i++) {
			int code = frameBytes[11+i*3];
			int time = frameBytes[11+i*3+1] + frameBytes[11+i*3+2]*256;
			if (sb.length()>0) sb.append(",");
			sb.append(String.format("%03d,%05d", new Object[] { code, time}));
		}
		return sb.toString();
	}
	
	public String toString() {
		if (this.isComplete()) 
			return new ToStringBuilder(this).append("week", getProductionWeek())
			.append("year", getProductionYear())
			.append("badge", getBadgeNo())
			.append("splits", getSplits())
			.toString();
		return new ToStringBuilder(this).append("" + curByte).toString();
	}

	public String getRunningTime() {
		int prevTime = -1;
		int runtime = -1;
		for (int i = 0; i<50 && runtime<1;i++) {
			int code = frameBytes[11+i*3];
			int time = frameBytes[11+i*3+1] + frameBytes[11+i*3+2]*256;
			if (code==250) runtime = prevTime;
			prevTime = time;
		}
		return String.format("%02d:%02d:%02d",
				runtime/(60*60),
				(runtime/60)%60,
				runtime%60);
	}

}
