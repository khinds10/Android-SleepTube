package com.kevinhinds.sleeptube;

/**
 * the list of Channel mapping to use in the TV Sounds application
 * 
 * @author khinds
 */
public class Channel {

	public int number;
	public int sound;
	public int image;
	public String channelName;

	/**
	 * construct Channel
	 */
	public Channel(int number, int sound, int image, String channelName) {
		this.number = number;
		this.sound = sound;
		this.image = image;
		this.channelName = channelName;
	}
}