package net.petecarter.pool.controls;

import java.time.LocalTime;


public class PumpTimer {
	private PoolMode mode;
	private String startTime;
	private String stopTime;
	
	public PumpTimer ( PoolMode mode, String start, String stop) {
		this.mode = mode;
		this.startTime = start;
		this.stopTime = stop;
	}
	public PoolMode getMode() {
		return mode;
	}
	public void setMode(PoolMode mode) {
		this.mode = mode;
	}
	public String getStartTimeString() {
		return startTime;
	}
	public void setStartTimeString(String startTimeString) {
		this.startTime = startTimeString;
	}
	public String getStopTimeString() {
		return stopTime;
	}
	public LocalTime getStartTime() {
		return LocalTime.parse(startTime);
	}
	public LocalTime getStopTime() {
		return LocalTime.parse(stopTime);
	}
	public void setStopTimeString(String stopTime) {
		this.stopTime = stopTime;
	}
}
