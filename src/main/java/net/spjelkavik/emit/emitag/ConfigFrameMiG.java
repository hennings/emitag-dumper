package net.spjelkavik.emit.emitag;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * User: hennings
 * Date: 22.08.2014
 * Time: 18:24
 */
public class ConfigFrameMiG extends JFrame {

    public final static String JDBC_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";

    Logger log = Logger.getLogger(ConfigFrameMiG.class);

    JTextField dbFileTxt = new JTextField();
    JTextField sysFileTxt = new JTextField();
    JButton butOk = new JButton("Start");

    private JButton butExit = new JButton("Avslutt");

    private JComboBox listOfComPorts;
    private final JLabel configStatus = new JLabel("Not tested");
    private String jdbcUrl;
    private EmitagConfig emitagConfig;

    String ecardField;

    public void init(List<String> serialPorts, final ActionListener callback) {

        final JFrame thisFrame = this;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel all = new JPanel(new MigLayout());

        all.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.add(all);
        final RegistryPreferences prefs = new RegistryPreferences();

        String dbFileName = prefs.getDbFile();
        String sysFileName = prefs.getSysFile();
        String comPort = prefs.getComPort();
        String mode64 = prefs.getMode64();
        ecardField = prefs.getEcardField();

        dbFileTxt.setText(dbFileName);
        sysFileTxt.setText(sysFileName);

        JLabel titleLabel = new JLabel("Anonyme Emitags");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));

        all.add(titleLabel, "span,wrap");

        addFileChooser(all, "eTime.mdb file", dbFileTxt, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileTypeFilter ftf = new FileTypeFilter("mdb", "etime.mdb Files", "Select", thisFrame);
                File dbFile = ftf.pickFile(dbFileTxt.getText());
                if (dbFile != null) {
                    dbFileTxt.setText(dbFile.getAbsolutePath());
                }
            }
        });

        addFileChooser(all, "system.mdw file", sysFileTxt, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileTypeFilter ftf = new FileTypeFilter("system.mdw", "system.mdw file", "Select", thisFrame);
                File sysFile = ftf.pickFile(sysFileTxt.getText());
                if (sysFile != null) {
                    sysFileTxt.setText(sysFile.getAbsolutePath());
                }
            }
        });

        String winMode = "Windows 32/64 bits? (" + System.getProperty("os.arch") + ")";
        String[] buttonsMode = new String[] {"32","64"};
        addButtonLine(all, winMode, buttonsMode, mode64, prefs, new DoIt(){
            @Override
            public void doit(String value) {
                prefs.setMode64(value);
            }
        });


        String[] comPorts = serialPorts.toArray(new String[0]);
        listOfComPorts = new JComboBox(comPorts);
        for (int i = 0; i < listOfComPorts.getItemCount(); i++) {
            if (listOfComPorts.getItemAt(i).equals(comPort)) {
                listOfComPorts.setSelectedIndex(i);
            }
        }
        all.add(new JLabel("COM-port"));
        all.add(listOfComPorts, "wrap");


        String ecardLabel = "ecard1 or ecard2?";
        String[] ecardAlternatives = new String[]{"ecard1", "ecard2"};
        addButtonLine(all, ecardLabel, ecardAlternatives, ecardField, prefs, new DoIt() {
            @Override
            public void doit(String value) {
                prefs.setEcardField(value);
                ecardField = value;
            }
        });


        final JTextField furl = new JTextField();
        all.add(furl, "span,width 100%");

        butOk.setEnabled(false);
        butOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateConfig(prefs);
                calculateJdbcUrl(prefs, furl);
                //verifyDataSource(prefs, furl);

                callback.actionPerformed(null);
                setVisible(false);
            }
        });


        JButton butTest = new JButton("Test config");
        all.add(butTest);
        all.add(new JLabel(""), "wrap");
        butTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateConfig(prefs);
                verifyDataSource(prefs, furl);

            }
        });

        configStatus.setForeground(Color.RED);
        all.add(configStatus, "span");
        all.add(new JLabel(""));
        all.add(new JLabel(""),"wrap");

        all.add(butTest);


        all.add(butOk);
        all.add(butExit, "wrap");

        butExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(-1);
            }
        });

        pack();
        setLocationRelativeTo(null);  // Center window.
        setVisible(true);


    }



    void addButtonLine(JPanel all, String lineText, String[] buttons, String currentValue,
                       final RegistryPreferences prefs, final DoIt l) {
        JLabel l0 = new JLabel(lineText);
        all.add(l0);
        java.util.List<JRadioButton> rb = new ArrayList<JRadioButton>();
        ButtonGroup rg = new ButtonGroup();
        int nbuttons = buttons.length;
        all.add(new JLabel(), "split " + (1 + nbuttons));
        for (final String bn : buttons) {
            JRadioButton theButton = new JRadioButton(bn);
            if (bn.equals(currentValue)) {
                theButton.setSelected(true);
            }

            theButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    l.doit(bn);
                }

            });

            rb.add(theButton);
            rg.add(theButton);
            all.add(theButton);
        }

        all.add(new JLabel(), "wrap");
    }


    private JPanel addFileChooser(JPanel cf, String theLabel, JTextField resultTextField, ActionListener al) {
        //BorderLayout bl = new BorderLayout(5,5);
        //JPanel cf = new JPanel();
        //cf.setLayout(bl);
        JLabel label = new JLabel(theLabel);
        JTextField tf = resultTextField;
        JButton selectButton = new JButton("Select file");
        selectButton.addActionListener(al);

        cf.add(label);
        cf.add(tf, "growx 100");
        cf.add(selectButton, "wrap");

        //cf.add(configFileStatus, BorderLayout.PAGE_END);
        return null;
    }

    public EmitagConfig getConfig() {
        return emitagConfig;
    }

    interface DoIt {
        void doit(String value);
    }

    private void updateConfig(RegistryPreferences prefs) {
        prefs.setDbFile(dbFileTxt.getText());
        prefs.setSysFile(sysFileTxt.getText());
        prefs.setComPort((String) listOfComPorts.getSelectedItem());
        emitagConfig = new EmitagConfig(dbFileTxt.getText(), sysFileTxt.getText(),
                (String) listOfComPorts.getSelectedItem(), ecardField,jdbcUrl);
    }

    private static class RegistryPreferences {
        final Preferences preferences;

        RegistryPreferences() {
            preferences = Preferences.userNodeForPackage(ConfigFrameMiG.class);
        }

        public void setSysFile(String sysFile) {
            preferences.put("/db/sysFile", sysFile);
        }

        public void setDbFile(String dbFile) {
            preferences.put("/db/etimeFile", dbFile);
        }

        public String getSysFile() {
            return preferences.get("/db/sysFile", "c:\\system.mdb");
        }

        public String getDbFile() {
            return preferences.get("/db/etimeFile", "c:\\etime.mdb");
        }

        public void setMode64(String m) {
            preferences.put("/mode64", m);
        }

        public String getMode64() {
            return preferences.get("/mode64", "64");
        }


        public String getComPort() {
            return preferences.get("/comport", "COM1");
        }

        public void setComPort(String comPort) {
            preferences.put("/comport", comPort);
        }

        public String getEcardField() {
            return preferences.get("/ecardfield", "ecard1");
        }

        public void setEcardField(String s) {
            preferences.put("/ecardfield", s);

        }
    }

    public static class FileTypeFilter {
        final String type;
        final String descr;
        final String buttonTxt;
        final JFrame jframe;

        FileTypeFilter(String type, String descr, String buttonTxt, JFrame jframe) {
            this.type = type;
            this.descr = descr;
            this.buttonTxt = buttonTxt;
            this.jframe = jframe;
        }

        private File pickFile(String cur) {
            JFileChooser dbFile = new JFileChooser(cur);
            dbFile.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (pathname.isDirectory()) return true;
                    if (pathname.getName().toLowerCase().endsWith(type)) {
                        return true;
                    }
                    return false;
                }

                @Override
                public String getDescription() {
                    return descr;
                }
            });
            dbFile.setApproveButtonText(buttonTxt);
            dbFile.showDialog(jframe, null);
            return dbFile.getSelectedFile();

        }

    }

    private void verifyDataSource(RegistryPreferences prefs, JTextField furl) {
        String url = calculateJdbcUrl(prefs, furl);
        int c = 0;
        configStatus.setText("Not yet successful");
        configStatus.setForeground(Color.RED);
        try {
            BasicDataSource ds = new BasicDataSource();
            ds.setDriverClassName(JDBC_DRIVER);
            ds.setUrl(url);
            SimpleJdbcTemplate sjt = new SimpleJdbcTemplate(ds);
            c = sjt.queryForInt("select count(*) from arr");
            if (c>0) {
                String name = findName(sjt);
                configStatus.setText("OK! " + name );
                configStatus.setForeground(Color.GREEN);

            } else {
                configStatus.setForeground(Color.RED);

            }
            butOk.setEnabled(true);
        } catch (Exception ec) {
            log.error("Couldn't run: " + ec);
        }

    }

    private String calculateJdbcUrl(RegistryPreferences prefs, JTextField furl) {
        String driver;

        if ("64".equals(prefs.getMode64())) {
            driver = "Microsoft Access Driver (*.mdb, *.accdb)";
        } else {
            driver = "Microsoft Access Driver (*.mdb)";
        }
        String url = String.format("jdbc:odbc:Driver={%s};dbq=%s;SystemDB=%s;UID=admin",
                driver, prefs.getDbFile(), prefs.getSysFile()).replaceAll("\\\\","/");
        furl.setText(url);
        this.jdbcUrl = url;
        return url;
    }

    private String findName(SimpleJdbcTemplate sjt) {
        return sjt.queryForObject("select name from arr", String.class);
    }



}
