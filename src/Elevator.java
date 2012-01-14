import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// The elevator class draws the elevator area and simulates elevator movement
@SuppressWarnings("serial")
class Elevator extends JPanel implements ActionListener {
	// Declaration of variables

	private Elevator_Simulation app; // the Elevator Simulation frame
	private boolean up; // the elevator is moving up or down
	private int width; // Elevator width
	private int height; // Elevator height
	private int xco; // The x coordinate of the elevator's upper left corner
	private int yco; // The y coordinate of the elevator's upper left corner
	private int dy0; // Moving interval
	//private int topy; // the y coordinate of the top level
	private int bottomy; // the y coordinate of the bottom level
	private Timer tm; // the timer to drive the elevator movement
	// other variables to be used ...

	private boolean paused = false;
	private int pauseForCounter = 0;
	private int pauseForDuration = 20;
	private int doorWidth = 1;
	private int floorIndexPausedAt = -1;

	public static int TIMER_DEFAULT = 40;
	public static int OFFSET_DEFAULT = 4;

	private boolean activeScan = false;
	private boolean optimiseScan = true;
	private boolean energySaving = false;
	private boolean dropOffAtFloor = false;

	// constructor
	public Elevator(Elevator_Simulation app) {

		// necessary initialisation

		this.app = app; // Assign ref to app to retain reference
		up = true; // Start going up
		width = app.control.b[0].getWidth();
		// height specified in paint method to cater for resize

		// adjust Y coordinate to simulate elevator movement
		dy0 = OFFSET_DEFAULT;

		tm = new Timer(TIMER_DEFAULT, this);
	}

	public void setStartPosition(int y, int _width, int _height) {

		yco = y;
		this.width = _width;
		this.height = _height;

		// Update topy and bottomy
		//topy = 0;
		bottomy = app.control.b[0].getY();
	}

	// Paint elevator area
	@Override
	public void paintComponent(Graphics g) {

		// obtain geometric values of components for drawing the elevator area
		height = app.control.b[0].getHeight();
		xco = (this.getWidth() - width) / 2;
		bottomy = app.control.b[0].getY();

		// clear the painting canvas
		super.paintComponent(g);

		// start the Timer if not started elsewhere
		if (!tm.isRunning()) {
			setStartPosition(app.control.b[0].getY(),
					app.control.b[0].getWidth(), app.control.b[0].getHeight());
			tm.start();
		}

		// set background color
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, this.getWidth(), app.control.b[0].getY()
				+ app.control.b[0].getHeight());

		// draw horizontal lines
		g.setColor(Color.BLACK);
		for (int i = 0; i < Elevator_Simulation.NUM_OF_FLOORS; i++) {
			// g.drawLine(0, i * height, this.getWidth(), i * height);
			g.drawLine(0, app.control.b[i].getY(), this.getWidth(),
					app.control.b[i].getY());
		}
		// draw final line
		int bottomLine = app.control.b[0].getY() + app.control.b[0].getHeight();
		g.drawLine(0, bottomLine, this.getWidth(), bottomLine);

		// draw the elevator
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(xco, yco, width, height);

		// draw door
		g.setColor(Color.DARK_GRAY);
		g.fillRect(xco + ((width - doorWidth) / 2), yco, doorWidth, height);
		// g.drawLine(xco + (width / 2), yco, xco + (width / 2), yco + height);

	}

	// Handle the timer events
	public synchronized void actionPerformed(ActionEvent e) {

		try {

			// loop if the elevator needs to be stopped for a while
			if (paused) {
				if (pauseForCounter >= pauseForDuration) {
					paused = false;
					pauseForCounter = 0;

					// Drop off at a particular floor enabled?
					// only if we are here to drop off ONLY
					if (dropOffAtFloor && app.control.bp[floorIndexPausedAt]) {
						int newDropOffAtFloor = getRandomFloorIndexToDropOffAt(floorIndexPausedAt);
						app.control.dropOffAtFloor(newDropOffAtFloor);
					}

					if (optimiseScan) {

						// Optimised look ahead logic
						if ((up && floorIndexPausedAt >= (Elevator_Simulation.NUM_OF_FLOORS / 2) && !hasPassengersAbove(floorIndexPausedAt + 1))
							|| (!up && floorIndexPausedAt < (Elevator_Simulation.NUM_OF_FLOORS / 2) && !hasPassengersBelow(floorIndexPausedAt - 1))) {
							up = !up;
						}

					}
					
					// clear floor button
					app.control.passengerPickedUpOrDroppedOff(floorIndexPausedAt);
					floorIndexPausedAt = -1;

					// reset door values
					doorWidth = 1;

				} else {
					pauseForCounter++;

					// door animation
					int halfDuration = pauseForDuration / 2;
					int offset = width / halfDuration;
					if (pauseForCounter < halfDuration) {
						doorWidth += offset;
					} else if (doorWidth > offset) {
						doorWidth -= offset;
					}

					// ensure floor is in sync
					yco = app.control.b[floorIndexPausedAt].getY();

					// update status message

					String text = "";
					if (app.control.b[floorIndexPausedAt].getBackground() == ButtonPanel.COLOR_SELECTED) {
						text = "Picking up passengers";
					} else if (app.control.b[floorIndexPausedAt]
							.getBackground() == ButtonPanel.COLOR_DROPOFF) {
						text = "Dropping off passengers";
					} else {
						text = "Picking up and dropping off passengers";
					}
					app.state.setText(text + " from floor "
							+ (floorIndexPausedAt + 1));

				}

			} else {


				// test if elevator is at a floor
				int floorHeight = app.control.b[0].getHeight();
				int modFloorHeight = (yco + dy0) % floorHeight;
				if (modFloorHeight <= dy0 * 2) {

					// int modFloorHeight = yco % floorHeight;
					// if ((modFloorHeight >= 0 && modFloorHeight <= (dy0)) ||
					// (modFloorHeight >= floorHeight-(dy0) && modFloorHeight <=
					// floorHeight)) {
					// int floorIndex = Elevator_Simulation.NUM_OF_FLOORS - (yco
					// / height) - 1;

					int floorIndex = (int) ((yco + dy0) / floorHeight);
					// Correct index value
					floorIndex = Elevator_Simulation.NUM_OF_FLOORS - floorIndex-1;

					// change moving direction when hits the top and bottom (NORMAL
					// LOGIC)
					if (floorIndex == Elevator_Simulation.NUM_OF_FLOORS-1 && up) {
						up = false;
						yco = 0-dy0;
					} else if (floorIndex == 0 && !up) {
						up = true;
						yco = bottomy+dy0;
					}
					
					
					// check if floor's button is pressed
					// or dropOff Logic enabled and has people to drop off
					if (app.control.bp[floorIndex]
							|| (dropOffAtFloor && app.control.dropAt[floorIndex])) {

						// pause a short while
						paused = true;
						floorIndexPausedAt = floorIndex;

						// no need to move the elevator, just break out
						return;

					}


					// Test for energy Saving to freeze lift
					if (energySaving) {

						// check if there are buttons pressed
						// Should change to a queue system to capture buttons
						// pressed but perhaps when I have more time
						boolean hasButtonPressed = false;
						for (int i = 0; i < Elevator_Simulation.NUM_OF_FLOORS; i++) {
							if (app.control.bp[i] || (dropOffAtFloor && app.control.dropAt[i] )) {
								hasButtonPressed = true;
								break;
							}
						}

						// If there are no buttons pressed and elevator is
						// exactly on a floor
						if (!hasButtonPressed) {
							alignToFloorIndex(floorIndex);
							app.state.setText("The elevator is taking a break!");

							// ensure floor is in sync
							yco = app.control.b[floorIndex].getY();
							repaint();
							return;
						}
					}

				}
				
				
				// If active scan is enabled, do a scan to see if there are
				// buttons in the opposite direction that have been pressed
				// Here, floorIndex get the floor that the yco is currently within
				if (activeScan) {

					int floorIndex = Elevator_Simulation.NUM_OF_FLOORS
							- (yco / height) - 1;

					if ((up && hasPassengersBelow(floorIndex - 1) && !hasPassengersAbove(floorIndex))
							|| (!up && hasPassengersAbove(floorIndex) && !hasPassengersBelow(floorIndex - 1))) {
						up = !up;
					}

				}

				// Move elevator
				if (up) {
					yco -= dy0;
				} else {
					yco += dy0;
				}

				// update the state of the elevator
				app.state.setText("The elevator is moving " + (up ? "up" : "down") );
			}

			// repaint the panel
			repaint();

		} catch (Exception ex) {
			JOptionPane
					.showMessageDialog(
							this,
							"An error has occured! If you repeatedly get this error, please report to the developers.\n\n Program will reset to position lift at Start Position."
									+ ex.getMessage(), "Error",
							JOptionPane.ERROR_MESSAGE);
			
			setStartPosition(app.control.b[0].getY(),
					app.control.b[0].getWidth(), app.control.b[0].getHeight());
		}

	}

	private boolean hasPassengersAbove(int floorNumberToScanAbove) {

		/*
		 * // Ensure no passengers below for (int i = floorNumberToScanAbove-1;
		 * i >= 0; i--) { if (app.control.bp[i]) { return false; } }
		 */

		for (int i = floorNumberToScanAbove; i < Elevator_Simulation.NUM_OF_FLOORS; i++) {
			if (app.control.bp[i] || (dropOffAtFloor && app.control.dropAt[i])) {
				return true;
			}
		}

		return false;
	}

	private boolean hasPassengersBelow(int floorNumberToScanBelow) {

		/*
		 * // Ensure no passengers above for (int i = floorNumberToScanBelow+1;
		 * i < Elevator_Simulation.NUM_OF_FLOORS; i++) { if (app.control.bp[i])
		 * { return false; } }
		 */

		for (int i = floorNumberToScanBelow; i >= 0; i--) {
			if (app.control.bp[i] || (dropOffAtFloor && app.control.dropAt[i])) {
				return true;
			}
		}

		return false;
	}

	public boolean updateTimerDelay(double delayPercent) {

		tm.setDelay((int) ((double) TIMER_DEFAULT * delayPercent));

		return true;
	}

	public void alignToFloorIndex(int floorIndex) {

		// Validation check
		if (floorIndex < 0 || floorIndex >= Elevator_Simulation.NUM_OF_FLOORS) {
			return;
		}

		yco = app.control.b[floorIndex].getY();

	}

	public boolean isActiveScan() {
		return activeScan;
	}

	public void setActiveScan(boolean activeScan) {
		this.activeScan = activeScan;
	}

	public boolean isOptimiseScan() {
		return optimiseScan;
	}

	public void setOptimiseScan(boolean optimiseScan) {
		this.optimiseScan = optimiseScan;
	}

	public boolean isEnergySaving() {
		return energySaving;
	}

	public void setEnergySaving(boolean energySaving) {
		this.energySaving = energySaving;
	}

	public boolean isDropOff() {
		return dropOffAtFloor;
	}

	public void setDropOff(boolean dropOff) {
		this.dropOffAtFloor = dropOff;

		if (!dropOff) {
			// Need to clear existing dropOff floors
			for (int i = 0; i < Elevator_Simulation.NUM_OF_FLOORS; i++) {
				app.control.passengerPickedUpOrDroppedOff(i);
			}
		}
	}

	public void setTimerEnabled(boolean enable) {

		if (enable && !tm.isRunning()) {
			tm.start();
		} else if (!enable && tm.isRunning()) {
			tm.stop();
		}

	}

	private int getRandomFloorIndexToDropOffAt(int currentFloorIndex) {

		int floorToDrop = currentFloorIndex;
		// To ensure that the floor dropping off at is not the current floor
		while (floorToDrop == currentFloorIndex) {
			floorToDrop = (int) (Math.random() * Elevator_Simulation.NUM_OF_FLOORS);
		}

		return floorToDrop;

	}

} // the end of Elevator class

