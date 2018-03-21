package net.petecarter.pool.gpio;

public interface GPIOInterface {

	double getTemp();
	boolean isRelayOn(int pinNumber);
	void setRelayOn(int pinNumber);
	void setRelayOff(int pinNumber);
}