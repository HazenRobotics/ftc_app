package co.lijero.react;

/** A wrapper for the invocation of an arbitrary method. */
interface Invokable {
	/**
	 * @param args The dependencies of this method call
	 * @return The new result of this method call
	 */
	public Object invoke(Object... args);
}
