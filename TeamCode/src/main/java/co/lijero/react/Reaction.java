package co.lijero.react;

public interface Reaction {
    public Object invoke(Object... inputs);
    public String getName();
    public boolean isTracker();
}
