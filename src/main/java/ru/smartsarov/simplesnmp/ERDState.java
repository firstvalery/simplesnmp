package ru.smartsarov.simplesnmp;

import java.time.LocalDateTime;

public class ERDState {
	public LocalDateTime timestamp=null;
	public String quallity="";
	public boolean ec = false;
	public boolean mp = false;
	public boolean rs = false;
	
	ERDState(LocalDateTime ts, boolean ec, boolean mp, boolean rs){
		this.timestamp = ts;
		quallity = "GOOD";
		this.ec = ec;
		this.mp = mp;
		this.rs = rs;
	}
	
	ERDState(LocalDateTime ts){
		this.timestamp = ts;
		quallity = "BAD";
		this.ec = false;
		this.mp = false;
		this.rs = false;
	}
}
