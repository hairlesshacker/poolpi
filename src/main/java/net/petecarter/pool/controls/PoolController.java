package net.petecarter.pool.controls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.petecarter.pool.controls.PumpController.PumpState;
import net.petecarter.pool.gpio.GPIOInterface;

@Component
public class PoolController {
	private static final int HEAT_PIN = 0;
	private static final int VALVE_PIN = 1;
	private static final int BUBBLES_PIN = 3;
	private static final int SPENCER_PIN = 2;
	private GPIOInterface gpio;
	private PumpController pumpController;
	
			
	@Autowired
	PoolController (GPIOInterface gpio, PumpController pumpController ) {
		this.gpio = gpio;
		this.pumpController = pumpController;
	}

	public void changePumpState(PumpState state) {
		pumpController.changeState(state);
	}
	public PumpState getPumpState() {
		return pumpController.getState();
	}
	public double getTemp() {
		return gpio.getTemp();
	}
	public boolean isBubblesOn() {
		return gpio.isRelayOn(BUBBLES_PIN);
	}
	public boolean isHeatOn() {
		return gpio.isRelayOn(HEAT_PIN); 
	}
	public boolean isSpaValveOn() {
		return gpio.isRelayOn(VALVE_PIN); 
	}
	public boolean isSpencerOn() {
		return gpio.isRelayOn(SPENCER_PIN);
	}
	public void setBubblesOff() {
		gpio.setRelayOff(BUBBLES_PIN);
	}
	public void setBubblesOn() {
		gpio.setRelayOn(BUBBLES_PIN);
	}
	public void setHeatOff() {
		gpio.setRelayOff(HEAT_PIN);	
	}
	public void setHeatOn() {
		gpio.setRelayOn(HEAT_PIN);	
	}
	public void setSpaValveOff() {
		gpio.setRelayOff(VALVE_PIN);	
	}
	public void setSpaValveOn() {
		gpio.setRelayOn(VALVE_PIN);	
	}
	public void setSpencerOff() {
		gpio.setRelayOff(SPENCER_PIN);
	}
	public void setSpencerOn() {
		gpio.setRelayOn(SPENCER_PIN);
	}
	public void setMode(PoolMode mode) {
		changePumpState(mode.getPumpState());
		setBubbles (mode.isBubblesOn());
		setHeat (mode.isHeatOn());
		setSpaValve(mode.isSpaValveOn());
		setSpencer(mode.isSpencerOn());
	}	
	private void setBubbles (boolean isOn) {
		if (isOn) {
			setBubblesOn();
		}
		else {
			setBubblesOff();
		}
	}
	private void setHeat (boolean isOn) {
		if (isOn) {
			setHeatOn();
		}
		else {
			setHeatOff();
		}
	}
	private void setSpaValve (boolean isOn) {
		if (isOn) {
			setSpaValveOn();
		}
		else {
			setSpaValveOff();
		}
	}
	private void setSpencer (boolean isOn) {
		if (isOn) {
			setSpencerOn();
		}
		else {
			setSpencerOff();
		}
	}
}
