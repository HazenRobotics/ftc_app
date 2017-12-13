package org.firstinspires.ftc.teamcode.output;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

/**
 * A message displayed in {@link Telemetry}.
 */
public class Message {
	private final String key;
	private final Supplier<?> message;
	
	/**
	 * @param key The message's key
	 * @param message The message's content
	 */
	public Message(String key, Supplier<?> message) {
		this.key = key;
		this.message = message;
	}
	
	/** @return The message's name. */
	public String getKey() {
		return key;
	}
	
	/** @return The message to display. */
	public String getMessage() {
		return message.get().toString();
	}
}
