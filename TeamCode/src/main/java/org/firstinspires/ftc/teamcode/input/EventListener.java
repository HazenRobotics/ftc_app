package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class EventListener<T extends IButton> {
    public abstract void on(EventType type, T button);

    // WARNING: we just check that the parameter is some sort of button event-- it could be a JoystickEvent when you're trying for a ButtonPair!
    // This is because of type erasure making it impossible to do so.
    public static EventListener fromMethod(final Object object, String method) {
        // Search for the method in the class
        for(final Method potentialListener : object.getClass().getMethods()) {
            // Does the name match?
            if(!potentialListener.getName().equals(method))
                continue;
            // Can we access the method?
            // NOTE: isAccessible just means the method has been forced to be accessible, via reflection
            // it is NOT necessarily accessible if it's public!
            if(!potentialListener.isAccessible() && !Modifier.isPublic(potentialListener.getModifiers()))
                continue;
            // Does this method have the correct number of parameters.
            if(potentialListener.getParameterTypes().length != 2)
                continue;
            // Make sure the parameters have the appropriate types.
            if(!EventType.class.isAssignableFrom(potentialListener.getParameterTypes()[0]))
                continue;
            if(!IButton.class.isAssignableFrom(potentialListener.getParameterTypes()[1]))
                continue;
            // Make sure the event listener can't throw an exception, causing a crash.
            if(potentialListener.getExceptionTypes().length != 0)
                throw new IllegalArgumentException("An event listener must not throw exceptions! " + method);
            return new EventListener() {
                @Override
                public void on(EventType type, IButton button) {
                    try {
                        // Call the listener
                        potentialListener.invoke(object, type, button);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        // This should be impossible because we checked if the listener is accessible and made sure it had no exceptions
                        throw new RuntimeException(e);
                    }
                }
            };
        }
        throw new IllegalArgumentException("Failed to find method named " + method + " with appropriate parameters in " + object.getClass().getSimpleName());
    }
}
