package ru.smartsarov.simplesnmp.job;

public class DeviceDbBean {
	private int id;
	private int device_id;
	private String ip;
	private String community;
	private String desc;

	public DeviceDbBean(int id, int device_id, String ip,String community, String desc) {
		this.id = id;
		this.device_id = device_id;
		this.ip = ip;
		this.community = community;
		this.desc = desc;
	}
	public DeviceDbBean() {
		id = 0;
		device_id = 0;
		ip = "";
		community = "";
		desc = "";
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
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	
}
