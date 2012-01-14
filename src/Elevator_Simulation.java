import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

/**
 * 
 * @author Administrator
 */
// The main class
@SuppressWarnings("serial")
public class Elevator_Simulation extends JApplet implements ItemListener {

	public JLabel state; // display the state of the elevator
	private JLabel id; // your name and group
	private JCheckBox cbActiveScan, cbOptimiseScan, cbEnergySaving, cbDropOff;
	public ButtonPanel control; // the button control panel
	private Elevator elevator; // the elevator area
	private JSlider slider;

	private static boolean isStandalone = false;

	private static boolean ENABLE_ADVANCED_OPTIONS = false;
	public static int NUM_OF_FLOORS = 8;

	// constructor
	public Elevator_Simulation() {

	}

	/*
	 * // Main method public static void main(String[] args) { // Create a frame
	 * and display it Elevator_Simulation frame = new Elevator_Simulation();
	 * 
	 * frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	 * //frame.setResizable(false);
	 * 
	 * // Set size and centerise // Get the coordinate (x, y) Dimension
	 * screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	 * 
	 * // Set frame size frame.setSize(600, 400);
	 * 
	 * int x = (screenSize.width - frame.getWidth()) / 2; int y =
	 * (screenSize.height - frame.getHeight()) / 2;
	 * 
	 * // place the frame to the center of the screen frame.setLocation(x, y);
	 * 
	 * frame.setVisible(true);
	 * 
	 * }
	 */

	public static void main(String[] args) {

		// Frame ref to hold applet
		JFrame frame = new JFrame();

		// create and add applet frame
		Elevator_Simulation es = new Elevator_Simulation();
		frame.getContentPane().add(es);

		// process input (if any)
		isStandalone = true;

		if (args.length == 2) {

			try {
				NUM_OF_FLOORS = Integer.parseInt(args[0]);
			} catch (Exception ex) {
			}

			try {
				ENABLE_ADVANCED_OPTIONS = Boolean.parseBoolean(args[1]);
			} catch (Exception ex) {
			}

		}

		
		es.init();

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		


	}

	/** Initialize the applet */
	public void init() {

		// Create GUI
		this.setLayout(new BorderLayout(0, 0));
		this.setSize(700, 400);

		// Check for input values
		if (!isStandalone) {

			if (getParameter("NUM_OF_FLOORS") != "") {

				try {
					NUM_OF_FLOORS = Integer
							.parseInt(getParameter("NUM_OF_FLOORS"));
				} catch (Exception ex) {
				}

			}

			if (getParameter("ENABLE_ADVANCED_OPTIONS") != "") {

				try {
					ENABLE_ADVANCED_OPTIONS = Boolean
							.parseBoolean(getParameter("ENABLE_ADVANCED_OPTIONS"));
				} catch (Exception ex) {
				}
			}

		} else {
			JOptionPane.showMessageDialog(null, 
					"**** OPTIONS INFORMATION ****\n" +
							"Optimised Movement\n" +
							"This is in accordance to 3.3 (LAB 2) requirements. Elevator will skip uppermost and/or lowermost floors should there be no further floors above or below to serve after searving a particular floor.\n\n" +
							"Active Scan\n" +
							"The elevator will constantly scan all floors and immediately head towards the floor with an active button if it is not already on its way to serve a floor.\n\n" +
							"Energy Saving\n" +
							"Elevator will pause at the nearest floor or the last served floor and await further active buttons before moving. 'Energy Saving' mode.\n\n" +
							"Drop Off\n" +
							"Passengers are dropped off at their desired floor in addition to just being picked up. Floor to drop them off is decided randomly for now.\n\n\n\n" +

							"**** FLOOR BUTTON LEGEND ****\n" +
							"RED\n" +
							"Floor selected for pickup of passengers.\n\n" +
							"GREEN\n" +
							"Floor has passengers that need to be dropped off at.\n\n" +
							"ORANGE\n" +
							"Floor has BOTH passengers to pickup and dropoff.\n\n",
							
							"Elevator Interaction Information", 
							JOptionPane.INFORMATION_MESSAGE);


		}

		// Get the content pane of the frame
		Container container = getContentPane();

		// Prep Grid for topbar
		JPanel topbar = new JPanel();
		topbar.setLayout(new FlowLayout());

		// Add top label
		id = new JLabel("Name: Jonathan Samraj Group: SSP3", JLabel.CENTER);
		topbar.add(id);

		if (ENABLE_ADVANCED_OPTIONS) {
			// Add Checkboxs
			cbActiveScan = new JCheckBox("Active Scan", false);
			cbActiveScan
					.setToolTipText("The elevator will constantly scan all floors and immediately head towards the floor with an active button if it is not already on its way to serve a floor.");
			cbActiveScan.addItemListener(this);
			topbar.add(cbActiveScan);

			cbOptimiseScan = new JCheckBox("Optimised Movement", true);
			cbOptimiseScan
					.setToolTipText("This is in accordance to 3.3 (LAB 2) requirements. Elevator will skip uppermost and/or lowermost floors should there be no further floors above or below to serve after searving a particular floor.");
			cbOptimiseScan.addItemListener(this);
			topbar.add(cbOptimiseScan);

			cbEnergySaving = new JCheckBox("Energy Saving", false);
			cbEnergySaving
					.setToolTipText("Elevator will pause at the nearest floor or the last served floor and await further active buttons before moving. 'Energy Saving' mode.");
			cbEnergySaving.addItemListener(this);
			topbar.add(cbEnergySaving);
			
			cbDropOff = new JCheckBox("Drop Off", false);
			cbDropOff
					.setToolTipText("Passengers are dropped off at their desired floor in addition to just being picked up. Floor to drop them off is decided randomly for now.");
			cbDropOff.addItemListener(this);
			topbar.add(cbDropOff);
		}

		container.add(topbar, BorderLayout.NORTH);

		// Add state label
		state = new JLabel("Welcome to the Elevator Simulator", JLabel.CENTER);
		container.add(state, BorderLayout.SOUTH);

		// Add slider
		slider = new SliderPanel(this);
		container.add(slider, BorderLayout.EAST);

		// Init button layout
		control = new ButtonPanel();
		container.add(control, BorderLayout.WEST);

		// Init elevator screen
		elevator = new Elevator(this);
		container.add(elevator, BorderLayout.CENTER);

	}

	public void stop() {
		elevator.setTimerEnabled(false);
	}

	public void updateTimerDelay(double delayPercent) {
		elevator.updateTimerDelay(delayPercent);

	}

	@Override
	public void itemStateChanged(ItemEvent e) {

		if (e.getItemSelectable() == cbActiveScan) {
			elevator.setActiveScan(cbActiveScan.isSelected());
		} else if (e.getItemSelectable() == cbOptimiseScan) {
			elevator.setOptimiseScan(cbOptimiseScan.isSelected());
		} else if (e.getItemSelectable() == cbEnergySaving) {
			elevator.setEnergySaving(cbEnergySaving.isSelected());
		} else if (e.getItemSelectable() == cbDropOff) {
			elevator.setDropOff(cbDropOff.isSelected());
		}

	}

} // the end of Elevator_Simulation class

