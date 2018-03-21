package net.petecarter.pool.controls;

import net.petecarter.pool.controls.PumpController.PumpState;

public enum PoolMode {
	
	OFF ("Pool Off", PumpState.OFF, false, false, false, false),
	POOL_LOW ("Pool Low", PumpState.LOW, false, false, false, false), 
	POOL_MEDIUM ("Pool Medium", PumpState.MEDIUM, false, false, false, false), 
	POOL_HIGH ("Pool High", PumpState.HIGH, false, false, false, false), 
	POOL_CLEAN ("Spencer Cleaning", PumpState.HIGH, false, false, true, false), 
	HOT_TUB ("HotTub", PumpState.HIGH, true, true, false, false);
	
	private String displayText;
	private PumpState pumpState;
	private boolean spaValveOn;
	private boolean heatOn;
	private boolean spencerOn;
	private boolean bubblesOn;
	
	PoolMode (String displayText, PumpState pumpState, boolean spaValveOn, boolean heatOn, boolean spencerOn, boolean bubblesOn) {
		this.displayText = displayText;
		this.pumpState = pumpState;
		this.spaValveOn = spaValveOn;
		this.heatOn = heatOn;
		this.spencerOn = spencerOn;
		this.bubblesOn = bubblesOn;
	}
	
	public PumpState getPumpState() {
		return pumpState;
	}
	
	public boolean isSpaValveOn() {
		return spaValveOn;
	}

	public boolean isHeatOn() {
		return heatOn;
	}

	public boolean isSpencerOn() {
		return spencerOn;
	}

	public boolean isBubblesOn() {
		return bubblesOn;
	}

	public String getDisplayText() {
		return displayText;
	}
}
