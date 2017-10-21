package co.lijero.react;

import java.lang.reflect.Field;

class FieldReaction implements Reaction {
    private final Object object;
    private final Field field;
    private final String name;

    public FieldReaction(Object object, Field field, String name) {
        this.object = object;
        this.field = field;
        this.name = name;
    }

    public FieldReaction(Object object, Field field) {
        this.object = object;
        this.field = field;
        this.name = getDefaultName();
    }

    @Override
    public Object invoke(Object... inputs) {
        if (inputs.length != 0)
            throw new IllegalArgumentException(getName() + " does not have " + inputs.length + " arguments: is a field.");
        try {
            return field.get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDefaultName() {
        try {
            Reactive reactive = field.getAnnotation(Reactive.class);
            if(!reactive.name().isEmpty())
                return reactive.name();
            return field.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isTracker() {
        return true;
    }
}
