package org.firstinspires.ftc.teamcode.output;

/**
 * A message whose content doesn't change
 */
public class ConstantMessage extends Message {
	/**
	 * @param key The message's key
	 * @param content The content of this message
	 */
	public ConstantMessage(String key, String content) {
		super(key, () -> { return content; });
	}
}
