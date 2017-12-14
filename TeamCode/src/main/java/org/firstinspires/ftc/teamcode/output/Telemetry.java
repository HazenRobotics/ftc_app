package org.firstinspires.ftc.teamcode.output;

import org.firstinspires.ftc.teamcode.reflection.Supplier;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/** Handles displaying messages to the app ("telemetry"). */
public class Telemetry {
	private final org.firstinspires.ftc.robotcore.external.Telemetry internal;
	private final List<Message> messages =  new ArrayList<Message>();
	private final List<SimpleEntry<Message, Long>> expires = new ArrayList<SimpleEntry<Message, Long>>();
	
	/**
	 * Makes a new telemetry manager wrapping the Ftc telemetry.
	 * @param internal The actual display of this telemetry.
	 */
	public Telemetry(org.firstinspires.ftc.robotcore.external.Telemetry internal) {
		this.internal = internal;
	}
	
	/**
	 * Adds a persistent message to the telemetry
	 * @param message The message to display
	 * @return The message added to persistent telemetry
	 */
	public Message add(Message message) {
		messages.add(message);
		return message;
	}
	
	/**
	 * Adds a persistent message to the telemetry
	 * @param key The message's key
	 * @param message The content of the message
	 * @return The message added to persistent telemetry
	 */
	public Message add(String key, String message) {
		return add(new ConstantMessage(key, message));
	}
	
	/**
	 * Adds a persistent message to the telemetry
	 * @param key The message's key
	 * @param message The content of the message
	 * @return The message added to persistent telemetry
	 */
	public Message add(String key, Supplier<Object> message) {
		return add(new Message(key, message));
	}
	
	/**
	 * Adds a temporary message to the telemetry
	 * @param message The message to display
	 * @param expiry The time, in seconds, it will take to expire
	 */
	public void notify(Message message, double expiry) {
		add(message);
		expires.add(new SimpleEntry<>(message, System.currentTimeMillis() + (int)(expiry * 1000)));
	}

	/**
	 * Removes the given message from the persistent telemetry list
	 * @param message The message to remove
	 * @return If the given message was in the persistent message list to be removed
	 */
	public boolean remove(Message message) {
		return messages.remove(message);
	}
	
	/**
	 * Adds a temporary message to the telemetry
	 * @param key The message's key
	 * @param message The content of the message
	 * @param expiry The time, in seconds, it will take to expire
	 */
	public void notify(String key, String message, double expiry) {
		notify(new ConstantMessage(key, message), expiry);
	}
	
	/**
	 * Adds a temporary message to the telemetry
	 * @param key The message's key
	 * @param message The content of the message
	 * @param expiry The time, in seconds, it will take to expire
	 */
	public void notify(String key, Supplier<String> message, double expiry) {
		notify(new Message(key, message), expiry);
	}
	
	private void removeExpiredMessages() {
		for(SimpleEntry<Message, Long> expiry : expires) {
			if(expiry.getValue() > System.currentTimeMillis())
				break;
			messages.remove(expiry.getKey());
			expires.remove(expiry);
		}
	}
	
	private void display() {
		for(Message message : messages) {
			internal.addData(message.getKey(), message.getMessage());
		}
		internal.update();
	}
	
	/** Display the messages to the telemetry */
	public void update() {
		removeExpiredMessages();
		display();
	}
}
