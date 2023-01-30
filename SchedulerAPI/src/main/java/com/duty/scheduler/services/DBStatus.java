package com.duty.scheduler.services;

public class DBStatus {
	
    private boolean busy = false;
    
	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}  
    
}
