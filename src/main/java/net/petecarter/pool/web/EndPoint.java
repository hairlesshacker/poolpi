package net.petecarter.pool.web;

import net.petecarter.pool.controls.PoolController;
import net.petecarter.pool.controls.PoolMode;
import net.petecarter.pool.controls.PoolScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class EndPoint {
	private static final Logger LOGGER = LogManager.getLogger(EndPoint.class);

	PoolController poolControl;
	PoolScheduler poolSchedule;
	Map<String,String> messageMap;

	@Autowired
	EndPoint (PoolController poolControl, PoolScheduler poolSchedule) {
		this.poolControl = poolControl;
		this.poolSchedule = poolSchedule;
		initMessageMap();
	}

	@RequestMapping(value="/", method=RequestMethod.GET)
	public String index(@RequestParam(value="msg", required=false) String msgKey, Map<String,Object> model) {

		LOGGER.debug("request /");
		model.put("message", messageMap.get(msgKey));       


		//Get Temp
		String temp = String.format("%3.1f", poolControl.getTemp());
		boolean isTubOn = poolControl.isSpaValveOn();
		
		model.put("temp", temp);
		model.put("startstop", isTubOn ? "Stop" : "Start");
		model.put("bubblestate", isTubOn ? "button" : "hidden");
		model.put("bubbleson", poolControl.isBubblesOn() ? "Off" : "On");
		model.put("state", poolSchedule.getLastModeSet().getDisplayText());
		return "pool";
	}

	@RequestMapping(value="/handler",method=RequestMethod.POST)
	public RedirectView handler(@RequestParam(value="action", required=false) String action, Map<String,Object> model) {
		//If start, do it...
		String msgCode=null;
		if ("Start".equalsIgnoreCase(action)) {
			poolSchedule.enableOverrideMode(PoolMode.HOT_TUB);
			msgCode="H1";
		}
		if ("Stop".equalsIgnoreCase(action)) {
			poolSchedule.resetToScheduledMode();
			msgCode="H0";
		}
		if ("bubbles".equalsIgnoreCase(action)) {
			if (poolControl.isSpaValveOn() && !poolControl.isBubblesOn()) {
				poolControl.setBubblesOn();
				msgCode="B1";
			}
			else {
				poolControl.setBubblesOff();
				msgCode="B0";
			}
		}
		String target = (msgCode==null) ? ("/") : ("/?msg=" + msgCode);
		return new RedirectView(target, true);
	}

	@RequestMapping(value="/status",method=RequestMethod.GET)
	public String status(Map<String,Object> model) {
		LOGGER.debug("request /status");

		model.put("pump", poolControl.getPumpState());
		model.put("valve", poolControl.isSpaValveOn() ? "Spa" : "Pool");
		model.put("heat", poolControl.isHeatOn() ? "On" : "Off");
		model.put("temp", poolControl.getTemp());
		model.put("spencer", poolControl.isSpencerOn() ? "On" : "Off");
		model.put("bubbles", poolControl.isBubblesOn() ? "On" : "Off");

		return "status";
	}

	@RequestMapping("/sseTemp")
	public ResponseBodyEmitter tempEmitter () {

		final SseEmitter emitter = new SseEmitter();

		ScheduledExecutorService tempEmitThread = Executors.newScheduledThreadPool(1);

		emitter.onCompletion(() -> {
			tempEmitThread.shutdown();
		});
		emitter.onTimeout(() -> {
			LOGGER.debug("timeout");
		});

		tempEmitThread.scheduleAtFixedRate(() -> {
			try {
				emitter.send(String.format("%3.1f", poolControl.getTemp()) , MediaType.TEXT_PLAIN);
			} catch (Exception e) {
				LOGGER.warn("Emitter Error", e);
				//emitter.completeWithError(e);
				emitter.complete();
				return;
			} 

		}, 0, 10, TimeUnit.SECONDS);
		return emitter;
	}

	private void initMessageMap() {
		messageMap = new HashMap<String,String>();
		messageMap.put("H1", "Hot Tub has been turned on.");
		messageMap.put("H0", "HotTub turned off.");
		messageMap.put("B1", "Bubble bubble fizz fizz...");
		messageMap.put("B0", "No more bubble for you.");
	}

}