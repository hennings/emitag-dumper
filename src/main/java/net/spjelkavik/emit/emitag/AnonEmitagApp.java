package net.spjelkavik.emit.emitag;

import net.miginfocom.swing.MigLayout;
import net.spjelkavik.emit.ept.EtimingReader;
import net.spjelkavik.emit.ept.Frame;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ActionMapUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class AnonEmitagApp extends JFrame implements ActionListener, EmitagMessageListener {



/*

  update ecard set ecard=startno

  Procedure:
    scan number bib - name and team is shown
    read emitag - emitag number is shown prominently
    press enter to save
    the stored combo is moved to the "previous" part
    the system is ready for the next runner



 */


    final static Logger log = Logger.getLogger(AnonEmitagApp.class);

    /**
     *
     */
    private static final long serialVersionUID = 4059084342591755190L;
    private EtimingReader etimingReader;
    private ECBMessage ecbMessage;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        BeanFactory f = new ClassPathXmlApplicationContext(new String[]{"applicationContext.xml"});
        EtimingReader et = (EtimingReader) f.getBean("etimingReader");

        AnonEmitagApp af = new AnonEmitagApp();
        af.setEtimingReader(et);

        String com = "COM1";
        if (args.length > 0) {
            com = args[0];
            System.out.println("Using port " + com);
        }
        if (!"NOCOM".equals(com)) {
            EmitagReader.findPort(com);
            EmitagReader reader = new EmitagReader();
            reader.setCallback(af);
        } else {
            log.info("Skipping COM-port.");
        }

//        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

        UIManager.put("swing.boldMetal", Boolean.FALSE);
    }


    private JButton saveDataButton;
    private int musicNumber = 0;
    private JLabel runnerNameLabel;
    private JTextField startNumberField;

    private JLabel brikkeNrLabel;
    private JButton fetchRunnerButton;
    private Frame frame;

    public void actionPerformed(ActionEvent ae){
        if (ae.getSource() == saveDataButton) {
            updateDatabase();

        }
        if (ae.getSource() == startNumberField) {
            System.out.println("Startnumber action!");
            updateRunner();
            startNumberField.selectAll();
        }
        if (ae.getSource() == fetchRunnerButton) {
            System.out.println("Pushed");
            //update the counter of button presses
            musicNumber++;
            System.out.println("The button has been pressed " +
                    musicNumber + " times");
            updateRunner();
        }

    }

    File logfile = new File("c:/tyrving/db/skole2012/log1.txt");
    File logfile2 = new File("c:/tyrving/db/skole2012/log2.txt");
    File logfile3 = new File("c:/tyrving/db/skole2012/log-stnr-for-spool.log");

    private JLabel runnerTimeLabel;

    private JLabel statusLabel;

    private JLabel prevLabel;

    private void updateDatabase() {
        System.err.println("update database");
        if (frame==null) {
            log.error("No frame read!");
            return;
        }
        System.err.println("Frame: " + frame.getLogLine());
        FileWriter logfw = null;
        try {
            logfw = new FileWriter(logfile, true);
            IOUtils.write(frame.getLogLine(), logfw);
            logfw.close();
        } catch (IOException e) {
            log.error("Could not write log: ", e);
        }
        try {
            logfw = new FileWriter(logfile2, true);
            IOUtils.write("# Start number: " + getStartNumber()+"\n", logfw);
            IOUtils.write(frame.getLogLine(), logfw);
            logfw.close();
        } catch (IOException e) {
            log.error("Could not write log: ", e);
        }

        try {
            logfw = new FileWriter(logfile3, true);
            IOUtils.write(frame.getLogLine(getStartNumber()), logfw);
            logfw.close();
        } catch (IOException e) {
            log.error("Could not write log: ", e);
        }

        boolean ok = etimingReader.updateResults(getStartNumber(), frame);
        if (ok) {

            currentState.ecard = frame.getBadgeNo();
            currentState.time = frame.getRunningTime();

            logArea.append( currentState.toString() +   "\n");
//			sp.
            prevLabel.setText(brikkeNrLabel.getText()+", " + startNumberField.getText());
            statusLabel.setText("<html><em>Stored!</em>");
            brikkeNrLabel.setText("");
            runnerNameLabel.setText("<html><h1>.</h1>");
            runnerTimeLabel.setText("");
            frame = null;

        } else {
            statusLabel.setText("<html><em>Not found</em>");
        }
    }

    public int getStartNumber() {
        return NumberUtils.toInt(startNumberField.getText(), -1);
    }


    public void updateRunner() {
        clearStatus();


        Map<String, String> runner = etimingReader.getRunner(getStartNumber());
        if (runner!=null) {
            runnerNameLabel.setText("<html><h1>"+runner.get("startno")+" " + runner.get("name")+" " + runner.get("ename")+"</h1>");
            currentState.name = runner.get("name")+" " + runner.get("ename");
        } else {
            runnerNameLabel.setText("<html><h1>Unknown...</h1>");
            currentState.name="Unknown...";
            currentState.stnr = getStartNumber();
        }

    }
    private void clearStatus() {
        statusLabel.setText("new");

    }

    JTextArea logArea = new JTextArea();
    JScrollPane sp;

    public AnonEmitagApp() {



        //give the window a name
        super("Anonyme emitag");

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel all = new JPanel(new MigLayout());
        this.add(all);
        all.setBorder(new EmptyBorder(10, 10, 10, 10));



        runnerNameLabel = new JLabel("<html><h1>xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</h1>");
        runnerTimeLabel = new JLabel("00:00:00");
        runnerTimeLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
        runnerNameLabel.setPreferredSize(new Dimension(600,40));

        //add the button
        saveDataButton = new JButton("Oppdater");
        saveDataButton.addActionListener(this);

        statusLabel = new JLabel("Startup!");


//		logArea.setPreferredSize(new Dimension(500,150));
        logArea.setFocusable(false);
        logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        logArea.setLineWrap(false);

        sp = new JScrollPane(logArea);
        sp.setPreferredSize(new Dimension(500,150));


        all.add(runnerNameLabel);
        all.add(runnerTimeLabel);
        all.add(statusLabel, "wrap");


        fetchRunnerButton = new JButton("Hent loper");
        fetchRunnerButton.addActionListener(this);
        all.add(fetchRunnerButton, "wrap");

        all.add(sp, "wrap");
        all.add(saveDataButton, "wrap");

        prevLabel = new JLabel("previous, previous");
        prevLabel.setFont(new Font("Sans serif", Font.PLAIN, 10));

        all.add(prevLabel, "wrap");

        startNumberField = new JTextField(16);
        all.add(startNumberField, "wrap");
        startNumberField.addActionListener(this);


        brikkeNrLabel = new JLabel("brikkenr");
        brikkeNrLabel.setFont(new Font("Sans serif", Font.PLAIN, 18));
        all.add(brikkeNrLabel, "wrap");

        InputMap keyMap = new ComponentInputMap(saveDataButton);
        keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0), "action");

        ActionMap actionMap = new ActionMapUIResource();
        actionMap.put("action", new UpdateAction("updater"));


        SwingUtilities.replaceUIActionMap(saveDataButton, actionMap);
        SwingUtilities.replaceUIInputMap(saveDataButton, JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);


        //display the window
        this.pack();
        this.setVisible(true);

        startNumberField.requestFocus();
    }

    @Override
    public void handleECBMessage(ECBMessage f) {
        this.ecbMessage = f;
        this.setBadgeNumber(f.getEmitagNumber());
        this.setRunningTime(f.getTimeSinceZero());
    }

    private void setRunningTime(String runningTime) {
        this.runnerTimeLabel.setText(runningTime);
    }

    public void setBadgeNumber(int badge) {
        clearStatus();
        brikkeNrLabel.setText(":" + badge);
    }

    public void setEtimingReader(EtimingReader etimingReader) {
        this.etimingReader = etimingReader;
    }

    public EtimingReader getEtimingReader() {
        return etimingReader;
    }

    class UpdateAction extends AbstractAction {
        public UpdateAction(String text) {
            super(text);
        }
        public void actionPerformed(ActionEvent e) {
            System.err.println("Action for first button/menu item: "+ e);
            updateDatabase();
        }
    }

    final CurrentState currentState = new CurrentState();

    class CurrentState {
        String name;
        String time;
        int ecard;
        int stnr;

        public String toString() {
            return String.format("%-30s %10s %8d %8d", name, time, ecard, stnr);
        }

    }

}




