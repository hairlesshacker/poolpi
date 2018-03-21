package net.petecarter.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


import net.petecarter.pool.controls.PoolMode;
import net.petecarter.pool.controls.PumpTimer;
import net.petecarter.pool.gpio.GPIODummy;
import net.petecarter.pool.gpio.GPIOInterface;
import net.petecarter.pool.gpio.GPIORealRpi;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableScheduling
public class Config {
	
	private static Logger LOG = LoggerFactory.getLogger(Config.class);

	@Bean
	List<PumpTimer> schedule() {
		List<PumpTimer> schedule = new ArrayList<PumpTimer>();
		schedule.add(new PumpTimer(PoolMode.POOL_LOW,"07:00:00", "11:00:00"));
		schedule.add(new PumpTimer(PoolMode.POOL_MEDIUM,"11:00:00", "15:00:00"));
		schedule.add(new PumpTimer(PoolMode.POOL_CLEAN,"15:00:00", "20:00:00"));
		schedule.add(new PumpTimer(PoolMode.POOL_LOW,"20:00:00", "23:00:00"));
		return schedule;
	}
		
	@Bean
	GPIOInterface gPIOInterface() {
		String osArch = System.getProperty("os.arch");
		LOG.info("Detected: " + osArch );

		// Assume "arm" means it's actual pi.
		if (osArch.equalsIgnoreCase("arm")) {
			LOG.info("Using Real GPIO");
			return new GPIORealRpi();	
		}
		// otherwise, get the dummy
		LOG.info("Using Dummy GPIO");
		return new GPIODummy();
	}
	
	@Bean 
	TaskScheduler taskScheduler() {
		return new ThreadPoolTaskScheduler();
	}
}
