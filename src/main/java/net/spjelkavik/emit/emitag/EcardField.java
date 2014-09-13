package net.spjelkavik.emit.emitag;

/**
 * User: hennings
 * Date: 29.08.2014
 * Time: 07:24
 */
public enum EcardField {
    ECARD1("ecard1"), ECARD2("ecard2");
    private final String name;

    EcardField(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
