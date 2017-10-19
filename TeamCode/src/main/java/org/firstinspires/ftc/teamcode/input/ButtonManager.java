package org.firstinspires.ftc.teamcode.input;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles the logic for a collection of buttons.
 */
public class ButtonManager {
	/** The tracked buttons: a pair of the button itself, and whether it was pressed last update. */
	protected final List<SimpleEntry<Button, Boolean>> buttons = new ArrayList<>();
	
	/** Performs the update logic for all buttons. */
	public void update() {
		for(SimpleEntry<Button, Boolean> buttonTracker : buttons) {
			Button button = buttonTracker.getKey();
			// Is the button pressed right now?
			// Do not confuse this with saying "yes, the button is pressed"! -- it's a question, not a statement.
			boolean isPressed = button.isInputPressed();
			// Was the button pressed last time we checked?
			boolean wasPressed = buttonTracker.getValue();
			
			// If the button has changed state (i.e. been pressed or released) ...
			// This means we don't accidentally run the button's press behavior twice.
			if(isPressed != wasPressed) {
				if(isPressed) button.onPress();
				else button.onRelease();
				
				// The previous button state is now the actual current state!
				buttonTracker.setValue(isPressed);
			}
		}
	}
	
	/** Tracks a new button. */
	public void add(Button button) {
		this.buttons.add(new SimpleEntry<>(button, false));
	}
}
