package net.spjelkavik.emit.emitag;

import org.apache.commons.lang.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class EmitagMessageParser {

    public ECBMessage parse(final EmitagFrame frame) {
        String f = frame.getFrame();
        String[] messages = f.split("\t");

        ECBMessage ecbm = new ECBMessage();

        for (String m : messages) {
            if (m.length()>1) {
                parseMessage(ecbm, m);

            }
        }
        return ecbm;
    }

    void parseMessage(ECBMessage ecbm, String m) {
        char type = m.charAt(0);
        String info = m.substring(1);
        switch (type) {
            case 'I': {           // unit info (HW/SW)
                ecbm.setInfo(info);
                break;
            }
            case 'M': {           // number of messages today
                ecbm.setMessages(info);
                break;
            }
            case 'W': {           // Clock - when the message was sent
                ecbm.setClock(info);
                break;
            }
            case 'C': { // Postcode
                ecbm.setControlCode(info);
                break;
            }
            case 'X': { // Operation mode
                ecbm.setOperationMode(info);
                break;
            }
            case 'Y': { // Serial
                ecbm.setSerial(info);
                break;
            }
            case 'A': { // unit health
                ecbm.setUnitHealth(info);
                break;
            }
            case 'H': {
             // more status ( abcde - a = 0/1 (normal/turning off), b = loop 1 status, 0=ok, 1 = missing, 2 = problem
             //     c = loop 2 status, d = radio status (1= ok, 0 = missing), e = gprs signal quality (0-4, 0 = missing)

                break;
            }
            case 'N': {
                ecbm.setEmitagNumber(info);
                ecbm.setType(MessageType.EMITAG);
                break;
            }

            case 'E': { // time of event
                ecbm.setTimeOfEvent(info);
                break;
            }

            case 'F': { // time of event
                ecbm.setType(MessageType.STARTFINISH);
                ecbm.setGateStatusChange(info);
                break;
            }

            case 'O': { // time of event
                ecbm.setNumberOfPackagesRemaining(info);
                break;
            }

            case 'T': { // elapsed time since zeroing
                ecbm.setTimeSinceZero(info);
                break;
            }
            case 'D': { // Parse emitag dump
                ecbm.setEmitagDumpValues(info);
                break;

            }
            case 'V' : {
                ecbm.setEmitagInternalInfo(info);
                break;
            }
            case 'S': {
                ecbm.setSerialNumber(info);
                break;
            }
            case 'R': {
                ecbm.setVersion(info);
                break;
            }
            case 'L': {
                ecbm.setCodeL(info);
                break;
            }
            case 'Q': {
                ecbm.setType(MessageType.EMITAGDUMP);
                ecbm.addControl(new EmitagControl(info));
                break;
            }

        }
    }


}
