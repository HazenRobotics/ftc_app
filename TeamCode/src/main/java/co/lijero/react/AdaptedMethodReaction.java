package co.lijero.react;

import java.lang.reflect.Method;

class AdaptedMethodReaction extends MethodReaction {
    public AdaptedMethodReaction(Object object, Method method) {
        super(object, method);
    }

    public AdaptedMethodReaction(Object object, Method method, String name) {
        super(object, method, name);
    }

    @Override
    public String getDefaultName() {
        return method.getName();
    }
}
