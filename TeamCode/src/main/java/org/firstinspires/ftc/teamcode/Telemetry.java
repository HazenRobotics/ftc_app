package org.firstinspires.ftc.teamcode;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import co.lijero.react.ReactionManager;
import co.lijero.react.Tracker;

/**
 * The Telemetry consumes reactive values and displays them to the phone app.
 */
public class Telemetry {
	/** The actual FTC telemetry to display to. */
	private final org.firstinspires.ftc.teamcode.Telemetry display;
	/** The system we're tracking variables from. */
	private final ReactionManager reactions;
	/** The constant strings that we're currently displaying: telemetry key, value */
	private final Map<String, String> displayedValues = new HashMap<>();
	/** The expirable values and their expiry times: variable, expiry time */
	private final Map<String, Long> expiries = new HashMap<>();
	
	/**
	 * @param display The FTC telemetry to display to.
	 * @param reactiveVariables The variables we're tracking.
	 */
	public Telemetry(org.firstinspires.ftc.teamcode.Telemetry display, ReactionManager reactions) {
		this.display = display;
		this.reactions = reactions;
	}
	
	/** Display a reactive variable in Telemetry. */
	public void track(String key, String variable) {
		// Track the variable in the reactions system to update its value here.
		reactions.make().name("__telemetry_" + key).run(new Tracker() {
			@Override
			public void update(Object... dependencies) {
				displayedValues.put(key, dependencies[0].toString());
			}
		}, variable).finish();
	}
	
	/**
	 * Display a constant in Telemetry for a limited period of time.
	 * 
	 * @param key The telemetry display key
	 * @param value The thing to display
	 * @param expiry The time, in seconds, that this message will last.
	 */
	public void notify(String key, String value, float expiry) {
		displayedValues.put(key, value);
		expiries.put(key, System.currentTimeMillis() + (long)(expiry * 1000));
	}
	
	private void removeExpiredValues() {
		for(Entry<String, Long> expirable : expiries.entrySet()) {
			// expirable.getValue() is the time at which this entry will expire,
			// so expire if we've passed that time
			if(System.currentTimeMillis() > expirable.getValue()) {
				expiries.remove(expirable.getKey());
				displayedValues.remove(expirable.getKey());
			}
		}
	}
	
	private void display() {
		for(Entry<String, String> displayedValue : displayedValues.entrySet())
			display.addData(displayedValue.getKey(), displayedValue.getValue());
	}
	
	/** Displays the updated values to the app. */
	public void update() {
		removeExpiredValues();
		display();
	}
}
