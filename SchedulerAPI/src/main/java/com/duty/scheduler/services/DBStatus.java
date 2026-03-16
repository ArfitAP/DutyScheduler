package com.duty.scheduler.services;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DBStatus {

	private final Set<String> busyKeys = ConcurrentHashMap.newKeySet();

	public boolean isBusy(Long roomId, String month) {
		return busyKeys.contains(roomId + "_" + month);
	}

	public void setBusy(Long roomId, String month, boolean busy) {
		String key = roomId + "_" + month;
		if (busy) {
			busyKeys.add(key);
		} else {
			busyKeys.remove(key);
		}
	}
}
