package ru.smartsarov.simplesnmp.job;

public class DeviceTable {
	private int id;
	private String community;
	private String name;
	private boolean removed;
	private String ip;

	public DeviceTable(int id, String community, String name, boolean removed, String ip) {
		this.id = id;
		this.community = community;
		this.name = name;
		this.removed = removed;
		this.ip = ip;
	}
	
	public DeviceTable() {
		this.id = 0;
		this.community = "";
		this.name = "";
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
	
}
