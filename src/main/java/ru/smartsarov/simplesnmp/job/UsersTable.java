package ru.smartsarov.simplesnmp.job;

public class UsersTable {
	private int id;
	private String user_name;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public UsersTable(int id, String user_name) {
		this.id = id;
		this.user_name = user_name;
	}
	public UsersTable() {
		this.id = 0;
		this.user_name = "";
	}	
}
