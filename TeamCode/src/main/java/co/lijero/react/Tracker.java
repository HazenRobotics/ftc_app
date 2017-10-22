package co.lijero.react;

/**
 * Tracks variables within the reactive system
 */
public interface Tracker {
	/**
	 * Does stuff when its dependencies change.
	 * @param dependencies The stuff that this tracks.
	 */
	public void update(Object... dependencies);
}
