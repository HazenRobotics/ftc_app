package org.firstinspires.ftc.teamcode.input;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public interface EventListener<T extends ButtonEvent> {
    public void on(T event);

    // WARNING: we just check that the parameter is some sort of button event-- it could be a JoystickEvent when you're trying for a ButtonPair!
    // This is because of type erasure making it impossible to do so.
    public static<T extends ButtonEvent> EventListener<T> fromMethod(Object object, String method) {
        // Search for the method in the class
        for(Method potentialListener : object.getClass().getMethods()) {
            // Does the name match?
            if(!potentialListener.getName().equals(method))
                continue;
            // Can we access the method?
            // NOTE: isAccessible just means the method has been forced to be accessible, via reflection
            // it is NOT necessarily accessible if it's public!
            if(!potentialListener.isAccessible() && !Modifier.isPublic(potentialListener.getModifiers()))
                continue;
            // Does this method have a single parameter?
            if(potentialListener.getParameterTypes().length != 1)
                continue;
            // Is that parameter an event?
            if(!ButtonEvent.class.isAssignableFrom(potentialListener.getParameterTypes()[0]))
                continue;
            // Make sure the event listener can't throw an exception, causing a crash.
            if(potentialListener.getExceptionTypes().length != 0)
                throw new IllegalArgumentException("An event listener must not throw exceptions! " + method);
            return new EventListener<T>() {
                @Override
                public void on(ButtonEvent event) {
                    try {
                        // Call the listener
                        potentialListener.invoke(object, event);
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
