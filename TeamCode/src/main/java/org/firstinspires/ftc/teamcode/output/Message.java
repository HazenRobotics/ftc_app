package org.firstinspires.ftc.teamcode.output;

/**
 * A message displayed in {@link Telemetry}.
 */
public class Message {
	/** The content of a {@link Message}. */
	@FunctionalInterface
	public static interface IMessageData {
		/** @return The message to display. */
		public String getMessage();
	}
	
	private final String key;
	private final IMessageData message;
	
	/**
	 * @param key The message's key
	 * @param message The message's content
	 */
	public Message(String key, IMessageData message) {
		this.key = key;
		this.message = message;
	}
	
	/** @return The message's name. */
	public String getKey() {
		return key;
	}
	
	/** @return The message to display. */
	public String getMessage() {
		return message.getMessage();
	}
}
