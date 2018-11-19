package ru.smartsarov.simplesnmp.job;

import com.google.gson.annotations.Expose;

public class DeviceRulesTable {
	@Expose
	private int id;
	private int device_id;
	@Expose
	private String work_t;
	@Expose
	private String weekend_t;
	private boolean removed;
	@Expose
	private int rule_type;
	public DeviceRulesTable(int id, int device_id, String work_t, String weekend_t, boolean removed, int rule_type) {
		this.setId(id);
		this.setDevice_id(device_id);
		this.setWork_t(work_t);
		this.setWeekend_t(weekend_t);
		this.setRemoved(removed);
		this.setRule_type(rule_type);
	}
	
	public DeviceRulesTable() {
		this.setId(0);
		this.setDevice_id(0);
		this.setWork_t("");
		this.setWeekend_t("");
		this.setRemoved(false);
		this.setRule_type(0);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDevice_id() {
		return device_id;
	}

	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}

	public String getWork_t() {
		return work_t;
	}

	public void setWork_t(String work_t) {
		this.work_t = work_t;
	}

	public String getWeekend_t() {
		return weekend_t;
	}

	public void setWeekend_t(String weekend_t) {
		this.weekend_t = weekend_t;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public int getRule_type() {
		return rule_type;
	}

	public void setRule_type(int rule_type) {
		this.rule_type = rule_type;
	}

}
