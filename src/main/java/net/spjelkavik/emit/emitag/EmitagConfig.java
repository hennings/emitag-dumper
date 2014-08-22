package net.spjelkavik.emit.emitag;

public class EmitagConfig {
    private final String db;
    private final String system;
    private final String ecardField;
    private final String comPort;
    private final String jdbcUrl;

    public EmitagConfig(String db, String system, String comPort, String ecardField, String jdbcUrl) {
        this.db = db;
        this.system = system;
        this.comPort = comPort;
        this.ecardField = ecardField;
        this.jdbcUrl = jdbcUrl;
    }

    public String getDb() {
        return db;
    }

    public String getSystem() {
        return system;
    }

    public String getEcardField() {
        return ecardField;
    }

    public String getComPort() {
        return comPort;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }
}
