package ru.smartsarov.simplesnmp.job;

import com.google.gson.annotations.Expose;

public class JobTable {
	@Expose
	private int id;
	@Expose
	private long job_ts;
	@Expose
	private String user_name;
	@Expose
	private long set_ts;
	@Expose
	private boolean done;
	@Expose
	private int command;
	@Expose
	private String name;
	
	private String community;
	private String ip;
	private boolean removed;
	@Expose
	private boolean sunny;
	
	public JobTable(int id, long job_ts, String user, long set_ts, boolean done, int command, String name, String ip,
			boolean removed, String community, boolean sunny) {
		this.id = id;
		this.job_ts = job_ts;
		this.user_name = user;
		this.set_ts = set_ts;
		this.done = done;
		this.command = command;
		this.name = name;
		this.ip = ip;
		this.removed = removed;
		this.setCommunity(community);
		this.sunny = sunny;
	}
	
	public JobTable() {
		this.id = 0;
		this.job_ts = 0;
		this.user_name = "";
		this.set_ts = 0;
		this.done = false;
		this.command = 0;
		this.name = "";
		this.ip = "";
		this.removed = false;
		this.setCommunity("");
		this.sunny = false;
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public long getJob_ts() {
		return job_ts;
	}
	public void setJob_ts(long job_ts) {
		this.job_ts = job_ts;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public long getSet_ts() {
		return set_ts;
	}
	public void setSet_ts(long set_ts) {
		this.set_ts = set_ts;
	}
	public boolean isDone() {
		return done;
	}
	public void setDone(boolean done) {
		this.done = done;
	}
	public int getCommand() {
		return command;
	}
	public void setCommand(int command) {
		this.command = command;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public boolean isRemoved() {
		return removed;
	}
	public void setRemoved(boolean removed) {
		this.removed = removed;
	}

	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	public boolean isSunny() {
		return sunny;
	}

	public void setSunny(boolean sunny) {
		this.sunny = sunny;
	}

	
}
