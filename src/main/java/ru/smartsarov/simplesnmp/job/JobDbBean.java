package ru.smartsarov.simplesnmp.job;

public class JobDbBean {
	private int id;
	private long job_ts;
	private String command;
	private String user;
	private int device_id;
	private long set_ts;
	private boolean done;
	private boolean remove;

	public JobDbBean(int id, long job_ts, String command, String user, int device_id, long set_ts, boolean done,
			boolean remove) {
		this.id = id;
		this.job_ts = job_ts;
		this.command = command;
		this.user = user;
		this.device_id = device_id;
		this.set_ts = set_ts;
		this.done = done;
		this.remove = remove;
	}

	public JobDbBean() {
		id = 0;
		job_ts = 0;
		command = "";
		user = "";
		device_id = 0;
		set_ts = 0;
		done = false;
		remove = false;
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
	public String getCommand() {
		return command;
	}
	public void setCommand(String command) {
		this.command = command;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public int getDevice_id() {
		return device_id;
	}
	public void setDevice_id(int device_id) {
		this.device_id = device_id;
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
	public boolean isRemove() {
		return remove;
	}
	public void setRemove(boolean remove) {
		this.remove = remove;
	}
}
