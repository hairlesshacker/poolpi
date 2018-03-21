package net.petecarter.pool.controls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.petecarter.pool.gpio.GPIOInterface;

@Component
public class PumpController {

	private GPIOInterface gpio;
	private static final int RELAY_A_PIN = 6;
	private static final int RELAY_B_PIN = 7;
		
	enum PumpState {
		OFF (0),
		LOW (1),
		MEDIUM (2),
		HIGH (3);
		private final int value;
		PumpState (int value) {
			this.value = value;
		}
		public int getValue() {
			return value;
		}
	}
	
	@Autowired
	PumpController (GPIOInterface gpio) {
		this.gpio = gpio;
	}

	public PumpState getState() {
		return readGPIOState();
	}
	
	public void changeState (PumpState newState) {
		setGPIOState(newState);
	}
	
	private PumpState readGPIOState() {
		boolean relayA = gpio.isRelayOn(RELAY_A_PIN);
		boolean relayB = gpio.isRelayOn(RELAY_B_PIN);
		
		if (relayA && relayB) {
			return PumpState.HIGH;
		}
		if (!relayA && relayB) {
			return PumpState.MEDIUM;
		}
		if (relayA && !relayB) {
			return PumpState.LOW;
		}
		if (!relayA && !relayB) {
			return PumpState.OFF;
		}
		//this can't happen?
		System.out.println("This wasn't supposed to happen, but it did.");
		return null;
	}
	private void setGPIOState(PumpState newState) {
		if (newState == PumpState.HIGH) {
			gpio.setRelayOn(RELAY_A_PIN);
			gpio.setRelayOn(RELAY_B_PIN);
		}
		if (newState == PumpState.MEDIUM) {
			gpio.setRelayOff(RELAY_A_PIN);
			gpio.setRelayOn(RELAY_B_PIN);
		}
		if (newState == PumpState.LOW) {
			gpio.setRelayOn(RELAY_A_PIN);
			gpio.setRelayOff(RELAY_B_PIN);
		}
		if (newState == PumpState.OFF) {
			gpio.setRelayOff(RELAY_A_PIN);
			gpio.setRelayOff(RELAY_B_PIN);
		}
	}
	

}
