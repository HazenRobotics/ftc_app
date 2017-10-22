package co.lijero.react;

/**
 * A variable in the reactive system
 */
interface Reaction extends Invokable {
    /** @return The name of this reaction */
    public String getName();
    
    /** @return What variables does this reaction depend on? */
    public String[] getDependencies();
}
