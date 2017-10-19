package org.firstinspires.ftc.teamcode.output;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/** Handles displaying messages to the app ("telemetry"). */
public class Telemetry {
	private final com.qualcomm.robotcore.robocol.Telemetry internal;
	private final List<Message> messages = new ArrayList<>();
	private final List<SimpleEntry<Message, Long>> expiries = new ArrayList<>();
	
	/**
	 * Makes a new telemetry manager wrapping the Ftc telemetry.
	 * @param internal The actual display of this telemetry.
	 */
	public Telemetry(com.qualcomm.robotcore.robocol.Telemetry internal) {
		this.internal = internal;
	}
	
	/**
	 * Adds a persistent message to the telemetry
	 * @param message The message to display
	 */
	public void add(Message message) {
		messages.add(message);
	}
	
	/**
	 * Adds a persistent message to the telemetry
	 * @param key The message's key
	 * @param message The content of the message
	 */
	public void add(String key, String message) {
		add(new ConstantMessage(key, message));
	}
	
	/**
	 * Adds a persistent message to the telemetry
	 * @param key The message's key
	 * @param message The content of the message
	 */
	public void add(String key, Message.IMessageData message) {
		add(new Message(key, message));
	}
	
	/**
	 * Adds a temporary message to the telemetry
	 * @param message The message to display
	 * @param expiry The time, in seconds, it will take to expire
	 */
	public void notify(Message message, double expiry) {
		add(message);
		expiries.add(new SimpleEntry<>(message, System.currentTimeMillis() + (int)(expiry * 1000)));
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
	public void notify(String key, Message.IMessageData message, double expiry) {
		notify(new Message(key, message), expiry);
	}
	
	private void removeExpiredMessages() {
		for(SimpleEntry<Message, Long> expiry : expiries) {
			if(expiry.getValue() > System.currentTimeMillis())
				break;
			messages.remove(expiry.getKey());
			expiries.remove(expiry);
		}
	}
	
	private void display() {
		for(Message message : messages) {
			internal.addData(message.getKey(), message.getMessage());
		}
	}
	
	/** Display the messages to the telemetry */
	public void update() {
		removeExpiredMessages();
		display();
	} 
}
