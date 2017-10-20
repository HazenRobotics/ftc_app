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
 */
public class ButtonManager {
	public final double JOYSTICK_ERROR_RANGE = 0.1;
	
	/** The tracked buttons: a pair of the button itself, and whether it was pressed last update. */
	protected final List<SimpleEntry<Button, Boolean>> buttons = new ArrayList<>();
	
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
				for(IButtonAction action : buttonPressActions.get(name)) action.act();;
			}
			
			@Override
			public void onRelease() {
				for(IButtonAction action : buttonReleaseActions.get(name)) action.act();;
			}
		};
	}

	public void registerButton(final String name, final IGamepadButton button) {
		add(prepareButton(name, button));
	}
	
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
	
	public void registerButton(final String name, final Object object, final String field) {
		registerButton(name, new GamepadButtonGetter(makeFieldGetter(object, field)));
	}
	
	public void registerToggle(final String name, final IGamepadButton button) {
		add(prepareButton(name, button).toToggle());
	}
	
	public void registerToggle(final String name, final Object object, final String field) {
		registerToggle(name, new GamepadButtonGetter(makeFieldGetter(object, field)));
	}
	
	private ILinearValue getLinear(String name) {
		return linears.get(name);
	}
	
	private IGamepadJoystick getJoystick(String name) {
		return joysticks.get(name);
	}
	
	private float getLinearValue(ILinearValue value) {
		return value.getValue();
	}
	
	private float getLinearValue(String name) {
		return getLinearValue(getLinear(name));
	}
	
	private float getJoystickPosition(IGamepadJoystick joystick, JoystickDimension variable) {
		if(variable == JoystickDimension.X)
			return joystick.getPositionX();
		return joystick.getPositionY();
	}
	
	private float getJoystickPosition(String name, JoystickDimension variable) {
		return getJoystickPosition(joysticks.get(name), variable);
	}
	
	private float getAdjustedJoystickPosition(IGamepadJoystick joystick, JoystickDimension variable) {
		float joystickPosition = getJoystickPosition(joystick, variable);
		if(Math.abs(joystickPosition) - JOYSTICK_ERROR_RANGE <= 0)
			return 0;
		return joystickPosition;
	}
	
	private float getAdjustedJoystickPosition(String joystick, JoystickDimension variable) {
		return getAdjustedJoystickPosition(getJoystick(joystick), variable);
	}
	
	private boolean isLinearActive(ILinearValue linear) {
		return getLinearValue(linear) != 0;
	}
	
	private boolean isLinearActive(String linear) {
		return isLinearActive(getLinear(linear));
	}
	
	private boolean isJoystickActive(IGamepadJoystick joystick, JoystickDimension variable) {
		return getAdjustedJoystickPosition(joystick, variable) > 0;
	}
	
	private boolean isJoystickActive(String joystick, JoystickDimension variable) {
		return isJoystickActive(getJoystick(joystick), variable);
	}
	
	private boolean isJoystickActive(IGamepadJoystick joystick) {
		return isJoystickActive(joystick, JoystickDimension.X)
				|| isJoystickActive(joystick, JoystickDimension.Y);
	}
	
	private boolean isJoystickActive(String joystick) {
		return isJoystickActive(getJoystick(joystick));
	}
	
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
	
	public void registerLinear(String name, Object object, String field) {
		registerLinear(name, new LinearValueGetter(makeFieldGetter(object, field)));
	}
	
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
	
	public void registerJoystick(String name, Object object, String fieldX, String fieldY) {
		registerJoystick(name, new GamepadJoystickGetter(makeFieldGetter(object, fieldX),
														makeFieldGetter(object, fieldY)));
	}
	
	public void registerJoystick(String name, Object object, String base_field) {
		registerJoystick(name, object, base_field + "_x", base_field + "_y");
	}
	
	public void registerJoystickLinearAlias(String joystick, String alias, JoystickDimension variable) {
		linears.put(alias, new ILinearValue() {
			@Override
			public float getValue() {
				return getAdjustedJoystickPosition(joystick, variable);
			}
		});
	}
	
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
	
	private void updateLinears() {
		for(String linear : linears.keySet()) {
			if(isLinearActive(linear))
				for(ILinearAction action : linearValueActions.get(linear))
					action.act(getLinearValue(linear));
		}
	}
	
	private Map<String, List<ILinearAction>> getJoystickLinearActions(JoystickDimension variable)
	{
		if(variable == JoystickDimension.X) 
			return joystickXValueActions;
		return joystickYValueActions;
	}
	
	private Map<String, List<IButtonAction>> getJoystickLinearReleaseActions(JoystickDimension variable)
	{
		if(variable == JoystickDimension.X) 
			return joystickXReleaseActions;
		return joystickYReleaseActions;
	}
	
	private List<ILinearAction> getJoystickLinearActions(String joystick, JoystickDimension variable)
	{
		getJoystickLinearActions(variable).get(joystick);
		if(variable == JoystickDimension.X) 
			return joystickXValueActions.get(joystick);
		return joystickYValueActions.get(joystick);
	}
	
	private List<IButtonAction> getJoystickLinearReleaseActions(String joystick, JoystickDimension variable)
	{
		return getJoystickLinearReleaseActions(variable).get(joystick);
	}
	
	private void runLinearActions(String joystick, JoystickDimension variable) {
		for(ILinearAction action : getJoystickLinearActions(joystick, variable)) {
			action.act(getJoystickPosition(joystick, variable));
		}
	}
	
	private void runJoystickActions(String joystick) {
		for(IJoystickAction action : joystickValueActions.get(joystick)) {
			action.act(getJoystickPosition(joystick, JoystickDimension.X),
					getJoystickPosition(joystick, JoystickDimension.Y));
		}
	}
	
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
	
	/** Checks all buttons and runs their actions */
	public void update() {
		updateButtons();
		updateLinears();
		updateJoysticks();
	}
	
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
	
	public void registerListeners(Object object) {
		for(Method method : object.getClass().getMethods()) {
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
