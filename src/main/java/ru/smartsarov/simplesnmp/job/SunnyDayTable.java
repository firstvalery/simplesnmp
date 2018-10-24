package ru.smartsarov.simplesnmp.job;

public class SunnyDayTable {
	private int day;
	private String off_ts;
	private String on_ts;
	
	public SunnyDayTable() {
		this.day = 0;
		this.off_ts = "";
		this.on_ts = "";
	}
	public SunnyDayTable(int day, String off_ts, String on_ts) {
		this.day = day;
		this.off_ts = off_ts;
		this.on_ts = on_ts;
	}
	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public String getOff_ts() {
		return off_ts;
	}
	public void setOff_ts(String off_ts) {
		this.off_ts = off_ts;
	}
	public String getOn_ts() {
		return on_ts;
	}
	public void setOn_ts(String on_ts) {
		this.on_ts = on_ts;
	}
}
