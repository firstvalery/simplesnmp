package ru.smartsarov.simplesnmp;

public class ERDState {
	public String timeshtamp="";
	public String quallity="";
	public boolean ec = false;
	public boolean mp = false;
	public boolean rs = false;
	
	ERDState(String ts, boolean ec, boolean mp, boolean rs){
		this.timeshtamp = ts;
		quallity = "GOOD";
		this.ec = ec;
		this.mp = mp;
		this.rs = rs;
	}
	
	ERDState(String ts){
		this.timeshtamp = ts;
		quallity = "BAD";
		this.ec = false;
		this.mp = false;
		this.rs = false;
	}
}
