package net.spjelkavik.emit.emitag;

import org.apache.log4j.Logger;

public class EmitagConfig {
    private final String db;
    private final String system;
    private final String ecardField;
    private final String comPort;
    private final String jdbcUrl;

    private final Logger LOG = Logger.getLogger(EmitagConfig.class);
    private String dbDir;

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

    public EcardField getEcardField() {
        if ("ecard1".equals(ecardField)) {
            return EcardField.ECARD1 ;
        } else if ("ecard2".equals(ecardField)) {
            return EcardField.ECARD2;
        }
        LOG.error("Not a valid ecardfield: " + ecardField);
        return EcardField.ECARD1;
    }

    public String getComPort() {
        return comPort;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    @Override
    public String toString() {
        return "EmitagConfig{" +
                "db='" + db + '\'' +
                ", system='" + system + '\'' +
                ", ecardField='" + ecardField + '\'' +
                ", comPort='" + comPort + '\'' +
                ", jdbcUrl='" + jdbcUrl + '\'' +
                '}';
    }

    public String getDbDir() {
        if (db.contains("etime.mdb")) {
            return db.replace("etime.mdb","");
        } else {
            return "c:/temp";
        }
    }
}
