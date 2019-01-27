package ru.smartsarov.simplesnmp.job;

public class LogTable {
	private int id;
	private long ts;
	private String device;
	private String text;
	private boolean removed;
	
	public LogTable(int id, long ts, String device, String text, boolean removed) {
		this.id = id;
		this.ts = ts;
		this.device = device;
		this.text = text;
		this.removed = removed;
	}
	
	public LogTable() {
		this.id = 0;
		this.ts = 0;
		this.device = "";
		this.text = "";
		this.removed = false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getTs() {
		return ts;
	}

	public void setTs(long ts) {
		this.ts = ts;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	
	

}
