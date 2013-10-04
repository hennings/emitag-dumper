package net.spjelkavik.emit.emitag;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: hennings
 * Date: 14.07.13
 * Time: 19:49
 * To change this template use File | Settings | File Templates.
 */
public class ECBMessage {
    private String info;
    private int from ;
    private int to;
    private String clock;
    private int controlCode;
    private String operationMode;
    private String serial;
    private String unitHealth;
    private int emitagNumber;
    private int messageNr;
    private String timeOfEvent;
    private String timeSinceZero;
    private String numberOfPackagesRemaining;
    private MessageType type;
    private int gate;
    private int status;
    private String emitagDumpValues;
    private String emitagInternalInfo;
    private int serialNumber;
    private String version;
    private String codeL;
    private List<EmitagControl> controls = new ArrayList<EmitagControl>();

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setMessages(String messages) {
        String[] values = info.split("-");
        if (values.length==2) {
            from = NumberUtils.toInt(values[0], 0);
            to = NumberUtils.toInt(values[1],0 ) ;
        } else if (values.length == 1) {
            messageNr = NumberUtils.toInt(values[0]);
        }

    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public void setClock(String clock) {
        this.clock = clock;
    }

    public String getClock() {
        return clock;
    }

    public void setControlCode(String controlCode) {
        this.controlCode = NumberUtils.toInt(controlCode);
    }

    public int getControlCode() {
        return controlCode;
    }

    public void setOperationMode(String operationMode) {
        this.operationMode = operationMode;
    }

    public String getOperationMode() {
        return operationMode;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getSerial() {
        return serial;
    }

    public void setUnitHealth(String unitHealth) {
        this.unitHealth = unitHealth;
    }

    public String getUnitHealth() {
        return unitHealth;
    }

    public void setEmitagNumber(String emitagNumber) {
        this.emitagNumber = NumberUtils.toInt(emitagNumber);
    }

    public int getEmitagNumber() {
        return emitagNumber;
    }

    public int getMessageNr() {
        return messageNr;
    }

    public void setTimeOfEvent(String timeOfEvent) {
        this.timeOfEvent = timeOfEvent;
    }

    public String getTimeOfEvent() {
        return timeOfEvent;
    }

    public void setTimeSinceZero(String timeSinceZero) {
        this.timeSinceZero = timeSinceZero;
    }

    public String getTimeSinceZero() {
        return timeSinceZero;
    }

    public void setNumberOfPackagesRemaining(String numberOfPackagesRemaining) {
        this.numberOfPackagesRemaining = numberOfPackagesRemaining;
    }

    public String getNumberOfPackagesRemaining() {
        return numberOfPackagesRemaining;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public void setGateStatusChange(String gateStatusChange) {
        String[] values = info.split("-");
        if (values.length==2) {
            gate = NumberUtils.toInt(values[0], -1);
            status = NumberUtils.toInt(values[1],-1 ) ;

    }

}

    public void setEmitagDumpValues(String emitagDumpValues) {
        this.emitagDumpValues = emitagDumpValues;
    }

    public String getEmitagDumpValues() {
        return emitagDumpValues;
    }

    public void setEmitagInternalInfo(String emitagInternalInfo) {
        this.emitagInternalInfo = emitagInternalInfo;
    }

    public String getEmitagInternalInfo() {
        return emitagInternalInfo;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = NumberUtils.toInt( serialNumber , -1);
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setCodeL(String codeL) {
        this.codeL = codeL;
    }

    public String getCodeL() {
        return codeL;
    }

    public void addControl(EmitagControl emitagControl) {
        controls.add(emitagControl);
    }

    public int getGate() {
        return gate;
    }

    public int getStatus() {
        return status;
    }

    public List<EmitagControl> getControls() {
        return controls;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("type", type).append("tagNumber", emitagNumber)
                .append("timestamp", timeOfEvent).toString();
    }
}
