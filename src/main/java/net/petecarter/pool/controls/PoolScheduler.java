package net.petecarter.pool.controls;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PoolScheduler {
	private static final Logger LOG = LoggerFactory.getLogger(PoolScheduler.class);
	private static final int OVERRIDE_MINUTES=180;
	
	private List<PumpTimer> schedule = new ArrayList<PumpTimer>();
	private PoolController poolControl;
	
	private long overrideTime = 0L;
	private PoolMode lastModeSet;
	
	@Autowired
	public PoolScheduler (PoolController poolController, List<PumpTimer> schedule) {
		this.poolControl = poolController;
		this.schedule = schedule;
		
		//add default as lowest priority to end of schedule; if nothing else, then OFF
		schedule.add(new PumpTimer(PoolMode.OFF,"00:00:00","23:59:59"));
		
		checkModeSchedule();
	}

	/**************
	 * Cron Be Like:
	 *    ┌──────────── second
	 *    │ ┌───────────── minute (0 - 59)
	 *    │ │ ┌───────────── hour (0 - 23)
	 *    │ │ │ ┌───────────── day of month (1 - 31)
	 *    │ │ │ │ ┌───────────── month (1 - 12)
	 *    │ │ │ │ │ ┌───────────── day of week (0 - 6) (Sunday to Saturday)
	 *    │ │ │ │ │ │                   
	 *   "* * * * * *"
	 **************/
	
	//every 15 minutes, at the 01 second.
	@Scheduled (cron = "01 00,15,30,45 * * * *")
	public void checkModeSchedule () {
		if (!isOverride()) {
			setModeBySchedule();
		}
		else
		{
			LOG.info("Ignoring schedule due to mode override until " + new Date(overrideTime));
		}
	}
	
	/**
	 * Set a pool mode to override the schedule, default override time
	 * @param mode
	 */
	public void enableOverrideMode(PoolMode mode) {
		enableOverrideMode(mode, OVERRIDE_MINUTES);
	}
	
	/**
	 * Sets a pool mode to override the schedule, with explicit override time
	 * @param mode Pool Mode to Enable
	 * @param overrideMinutes Minutes to override schedule
	 */
	public void enableOverrideMode(PoolMode mode, int overrideMinutes) {
		LOG.info("Schedule Override, set Mode to: " + mode);
		startOverride(overrideMinutes);
		poolControl.setMode(mode);
		lastModeSet = mode;
	}
	
	/**
	 * @return The last mode set using this scheduler.  May not reflect current state of system.
	 */
	public PoolMode getLastModeSet() {
		return lastModeSet;
	}

	/**
	 * @return
	 */
	public List<PumpTimer> getSchedule() {
		return schedule;
	}
	
	/**
	 * Resets any override, and returns to scheduled mode
	 * @return the new pool mode
	 */
	public PoolMode resetToScheduledMode() {
		resetOverride();
		return setModeBySchedule();
	}
	
	/**
	 * @param schedule
	 */
	public void setSchedule(List<PumpTimer> schedule) {
		this.schedule = schedule;
	}
	
	
	private synchronized boolean isOverride() {
			return (overrideTime > 0) && (System.currentTimeMillis() < overrideTime);
	}
	
	private boolean isTimeInRange (LocalTime start, LocalTime stop) {
		LocalTime currentTime = LocalTime.now();
		return (currentTime.isAfter(start) && currentTime.isBefore(stop)); 
	}
	
	private synchronized void resetOverride() {
		overrideTime = 0;
	}
	
	private PoolMode setModeBySchedule()	{
		for (PumpTimer timer : schedule) {
			if ( isTimeInRange(timer.getStartTime(),timer.getStopTime())) {
				
				LOG.info("Setting Pool Mode Per Timer:" + timer.getMode() + " " + 
			             timer.getStartTimeString() + "-" + timer.getStopTimeString());
				poolControl.setMode(timer.getMode());
				lastModeSet = timer.getMode();
				//First timer in List which matches range takes priority! (others ignored)
				return timer.getMode();
			}
		}
		//If no mode found, something's wrong.
		LOG.error ("No Pool Mode Found for current Time!");
		poolControl.setMode(PoolMode.OFF);
		return PoolMode.OFF;
	}
			
	private synchronized void startOverride(int minutes) {
		overrideTime = System.currentTimeMillis() + (minutes * 1000 * 60);
	}
}
