package co.lijero.react;

import java.lang.reflect.Method;

class MethodReaction implements Reaction {
    protected final Object object;
    protected final Method method;
    protected final String name;

    public MethodReaction(Object object, Method method) {
        this.object = object;
        this.method = method;
        this.name = getDefaultName();
    }

    public MethodReaction(Object object, Method method, String name) {
        this.object = object;
        this.method = method;
        this.name = name;
    }

    @Override
    public Object invoke(Object... inputs) {
        try {
            return method.invoke(object, inputs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    protected String getDefaultName() {
        try {
            Reactive reactive = method.getAnnotation(Reactive.class);
            if(!reactive.name().isEmpty())
                return reactive.name();
            return method.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isTracker() {
        return method.getParameterTypes().length == 0;
    }
}
