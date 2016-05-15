package com.sound.oil;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * oil data. 
 */
public class OilData {
	/**
	 * time
	 */
	public Date time;

	/**
	 * the left oil height in oil box
	 */
	public double h;
	
	
	public OilData(Date time, double h) {
		this.time = new Date(time.getTime());
		this.h = h;
	}
	
	public OilData(long time, double h) {
		this.time = new Date(time);
		this.h = h;
	}
	
	// only show hour:minute
	private static SimpleDateFormat s = new SimpleDateFormat("HH:mm");
	
	public String toString() {
		String ret = String.format("%s      %.3f", s.format(time), h);
		return ret;
	}
	
	// getter and setter
	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
}
