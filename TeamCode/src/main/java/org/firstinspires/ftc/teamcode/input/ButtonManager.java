package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;

import org.firstinspires.ftc.teamcode.input.gamepad.IButtonAction;
import org.firstinspires.ftc.teamcode.input.gamepad.IJoystickAction;
import org.firstinspires.ftc.teamcode.input.gamepad.ILinearAction;
import org.firstinspires.ftc.teamcode.input.gamepad.JoystickDimension;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.ButtonListener;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.JoystickLinearListener;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.JoystickListener;
import org.firstinspires.ftc.teamcode.input.gamepad.annotations.LinearListener;
import org.firstinspires.ftc.teamcode.input.gamepad.values.GamepadButtonGetter;
import org.firstinspires.ftc.teamcode.input.gamepad.values.GamepadJoystickGetter;
import org.firstinspires.ftc.teamcode.input.gamepad.values.LinearValueGetter;
import org.firstinspires.ftc.teamcode.input.gamepad.values.IGamepadButton;
import org.firstinspires.ftc.teamcode.input.gamepad.values.IGamepadJoystick;
import org.firstinspires.ftc.teamcode.input.gamepad.values.ILinearValue;
import org.firstinspires.ftc.teamcode.input.gamepad.values.IValueGetter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles the logic for a collection of buttons.
 * <pre>
 * How to use:
 * * Register all of your gamepad or other variables using registerThing()
 * * Define listeners using @ThingListener
 * * Register your class' listeners
 * * Add buttons.update() to your update loop
 * 
 * You may also use everyday buttons using add(Button), without any fancy listeners or trackers.
 * </pre>
 */
public class ButtonManager {
	///
	///
	/// WARNING: The code you're about to read is pretty complex (and massive!),
	/// and there's quite a few things that could probably be
	/// simplified a lot still.
	///
	/// Even the comments aren't very helpful. Good luck!
	///
	
	public final double JOYSTICK_ERROR_RANGE = 0.1;
	
	/** The tracked buttons: a pair of the button itself, and whether it was pressed last update. */
	protected final List<SimpleEntry<Button, Boolean>> buttons = new ArrayList<>();
	
	///
	/// THE GREAT LISTS OF TRACKED ACTIONS
	///
	
	private Map<String, List<IButtonAction>> buttonPressActions = new HashMap<>();
	private Map<String, List<IButtonAction>> buttonReleaseActions = new HashMap<>();

	private Map<String, IGamepadJoystick> joysticks = new HashMap<>();
	private Map<String, List<IJoystickAction>> joystickValueActions = new HashMap<>();
	private Map<String, List<IButtonAction>> joystickReleaseActions = new HashMap<>();
	private Map<String, List<ILinearAction>> joystickXValueActions = new HashMap<>();
	private Map<String, List<IButtonAction>> joystickXReleaseActions = new HashMap<>();
	private Map<String, List<ILinearAction>> joystickYValueActions = new HashMap<>();
	private Map<String, List<IButtonAction>> joystickYReleaseActions = new HashMap<>();
	
	private Map<String, ILinearValue> linears = new HashMap<>();
	private Map<String, List<ILinearAction>> linearValueActions;
	private Map<String, List<IButtonAction>> linearReleaseActions;
	
	/** Tracks a new button. */
	public void add(Button button) {
		this.buttons.add(new SimpleEntry<>(button, false));
	}
	
	/** Most of the stuff needed for {@link #registerButton}, only factored out to enable {@link #registerToggle}. */
	private Button prepareButton(final String name, final IGamepadButton button) {
		buttonPressActions.put(name, new ArrayList<>());
		buttonReleaseActions.put(name, new ArrayList<>());
		
		return new Button() {
			@Override
			public boolean isInputPressed() {
				return button.isPressed();
			}
			
			@Override
			public void onPress() {
				for(IButtonAction action : buttonPressActions.get(name))
					action.act();;
			}
			
			@Override
			public void onRelease() {
				for(IButtonAction action : buttonReleaseActions.get(name))
					action.act();;
			}
		};
	}

	/**
	 * Registers a boolean variable for tracking.
	 * @param name The name of the button
	 * @param button The button's tracker
	 */
	public void registerButton(final String name, final IGamepadButton button) {
		add(prepareButton(name, button));
	}
	
	/**
	 * Makes it possible to implement e.g. an IGamepadButton by tracking a field, rather than making a new one each time.
	 * @param object The object that the field is in.
	 * @param fieldName The field to track.
	 * @return A getter for that object's field.
	 */
	private<T> IValueGetter<T> makeFieldGetter(Object object, String fieldName) {
		Field field;
		try {
			field = object.getClass().getField(fieldName);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new IValueGetter<T>() {
			@Override
			public T get() {
				try {
					return (T) field.get(object);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	/**
	 * Registers a boolean variable for tracking.
	 * @param name The name of the button
	 * @param object The object whose field you want to track
	 * @param field The field to track as a button
	 */
	public void registerButton(final String name, final Object object, final String field) {
		registerButton(name, new GamepadButtonGetter(makeFieldGetter(object, field)));
	}
	
	/**
	 * Registers a boolean variable for tracking so that it behaves like a toggle.
	 * @param name The name of the button
	 * @param button The button's tracker
	 */
	public void registerToggle(final String name, final IGamepadButton button) {
		add(prepareButton(name, button).toToggle());
	}
	
	/**
	 * Registers a boolean variable for tracking so that it behaves like a toggle.
	 * @param name The name of the button
	 * @param object The object whose field you want to track
	 * @param field The field to track as a button
	 */
	public void registerToggle(final String name, final Object object, final String field) {
		registerToggle(name, new GamepadButtonGetter(makeFieldGetter(object, field)));
	}
	
	/** Gets the linear value with the given name. */
	private ILinearValue getLinear(String name) {
		return linears.get(name);
	}
	
	/** Gets the joystick with the given name. */
	private IGamepadJoystick getJoystick(String name) {
		return joysticks.get(name);
	}
	
	/** Gets the value of the linear variable. */
	private float getLinearValue(ILinearValue value) {
		return value.getValue();
	}
	
	/** Gets the value of the linear variable with the given name. */
	private float getLinearValue(String name) {
		return getLinearValue(getLinear(name));
	}
	
	/**
	 * @param joystick The joystick to get the position of
	 * @param variable The dimension of the joystick to get
	 * @return The position of the joystick in the given dimension.
	 */
	private float getJoystickPosition(IGamepadJoystick joystick, JoystickDimension variable) {
		if(variable == JoystickDimension.X)
			return joystick.getPositionX();
		return joystick.getPositionY();
	}
	
	/**
	 * @param joystick The name of the joystick to get the position of
	 * @param variable The dimension of the joystick to get
	 * @return The position of the joystick in the given dimension.
	 */
	private float getJoystickPosition(String joystick, JoystickDimension variable) {
		return getJoystickPosition(joysticks.get(joystick), variable);
	}
	
	/**
	 * @param joystick The joystick to get the position of
	 * @param variable The dimension of the joystick to get
	 * @return The position of the joystick in the given dimension such that negligible values are adjusted to zero.
	 */
	private float getAdjustedJoystickPosition(IGamepadJoystick joystick, JoystickDimension variable) {
		float joystickPosition = getJoystickPosition(joystick, variable);
		if(Math.abs(joystickPosition) - JOYSTICK_ERROR_RANGE <= 0)
			return 0;
		return joystickPosition;
	}
	
	/**
	 * @param joystick The joystick to get the position of
	 * @param variable The dimension of the joystick to get
	 * @return The position of the joystick in the given dimension such that negligible values are adjusted to zero.
	 */
	private float getAdjustedJoystickPosition(String joystick, JoystickDimension variable) {
		return getAdjustedJoystickPosition(getJoystick(joystick), variable);
	}
	
	/** Is the given linear value enabled in any way? */
	private boolean isLinearActive(ILinearValue linear) {
		return getLinearValue(linear) != 0;
	}
	
	/** Is the given linear value enabled in any way? */
	private boolean isLinearActive(String linear) {
		return isLinearActive(getLinear(linear));
	}
	
	/** Is the given joystick pressed in any way in the given dimension? */
	private boolean isJoystickActive(IGamepadJoystick joystick, JoystickDimension variable) {
		return getAdjustedJoystickPosition(joystick, variable) > 0;
	}
	
	/** Is the given joystick pressed in any way in the given dimension? */
	private boolean isJoystickActive(String joystick, JoystickDimension variable) {
		return isJoystickActive(getJoystick(joystick), variable);
	}
	
	/** Is the given joystick pressed in any way? */
	private boolean isJoystickActive(IGamepadJoystick joystick) {
		return isJoystickActive(joystick, JoystickDimension.X)
				|| isJoystickActive(joystick, JoystickDimension.Y);
	}
	
	/** Is the given joystick pressed in any way? */
	private boolean isJoystickActive(String joystick) {
		return isJoystickActive(getJoystick(joystick));
	}
	
	/**
	 * Registers a variable for tracking.
	 * @param name The name of the variable
	 * @param value The tracker for the object's value
	 */
	public void registerLinear(String name, ILinearValue value) {
		linears.put(name, value);
		linearValueActions.put(name, new ArrayList<>());
		linearReleaseActions.put(name, new ArrayList<>());
		
		add(new Button() {
			@Override
			public boolean isInputPressed() {
				return getLinearValue(name) > 0;
			}
			
			@Override
			public void onRelease() {
				for(IButtonAction action : linearReleaseActions.get(name))
					action.act();
			}
		});
	}
	
	/**
	 * Registers a variable for tracking.
	 * @param name The name of the variable
	 * @param object The object whose field you want to track
	 * @param field The field to track
	 */
	public void registerLinear(String name, Object object, String field) {
		registerLinear(name, new LinearValueGetter(makeFieldGetter(object, field)));
	}
	
	/**
	 * Registers a joystick for tracking.
	 * @param name The name of the joystick
	 * @param value The tracker for the joystick's value
	 */
	public void registerJoystick(String name, IGamepadJoystick joystick) {
		joysticks.put(name, joystick);
		joystickXValueActions.put(name, new ArrayList<>());
		joystickXReleaseActions.put(name, new ArrayList<>());
		joystickYValueActions.put(name, new ArrayList<>());
		joystickYReleaseActions.put(name, new ArrayList<>());

		add(new Button() {
			@Override
			public boolean isInputPressed() {
				return isJoystickActive(joystick, JoystickDimension.X);
			}
			
			@Override
			public void onRelease() {
				for(IButtonAction action : joystickXReleaseActions.get(name))
					action.act();
			}
		});
		
		add(new Button() {
			@Override
			public boolean isInputPressed() {
				return isJoystickActive(joystick, JoystickDimension.Y);
			}
			
			@Override
			public void onRelease() {
				for(IButtonAction action : joystickYReleaseActions.get(name))
					action.act();
			}
		});
	}
	
	/**
	 * Registers a joystick for tracking.
	 * @param name The name of the joystick
	 * @param object The object whose field you want to track
	 * @param fieldX The field representing the joystick's X
	 * @param fieldY The field representing the joystick's Y
	 */
	public void registerJoystick(String name, Object object, String fieldX, String fieldY) {
		registerJoystick(name, new GamepadJoystickGetter(makeFieldGetter(object, fieldX),
														makeFieldGetter(object, fieldY)));
	}
	
	/**
	 * Registers a joystick for tracking.
	 * @param name The name of the joystick
	 * @param object The object whose field you want to track
	 * @param base_field The base name of the fields to track. E.g. "my_joystick" produces "my_joystick_x" and "my_joystick_y".
	 */
	public void registerJoystick(String name, Object object, String base_field) {
		registerJoystick(name, object, base_field + "_x", base_field + "_y");
	}
	
	/**
	 * Lets you alias one of the dimensions of a joystick to something more convenient. Example:
	 * <pre>
	 * // Before
	 * @JoystickLinearListener("myJoystick", JoystickDimension.X)
	 * // After
	 * @LinearListener("myThing")
	 * </pre>
	 * @param joystick The joystick to alias
	 * @param alias The new name of the dimension
	 * @param variable The dimension of the joystick to alias
	 */
	public void registerJoystickLinearAlias(String joystick, String alias, JoystickDimension variable) {
		registerLinear(alias, new ILinearValue() {
			@Override
			public float getValue() {
				return getAdjustedJoystickPosition(joystick, variable);
			}
		});
	}
	
	/** Triggers the actions for just the buttons. */
	private void updateButtons() {
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
	
	/** Triggers the actions for just the variables. */
	private void updateLinears() {
		for(String linear : linears.keySet()) {
			if(isLinearActive(linear))
				for(ILinearAction action : linearValueActions.get(linear))
					action.act(getLinearValue(linear));
		}
	}
	
	/** Gets the appropriate joystick_ValueActions depending upon the dimension you want. */
	private Map<String, List<ILinearAction>> getJoystickLinearActions(JoystickDimension variable)
	{
		if(variable == JoystickDimension.X) 
			return joystickXValueActions;
		return joystickYValueActions;
	}
	
	/** Gets the appropriate joystick_ReleaseActions depending upon the dimension you want. */
	private Map<String, List<IButtonAction>> getJoystickLinearReleaseActions(JoystickDimension variable)
	{
		if(variable == JoystickDimension.X) 
			return joystickXReleaseActions;
		return joystickYReleaseActions;
	}
	
	/** Gets the appropriate joystick_ValueActions for a specific joystick depending upon the dimension you want. */
	private List<ILinearAction> getJoystickLinearActions(String joystick, JoystickDimension variable)
	{
		getJoystickLinearActions(variable).get(joystick);
		if(variable == JoystickDimension.X) 
			return joystickXValueActions.get(joystick);
		return joystickYValueActions.get(joystick);
	}
	
	/** Gets the appropriate joystick_ReleaseActions for a specific joystick depending upon the dimension you want. */
	private List<IButtonAction> getJoystickLinearReleaseActions(String joystick, JoystickDimension variable)
	{
		return getJoystickLinearReleaseActions(variable).get(joystick);
	}
	
	/** Runs the the joystick's linear actions. */
	private void runLinearActions(String joystick, JoystickDimension variable) {
		for(ILinearAction action : getJoystickLinearActions(joystick, variable)) {
			action.act(getJoystickPosition(joystick, variable));
		}
	}
	
	/** Runs the joystick's normal actions. */
	private void runJoystickActions(String joystick) {
		for(IJoystickAction action : joystickValueActions.get(joystick)) {
			action.act(getJoystickPosition(joystick, JoystickDimension.X),
					getJoystickPosition(joystick, JoystickDimension.Y));
		}
	}
	
	/** Runs the actions for all of the joysticks. */
	private void updateJoysticks() {
		// I wrote a poem about streams here to express my joy.
		// Then I had to move to foreach because of a restructuring.
		for(String joystick : joysticks.keySet()) {
			if(isJoystickActive(joystick)) {
				runJoystickActions(joystick);
				if(isJoystickActive(joystick, JoystickDimension.X))
					runLinearActions(joystick, JoystickDimension.X);
				if(isJoystickActive(joystick, JoystickDimension.Y))
					runLinearActions(joystick, JoystickDimension.Y);
			}
		}
	}
	
	/** Runs the actions for everything tracked by this ButtonManager. */
	public void update() {
		updateButtons();
		updateLinears();
		updateJoysticks();
	}
	
	/** Wraps a method simple invocation into a more usable interface. */
	private IButtonAction makeButtonAction(Method method, Object object) {
		if(method.getParameterCount() != 0)
			throw new RuntimeException("Annotated button listener had too many parameters!");
		return new IButtonAction() {
			@Override
			public void act() {
				try {
					method.invoke(object);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	/** Wraps a method simple invocation into a more usable interface. */
	private ILinearAction makeLinearAction(Method method, Object object) {
		return new ILinearAction() {
			@Override
			public void act(float position) {
				try {
					method.invoke(object, position);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	/** Wraps a method simple invocation into a more usable interface. */
	private IJoystickAction makeJoystickAction(Method method, Object object) {
		return new IJoystickAction() {
			@Override
			public void act(float x, float y) {
				try {
					method.invoke(object, x, y);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		};
	}
	
	/** Reads all of the functions in the entire given object for those tagged with @SomethingListener, and tracks them. */
	public void registerListeners(Object object) {
		for(Method method : object.getClass().getMethods()) {
			//
			// All of these follow the same format, with some variations:
			// 1. Get all of the annotations of a particular type on the method.
			// 2. Find out what type of event the method is listing for (i.e. pressed or released)
			// 3. Convert the method into a usable interface
			// 4. Register the method into the appropriate tracker variable
			//
			
			for(ButtonListener annotation : method.getDeclaredAnnotationsByType(ButtonListener.class)) {
				IButtonAction action = makeButtonAction(method, object);
				switch(annotation.event()) {
				case PRESS:
					buttonPressActions.get(annotation.button()).add(action);
					break;
				case RELEASE:
					buttonReleaseActions.get(annotation.button()).add(action);
					break;
				}
			}
			
			for(LinearListener annotation : method.getDeclaredAnnotationsByType(LinearListener.class)) {
				if(method.getParameterCount() == 0) {
					IButtonAction action = makeButtonAction(method, object);
					linearReleaseActions.get(annotation.name()).add(action);
				} else if(method.getParameterCount() == 1
						&& method.getParameterTypes()[0].equals(float.class)) {
					ILinearAction action = makeLinearAction(method, object);
					linearValueActions.get(annotation.name()).add(action);
				} else throw new RuntimeException("Invalid parameters for a joystick listener!");
			}
			
			for(JoystickLinearListener annotation : method.getDeclaredAnnotationsByType(JoystickLinearListener.class)) {
				if(method.getParameterCount() == 0) {
					IButtonAction action = makeButtonAction(method, object);
					getJoystickLinearReleaseActions(annotation.variable()).get(annotation.joystick()).add(action);
				} else if(method.getParameterCount() == 1
						&& method.getParameterTypes()[0].equals(float.class)) {
					ILinearAction action = makeLinearAction(method, object);
					getJoystickLinearActions(annotation.variable()).get(annotation.joystick()).add(action);
				} else throw new RuntimeException("Invalid parameters for a joystick listener!");
			}
			
			for(JoystickListener annotation : method.getDeclaredAnnotationsByType(JoystickListener.class)) {
				if(method.getParameterCount() == 0) {
					IButtonAction action = makeButtonAction(method, object);
					joystickReleaseActions.get(annotation.joystick()).add(action);
				} else if(method.getParameterCount() == 2
						&& method.getParameterTypes()[0].equals(float.class)
						&& method.getParameterTypes()[1].equals(float.class)) {
					IJoystickAction action = makeJoystickAction(method, object);
					joystickValueActions.get(annotation.joystick()).add(action);
				} else throw new RuntimeException("Invalid parameters for a joystick listener!");
			}
		}
	}
}
