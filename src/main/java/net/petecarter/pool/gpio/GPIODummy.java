package net.petecarter.pool.gpio;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GPIODummy implements GPIOInterface {

	private static Logger LOG = LoggerFactory.getLogger(GPIODummy.class);
	private static boolean pin[] = new boolean[8];


	@Override
	public double getTemp() {
		double start = 50;
		double end = 102;
		double random = new Random().nextDouble();
		return start + (random * (end - start));
	}

	@Override
	public boolean isRelayOn(int pinNumber) {
		return pin[pinNumber];
	}

	@Override
	public void setRelayOn(int pinNumber) {
		if (!isRelayOn(pinNumber)) {
			LOG.info("Turning Relay On: " + pinNumber);
			pin[pinNumber] = true;
		}
		else {
			LOG.debug ("Relay Already On, No Change: " +pinNumber);
		}
	}

	@Override
	public void setRelayOff(int pinNumber) {
		if (isRelayOn(pinNumber)) {
			LOG.info("Turning Relay Off: " + pinNumber);
			pin[pinNumber] = false;
		}
		else {
			LOG.debug ("Relay already Off, No change: " +pinNumber);
		}
		pin[pinNumber] = false;
	}

}
