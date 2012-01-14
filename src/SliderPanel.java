
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

//The ButtonPanel class receives and handles button pressing events
@SuppressWarnings("serial")
class SliderPanel extends JSlider implements ChangeListener  {

	public static int SLIDER_DEFAULT = 10;
	private Elevator_Simulation app; //the Elevator Simulation frame
	
    //constructor
    public SliderPanel(Elevator_Simulation app) {

    	this.app = app; // Assign ref to app to retain reference
    	
    	this.setOrientation(JSlider.VERTICAL);
    	this.setMinimum(2);
    	this.setMaximum(20);
    	this.setValue(SLIDER_DEFAULT);
    	
    	this.setMajorTickSpacing(2);
    	this.setMinorTickSpacing(1);
    	this.setPaintLabels(true);
        this.setPaintTicks(true);
        this.setSnapToTicks(true);
        
        this.setBorder(new EmptyBorder(10,10,10,10));
        
        this.addChangeListener(this);


    }


    public synchronized void stateChanged(ChangeEvent e) {
        int percentage = this.getValue();
        app.updateTimerDelay((double)SLIDER_DEFAULT/percentage);
      }

    
} //the end of ButtonPanel class

