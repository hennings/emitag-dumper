package net.spjelkavik.emit.ept;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ComponentInputMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ActionMapUIResource;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/*

  update ecard set ecard=startno

 */

public class AnonEmitTradApp extends JFrame implements ActionListener, BadgeListener{

	Logger log = Logger.getLogger(AnonEmitTradApp.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4059084342591755190L;

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

		BeanFactory f = new ClassPathXmlApplicationContext(new String[] {"applicationContext.xml"} );
		EtimingReader et = (EtimingReader) f.getBean("etimingReader");

		AnonEmitTradApp af = new AnonEmitTradApp();
		af.setEtimingReader(et);

		String com = "COM1";
		if (args.length>0) {
			com=args[0];
			System.out.println("Using port " + com);
		}

		Read250.findPort(com);
		Read250 reader = new Read250();
		reader.setCallback(af);

		UIManager.put("swing.boldMetal", Boolean.FALSE);
	}

	private EtimingReader etimingReader;

	private void setEtimingReader(EtimingReader et) {
		this.etimingReader = et;
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

	File logfile = new File("c:/tyrving/db/skole2013/log1.txt");
	File logfile2 = new File("c:/tyrving/db/skole2013/log2.txt");
	File logfile3 = new File("c:/tyrving/db/skole2013/log-stnr-for-spool.log");

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
		return NumberUtils.toInt(startNumberField.getText(),-1);
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

	public AnonEmitTradApp() {
		
		
		
		//give the window a name                  
		super("Anonyme brikker");

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new BorderLayout());

		runnerNameLabel = new JLabel("<html><h1>xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</h1>");
		runnerTimeLabel = new JLabel("00:00:00");
		runnerTimeLabel.setFont(new Font("Sans serif", Font.BOLD, 16));
		
		runnerNameLabel.setPreferredSize(new Dimension(600,40));
		
		//add the button                  
		saveDataButton = new JButton("Oppdater");
		saveDataButton.addActionListener(this);

		statusLabel = new JLabel("Startup!");
		
		Container thispane = this.getContentPane();
		
		thispane.setLayout(new BoxLayout(thispane, BoxLayout.PAGE_AXIS));
		
		JPanel superTop = new JPanel();
		superTop.setLayout(new BoxLayout(superTop, BoxLayout.PAGE_AXIS));
		
		thispane.add(superTop);
		Container pane = new JPanel(new BorderLayout());
		superTop.add(pane);
		
		
//		logArea.setPreferredSize(new Dimension(500,150));
		logArea.setFocusable(false);
		logArea.setFont(new Font("Courier New", Font.PLAIN, 12));
		
		logArea.setLineWrap(false);
		
		sp = new JScrollPane(logArea);
		sp.setPreferredSize(new Dimension(500,150));
		
		superTop.add(sp);
		
		pane.add(saveDataButton,BorderLayout.WEST);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		pane.add(topPanel, BorderLayout.NORTH );
		topPanel.add(runnerNameLabel,BorderLayout.NORTH);
		topPanel.add(runnerTimeLabel, BorderLayout.SOUTH);
		topPanel.add(statusLabel, BorderLayout.EAST);

		fetchRunnerButton = new JButton("Hent loper");
		fetchRunnerButton.addActionListener(this);
		pane.add(fetchRunnerButton, BorderLayout.EAST);

		prevLabel = new JLabel("previous, previous");
		prevLabel.setFont(new Font("Sans serif", Font.PLAIN, 10));

		JPanel panel = new JPanel(new BorderLayout());
		pane.add(panel, BorderLayout.SOUTH);

		panel.add(prevLabel, BorderLayout.NORTH);
		
		startNumberField = new JTextField(16);
		panel.add(startNumberField, BorderLayout.WEST);
		startNumberField.addActionListener(this);

		
		brikkeNrLabel = new JLabel("brikkenr");
		brikkeNrLabel.setFont(new Font("Sans serif", Font.PLAIN, 18));
		panel.add(brikkeNrLabel, BorderLayout.EAST);

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

	public void setFrame(Frame f) {
		this.frame = f;
		this.setBadgeNumber(f.getBadgeNo());
		this.setRunningTime(f.getRunningTime());
	}
	
	private void setRunningTime(String runningTime) {
		this.runnerTimeLabel.setText(runningTime);
	}

	public void setBadgeNumber(int badge) {
		clearStatus();
		brikkeNrLabel.setText(":" + badge);
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

