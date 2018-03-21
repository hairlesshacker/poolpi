package net.petecarter.pool.gpio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.w1.W1Master;


import com.pi4j.temperature.TemperatureScale;

public class GPIORealRpi implements GPIOInterface {

	private static final Logger LOG = LoggerFactory.getLogger(GPIORealRpi.class);

	private static  GpioController gpio;
	private static  GpioPinDigitalOutput[] pin = new GpioPinDigitalOutput[8];

	static {
		init();
	}

	private static void init() {
		try {
			gpio = GpioFactory.getInstance();

			for (int ii=0; ii<8; ii++) {
				pin[ii] = gpio.provisionDigitalOutputPin(RaspiPin.getPinByAddress(ii), "Relay"+ii, PinState.HIGH);
				pin[ii].setShutdownOptions(true, PinState.HIGH);
			}
		}
		catch (Throwable t) {
			LOG.error("cannot init",  t);
		}
	}

	@Override
	public boolean isRelayOn(int pinNumber) {
		return pin[pinNumber].isLow();
	}

	@Override
	public void setRelayOn(int pinNumber) {
		if (!isRelayOn(pinNumber)) {
			LOG.info("Turning Relay On: " + pinNumber);
			pin[pinNumber].setState(PinState.LOW);
		}
		else {
			LOG.debug ("Relay Already On, No Change: " +pinNumber);
		}
		
	}

	@Override
	public void setRelayOff(int pinNumber) {
		if (isRelayOn(pinNumber)) {
			LOG.info("Turning Relay Off: " + pinNumber);
			pin[pinNumber].setState(PinState.HIGH);
		}
		else {
			LOG.debug ("Relay already Off, No change: " +pinNumber);
		}
	}
	
	@Override
	public double getTemp() {
		W1Master w1Master = new W1Master();

        for (TemperatureSensor device : w1Master.getDevices(TemperatureSensor.class)) {
            //System.out.printf("%-20s %3.1f°C %3.1f°F\n", device.getName(), device.getTemperature(),
            //        device.getTemperature(TemperatureScale.FARENHEIT));
        	return device.getTemperature(TemperatureScale.FARENHEIT);
        }
        //0 if no device found?
        LOG.error("TemperatureSensor device not found");
        return 0d;
    }
}
