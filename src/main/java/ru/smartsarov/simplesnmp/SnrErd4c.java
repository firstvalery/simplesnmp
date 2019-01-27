package ru.smartsarov.simplesnmp;

import com.google.gson.Gson;

import ru.smartsarov.simplesnmp.job.JobsTableAgregator;

public class SnrErd4c {
	static final String ERD_DIGITAL_OUTPUT_OID = "1.3.6.1.4.1.40418.2.6.2.2.1.3.";
	static final int SET_OFF_VALUE = 0;
	static final int SET_ON_VALUE = 1;
	static final int SET_RELOAD_VALUE = 2;
	
	
	/**
	 * Turn On DO number "contactNum". Returns JSON formatted message
	 * 
	 */
	public static String erdSendOn(int contactNum) {
		if ((contactNum<1)&&(contactNum>6)) 
			return JobsTableAgregator.getJsonMessage("contactNum must be int and from {1,2,3,4,5,6}");
		
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(Props.get().getProperty("simplesnmp.alarm_ip", "127.0.0.1"), 
						Props.get().getProperty("simplesnmp.alarm_write_community", "public"), 
							ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_ON_VALUE);	
		return getContactState();
	}
	
	
	/**
	 * Turn Off DO number "contactNum". Returns JSON formatted message
	 * 
	 */
	public static String erdSendOff(int contactNum) {
		if ((contactNum<1)&&(contactNum>6)) 
			return JobsTableAgregator.getJsonMessage("contactNum must be int and from {1,2,3,4,5,6}");
		
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(Props.get().getProperty("simplesnmp.alarm_ip", "127.0.0.1"), 
				Props.get().getProperty("simplesnmp.alarm_write_community", "public"), 
				ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_OFF_VALUE);		
		return getContactState();
	}
	
	/**
	 * Reload DO number "contactNum". Returns JSON formatted message
	 * 
	 */
	public static String erdSendReload(int contactNum) {
		if ((contactNum<1)&&(contactNum>6)) 
			return JobsTableAgregator.getJsonMessage("contactNum must be int and from {1,2,3,4,5,6}");
		
		SnmpTest st = new SnmpTest();
		st.snmpSetInt(Props.get().getProperty("simplesnmp.alarm_ip", "127.0.0.1"), 
				Props.get().getProperty("simplesnmp.alarm_write_community", "public"), 
				ERD_DIGITAL_OUTPUT_OID.concat(String.valueOf(contactNum)), SET_RELOAD_VALUE);		
		return getContactState();
	}
	
	
	/**
	 * Returns JSON formatted state of digital outputs 
	 * 
	 */
	public static String getContactState() {
		SnmpTest st = new SnmpTest();
		SnrErd4cState js = new SnrErd4cState();
		int ec = st.snmpGetInt("192.168.15.20", "public", ERD_DIGITAL_OUTPUT_OID.concat("1"));
		if (ec>=0) {
			js.con1 = ec==1;
			ec = st.snmpGetInt("192.168.15.20", "public", ERD_DIGITAL_OUTPUT_OID.concat("2"));
			if (ec>=0) {
				js.con2 = ec==1;
				ec = st.snmpGetInt("192.168.15.20", "public", ERD_DIGITAL_OUTPUT_OID.concat("3"));	
				if (ec>=0) {
					js.con3 = ec==1;
					ec = st.snmpGetInt("192.168.15.20", "public", ERD_DIGITAL_OUTPUT_OID.concat("4"));
					if (ec>=0) {
						js.con4 = ec==1;
						ec = st.snmpGetInt("192.168.15.20", "public", ERD_DIGITAL_OUTPUT_OID.concat("5"));	
						if (ec>=0) {
							js.con5 = ec==1;
							ec = st.snmpGetInt("192.168.15.20", "public", ERD_DIGITAL_OUTPUT_OID.concat("6"));	
							if (ec>=0) {
								js.con6 = ec==1;
								js.quality = "good";
								return new Gson().toJson(js); 
							}
						}
					}
				}
			}
		} 
		return new Gson().toJson(new SnrErd4cState());
	}
}
