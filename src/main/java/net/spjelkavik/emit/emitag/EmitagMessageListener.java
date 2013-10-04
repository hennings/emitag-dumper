package net.spjelkavik.emit.emitag;

/**
 * Created with IntelliJ IDEA.
 * User: hennings
 * Date: 15.07.13
 * Time: 12:12
 * To change this template use File | Settings | File Templates.
 */
public interface EmitagMessageListener {
    void handleECBMessage(ECBMessage m);
}
