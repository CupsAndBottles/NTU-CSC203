
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//The ButtonPanel class receives and handles button pressing events
@SuppressWarnings("serial")
class ButtonPanel extends JPanel implements ActionListener {

    public JButton b[] = new JButton[Elevator_Simulation.NUM_OF_FLOORS]; // Buttons

    public boolean bp[] = new boolean[Elevator_Simulation.NUM_OF_FLOORS]; // the state of each button, pressed or not
    public boolean dropAt[] = new boolean[Elevator_Simulation.NUM_OF_FLOORS]; // Floor to drop off
    
    public static Color COLOR_ORIGINAL = Color.CYAN;
    public static Color COLOR_SELECTED = Color.RED;
    public static Color COLOR_DROPOFF = Color.GREEN;
    public static Color COLOR_SELECTED_AND_DROPOFF = Color.ORANGE;

    //constructor
    public ButtonPanel() {

        // Get the content pane of the frame
        this.setLayout(new GridLayout(Elevator_Simulation.NUM_OF_FLOORS, 1, 0, 0));

        // loop to add buttons to grid
        for (int i = Elevator_Simulation.NUM_OF_FLOORS; i > 0; i--) {

            int floorIndex = i - 1;
            // init JButton
            b[floorIndex] = new JButton("F" + i);
            b[floorIndex].setBackground(COLOR_ORIGINAL);
            b[floorIndex].addActionListener(this);

            bp[floorIndex] = false; // Not pressed by default

            this.add(b[floorIndex]);
        }


    }

    public void passengerPickedUpOrDroppedOff(int floor) {
        bp[floor] = false;
        b[floor].setBackground(COLOR_ORIGINAL);
        
        // Also check if there are any passengers to drop off and clear them from the dropOff list
        dropAt[floor] = false;
    }
    
    public synchronized void actionPerformed(ActionEvent e) {
        //handle the button pressing events

        String caller = e.getActionCommand();
        // strip first char and parse to int
        int floor = Integer.parseInt(caller.substring(1));
        floor -= 1;
        

        bp[floor] = true;
        if (dropAt[floor]) {
        	// Has people waiting to drop off
        	b[floor].setBackground(COLOR_SELECTED_AND_DROPOFF);
        } else {
        	b[floor].setBackground(COLOR_SELECTED);
        }
        


    }
    
    public void dropOffAtFloor(int floorIndexToDropAt) {
		dropAt[floorIndexToDropAt] = true;
		
		// Change color
		if (bp[floorIndexToDropAt]) {
			b[floorIndexToDropAt].setBackground(COLOR_SELECTED_AND_DROPOFF);
		} else {
			b[floorIndexToDropAt].setBackground(COLOR_DROPOFF);
		}
    }
} //the end of ButtonPanel class

