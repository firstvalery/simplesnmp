package ru.smartsarov.simplesnmp.job;

import com.google.gson.annotations.Expose;

public class DeviceTable {
	private int id;
	@Expose
	private String community;
	@Expose
	private String name;
	@Expose
	private String type;

	private boolean removed;
	@Expose
	private String ip;

	public DeviceTable(int id, String community, String name,String type, boolean removed, String ip) {
		this.id = id;
		this.community = community;
		this.name = name;
		this.type = type;
		this.removed = removed;
		this.ip = ip;
	}
	
	public DeviceTable() {
		this.id = 0;
		this.community = "";
		this.name = "";
		this.type = "";
		this.removed = false;
		this.ip = "";
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public boolean isRemoved() {
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
