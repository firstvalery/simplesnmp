package ru.smartsarov.simplesnmp.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import ru.smartsarov.simplesnmp.ERDSndRcv;
import ru.smartsarov.simplesnmp.ERDState;
import ru.smartsarov.simplesnmp.Props;
import ru.smartsarov.simplesnmp.SnrErd4c;
import ru.smartsarov.simplesnmp.SnrErd4cState;

public class JobsTableAgregator {

	/**
	 * Get Connection to DB Returns Connection object
	 */
	public static Connection getConnect() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:" + Props.get().getProperty("simplesnmp.db_path"));
		conn.setAutoCommit(false);
		return conn;
	}



	/**
	 * Do the job and set "done" to true Uses a connection as parameter. Passes an
	 * exception SQLException further
	 */

	private static String doJob(JobTable job) {
		switch (job.getCommand()) {
		case 1:
			ERDSndRcv.erdSendOn(job.getIp(), job.getCommunity());
			// System.out.println(Instant.ofEpochSecond(job.getJob_ts(), 0) + " to device "+
			// job.getName()+ " was send command On");
			break;
		case 2:
			ERDSndRcv.erdSendOff(job.getIp(), job.getCommunity());
			// System.out.println(Instant.ofEpochSecond(job.getJob_ts(), 0) + " to device "+
			// job.getName()+ " was send command Off");
			break;
		default:
			break;
		}
		job.setDone(true);// Set "done" for job
		return String.valueOf(job.getId());
	}

	/**
	 * Creating tables instrument
	 */
	public static String createTables() throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		QueryRunner qr = new QueryRunner();
		try {
			qr.update(conn, JobConstants.CREATE_JOBS_TABLE);
			qr.update(conn, JobConstants.CREATE_DEVICE_TABLE);
			qr.update(conn, JobConstants.CREATE_USERS_TABLE);
			qr.update(conn, JobConstants.CREATE_SUNNY_DAY_TABLE);
			qr.update(conn, JobConstants.CREATE_DEVICE_RULES_TABLE);
			qr.update(conn, JobConstants.CREATE_LOG_TABLE);
			conn.commit();
			return getJsonMessage("Tables was created");
		} finally {
			DbUtils.close(conn);
		}
	}

	/**
	 * Generalized method for adding a new record
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 */
	public static <V> String insertNewRecord(boolean checkFlag, Class<V> classTable, String insertConst,
			String selectConst, Object selectParam, Object... insertParams)
			throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			if (!checkFlag || isRecordExist(classTable, selectConst, conn, selectParam) == null) {// checking for
																									// existence
				new QueryRunner().update(conn, insertConst, insertParams);
				conn.commit();
				return getJsonMessage("new record was added");
			} else
				return getJsonMessage("record already exists!");
		} finally {
			DbUtils.close(conn);
		}
	}

	public static String getJsonMessage(String str) {
		return "{\"message\":\"" + str + "\"}";
	}

	/**
	 * Generalized method for checking the existence of an entry in a table
	 * 
	 * @throws SQLException
	 */
	private static <V> V isRecordExist(Class<V> classTable, String selectConst, Connection conn, Object obj)
			throws SQLException {
		ResultSetHandler<V> h = new BeanHandler<V>(classTable);
		return new QueryRunner().query(conn, selectConst, h, obj);
		// return rs;
	}

	/**
	 * Generalized method for getting an entries List from a table
	 * 
	 * @throws SQLException
	 */
	private static <V> List<V> getRecordList(Class<V> classTable, String selectConst, Connection conn, Object... obj)
			throws SQLException {
		ResultSetHandler<List<V>> h = new BeanListHandler<V>(classTable);
		return new QueryRunner().query(conn, selectConst, h, obj);
		// return rs;
	}

	/**
	 * Generalized method for adding a new device
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 */
	public static String insertNewDevice(String ip, String community, String name)
			throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			if (getRecordList(DeviceTable.class, JobConstants.SELECT_BY_IP_NAME, conn, ip, name).size() == 0) {// checking
																												// for
																												// existence
				new QueryRunner().update(conn, JobConstants.INSERT_DEVICE, community, name, ip);
				conn.commit();
				return getJsonMessage("new record was added");
			} else
				return getJsonMessage("IP address or name of device already exists!");
		} finally {
			DbUtils.close(conn);
		}
	}

	/**
	 * Specialized method for inserting new job with checking user and device
	 * existence
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */

	public static String insertNewJob(long job_ts, int command, String user, String device_name)
			throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			String str = null;
			// checking for user existence
			UsersTable rs_user = isRecordExist(UsersTable.class, JobConstants.SELECT_BY_USER_NAME, conn, user);
			if (rs_user == null) {
				str = "User " + user + " is not registered in system";
				return getJsonMessage(str);
			}
			// checking for device_id existence
			DeviceTable rs_device = isRecordExist(DeviceTable.class, JobConstants.SELECT_BY_NAME, conn, device_name);
			if (rs_device == null) {
				str = "Device " + device_name + " is not registered in system";
				return getJsonMessage(str);
			}
			
			//checking for existence a job for this timestamp
			if(isRecordExist(JobTable.class, JobConstants.SELECT_BY_TIMESTAMP, conn, job_ts)!=null) {
				str = "A job already exists for the device: "+ device_name + " at this timestamp!";
				return getJsonMessage(str);	
			}

			// insert a job
			new QueryRunner().update(conn, JobConstants.INSERT_JOB, job_ts, command, rs_user.getId(), rs_device.getId(),
					Instant.now().getEpochSecond(),false);
			conn.commit();
			return getJsonMessage("new job was added");
		} finally {
			DbUtils.close(conn);
		}
	}


	/**
	 * Shows select without params returns a json of elements
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public static <V> String getJsonSelect(Class<V> classTable, String selectConstant, Object... params)
			throws SQLException, ClassNotFoundException {
		Connection conn = getConnect();
		try {
			List<V> objList = getRecordList(classTable, selectConstant, conn, params);
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			return gson.toJson(objList);
		} finally {
			DbUtils.close(conn);
		}
	}

	/**
	 * Marks to remove record by it's parameter
	 */
	public static String removeElement(String columnConstant, Object param)
			throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			new QueryRunner().update(conn, columnConstant, param);
			conn.commit();
			return getJsonMessage("record was deleted");
		} finally {
			DbUtils.close(conn);
		}
	}

	/**
	 * Gets jobs from interval between current time and current time plus delta. Do
	 * this jobs immediately
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */

	public static void getCurrentJob(int delta) throws SQLException, ClassNotFoundException {
		Connection conn = getConnect();
		try {
			long curTimestamp = Instant.now().getEpochSecond();// take a current timestamp
			
			
			/*
			 * This code added for logging 
			 **/
			try(FileWriter writer = new FileWriter("C:/conf/simplesnmp/log.txt", true)){
				ERDState erdst = new Gson().fromJson(ERDSndRcv.erdGetInfo(), ERDState.class);
				writer.write(Instant.ofEpochMilli(erdst.timestamp).atOffset(ZoneOffset.ofHours(3)).toString()+"   "+erdst.mp);
				writer.append('\n');
				writer.flush();
			}catch(IOException ex){
				//TODO
			}catch(JsonSyntaxException ex) {
				//TODO
			}
			
			
			
			
			
			List<JobTable> jobList = getRecordList(JobTable.class, JobConstants.SELECT_JOBS_BETWEEN, conn, curTimestamp,
					curTimestamp + delta);
			if (jobList != null && !jobList.isEmpty()) {
				StringBuilder jobsId = new StringBuilder(10);
				jobList.stream().forEach(j -> {// do job for each element

					jobsId.append(doJob(j)).append(",");
				});
				/*
				 * StringBuilder sb = new StringBuilder(10);//make string like this:
				 * 1,2,3,4,5,....n, jobList.stream().filter(j -> j.isDone())//where
				 * "1,2,3,4,5,....n,"` is the id of completed jobs .forEach(j ->
				 * sb.append(String.valueOf(j.getId()).concat(",")));
				 */

				String st = jobsId.toString();
				String sql = JobConstants.MARK_FOR_DONE + "(".concat(st.substring(0, st.lastIndexOf(","))).concat(")");
				new QueryRunner().update(conn, sql);

				conn.commit();
			}
		} finally {
			DbUtils.rollbackAndClose(conn);
		}
	}

	/**
	 * method for reading file line by line. Just an Instrument for creating sunny day table
	 * @throws IOException 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * 
	 * 
	 **/
	public static String getSunnyFile(String fileName) throws IOException, ClassNotFoundException, SQLException{      
			File f = new File(fileName);
	        String readLine = "";
	        String[] line =null;
	        List<Object[]>ts_list = new ArrayList<Object[]>();
	        
	        //read file and prepare Object...param to QueryRunner
	        try(BufferedReader b = new BufferedReader(new FileReader(f))) {	
	        	b.readLine();
	        	 while ((readLine = b.readLine()) != null) {   	
	        		    line = readLine.split(";");
	        		    List<Object>tmp = new ArrayList<Object>();
	        		    tmp.add(line[1]);
	        		    tmp.add(line[2]);
	               		ts_list.add(tmp.toArray());
	             }    
	        }
	        
	        //save to DB
	        Connection conn = getConnect();
	        try {
	        	Object params[][] = ts_list.toArray(new Object[0][0]);
	        	new QueryRunner().batch(conn, JobConstants.INSERT_SUNNY_DAY, params);
				conn.commit();
				return getJsonMessage("Sunny day lenght table was Inserted");
	        }finally {
	        	DbUtils.rollbackAndClose(conn);
	        }	         
	}

	
	/**
	 * Adds a new jobs to device using sunny day calendar from 
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * 
	 * 
	 */
    public static String setSunnyScheduleForDevice(String user, String deviceName) throws ClassNotFoundException, SQLException {
    	Connection conn = getConnect();	
    	try {
    		//check for Sunny Schedule existence	
    		if(!getRecordList(JobTable.class, JobConstants.SELECT_SUNNY_DAY_CHECK, conn, deviceName).isEmpty()) {
    			return getJsonMessage("Sunny day lenght schedule for device "+ deviceName +" already exists");
    		}
    		
    		long timestamp = Instant.now().getEpochSecond();
    		
    		String str = null;
			// checking for user existence
			UsersTable rs_user = isRecordExist(UsersTable.class, JobConstants.SELECT_BY_USER_NAME, conn, user);
			if (rs_user == null) {
				str = "User " + user + " is not registered in system";
				return getJsonMessage(str);
			}
			// checking for device_id existence
			DeviceTable rs_device = isRecordExist(DeviceTable.class, JobConstants.SELECT_BY_NAME, conn, deviceName);
			if (rs_device == null) {
				str = "Device " + deviceName + " is not registered in system";
				return getJsonMessage(str);
			}

    		//read data from db sunny table	
    		List<SunnyDayTable>sunnyList = getRecordList(SunnyDayTable.class, 
    				JobConstants.SELECT_SUNNY_DAY_LENGTH, conn, (Object[])null);
    		
    		//get rules for creating schedule
    		List<DeviceRulesTable> dvrRule = getRecordList(DeviceRulesTable.class, JobConstants.SELECT_DEVICE_RULE_BY_DEVICE_ID_AND_TYPE,
    				 conn, rs_device.getId(), JobConstants.DEVICE_RULE_TYPE_OFF);
    		String workOff="";
    		String weekendOff="";
    		if (!dvrRule.isEmpty()) {
    			workOff = dvrRule.get(0).getWork_t();
    			weekendOff = dvrRule.get(0).getWeekend_t();
    		}
    		dvrRule = getRecordList(DeviceRulesTable.class, JobConstants.SELECT_DEVICE_RULE_BY_DEVICE_ID_AND_TYPE,
   				 conn, rs_device.getId(), JobConstants.DEVICE_RULE_TYPE_ON);
    		String workOn="";
    		String weekendOn="";
    		if (!dvrRule.isEmpty()) {
    			workOn = dvrRule.get(0).getWork_t();
    		    weekendOn = dvrRule.get(0).getWeekend_t();
    		}
    		
    		
    		//create List of params using LocalDateTime
    		List<Object[]>jobSunny= new ArrayList<Object[]>();// 
            LocalDate ld = LocalDate.now();
            int year = ld.getYear();
            int nextDay=0;
            String tmpDayTime="";
    		while(ld.plusDays(1).getYear()!=year+1) {// Run while not next year
    			nextDay = (ld.getDayOfYear() > 364) ? 0 : 1;
    			List<Object> tmp = new ArrayList<>();
    			
    			if(workOff!="") {
    				if(ld.plusDays(nextDay).getDayOfWeek().equals(DayOfWeek.SATURDAY) || ld.plusDays(nextDay).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
    					tmpDayTime=weekendOff;
    				}else {
    					tmpDayTime=workOff;
    				}
    			}else {
        				tmpDayTime = sunnyList.get(ld.getDayOfYear()-1+nextDay).getOff_ts();
    			}
    			tmp.add(LocalDateTime.of(ld.plusDays(nextDay), 
    					LocalTime.parse(tmpDayTime, DateTimeFormatter.ISO_LOCAL_TIME)).
    					toEpochSecond(ZoneOffset.ofHours(3)));
    			tmp.add(2);
    			tmp.add(rs_user.getId());
    			tmp.add(rs_device.getId());
    			tmp.add(timestamp);
    			tmp.add(true);
    			jobSunny.add(tmp.toArray());
    			
    			if(workOn!="") {
    				if(ld.plusDays(nextDay).getDayOfWeek().equals(DayOfWeek.SATURDAY) || ld.plusDays(nextDay).getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
    					tmpDayTime=weekendOn;
    				}else {
    					tmpDayTime=workOn;
    				}
    			}else {
        				tmpDayTime = sunnyList.get(ld.getDayOfYear()-1+nextDay).getOff_ts();
    			}
    			tmp = new ArrayList<>();
    			tmp.add(LocalDateTime.of(ld.plusDays(nextDay), 
    					LocalTime.parse(sunnyList.get(ld.getDayOfYear()-1+nextDay).getOn_ts(), DateTimeFormatter.ISO_LOCAL_TIME)).
    					toEpochSecond(ZoneOffset.ofHours(3)));
    			tmp.add(1);
    			tmp.add(rs_user.getId());
    			tmp.add(rs_device.getId());
    			tmp.add(timestamp);
    			tmp.add(true);
    			jobSunny.add(tmp.toArray());
    			

    			ld = ld.plusDays(nextDay);
    		}
    		
    		 

    		//QueryRunner().batch
    		Object[][] params = jobSunny.toArray(new Object[0][0]);
    		new QueryRunner().batch(conn, JobConstants.INSERT_JOB, params);
    		conn.commit();
    		
		} finally{
			DbUtils.rollbackAndClose(conn);
		}   	
    	return getJsonMessage("Sunny day lenght schedule was added for device "+ deviceName);	
    }
    
	/**
	 * Generalized method for adding a new device rule
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 */
	public static String insertDeviceRule(String deviceName, String workT, String weekendT, int ruleType)
			throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			// checking for device_id existence
			String str;
			DeviceTable rs_device = isRecordExist(DeviceTable.class, JobConstants.SELECT_BY_NAME, conn, deviceName);
			if (rs_device == null) {
				str = "Device " + deviceName + " is not registered in system";
				return getJsonMessage(str);
			}
			if(getRecordList(DeviceRulesTable.class, JobConstants.SELECT_DEVICE_RULE_BY_DEVICE_ID_AND_TYPE, conn, rs_device.getId(), ruleType).size()!=0) {
				str = "The rule for " + deviceName +" with type "+ String.valueOf(ruleType) + " is exist";
				return getJsonMessage(str);
			}
			try{LocalTime.parse(workT, DateTimeFormatter.ISO_LOCAL_TIME);
				}catch(DateTimeParseException e) {
					return getJsonMessage("parameter workT = "+String.valueOf(workT)+" - Bad format. Try HH:MM:SS");
				}
			try{LocalTime.parse(weekendT, DateTimeFormatter.ISO_LOCAL_TIME);
			}catch(DateTimeParseException e) {
				return getJsonMessage("parameter weekendT = "+String.valueOf(weekendT)+" - Bad format. Try HH:MM:SS");
			}

				new QueryRunner().update(conn, JobConstants.INSERT_DEVICE_RULE, rs_device.getId(), workT, weekendT, ruleType);
				conn.commit();
				return getJsonMessage("new record was added");
		} finally {
			DbUtils.close(conn);
		}
	}
	/**
	 * Generalized method for get a device rules
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * 
	 */
	public static String showDeviceRules(String deviceName)
			throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			// checking for device_id existence
			String str;
			DeviceTable rs_device = isRecordExist(DeviceTable.class, JobConstants.SELECT_BY_NAME, conn, deviceName);
			if (rs_device == null) {
				str = "Device " + deviceName + " is not registered in system";
				return getJsonMessage(str);
			}		
				return getJsonSelect(DeviceRulesTable.class, JobConstants.SELECT_DEVICE_RULE_BY_DEVICE_ID, rs_device.getId());
		} finally {
			DbUtils.close(conn);
		}
	}
	
	
	/**
	 * Device state logging 
	 * @throws SQLException 
	 * TODO
	 **/
	public static String getInfoFromDevices() throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			Gson gs= new Gson();
			LogTable rs = isRecordExist(LogTable.class, JobConstants.SELECT_LOGGED_DEVICE, conn, "SNRERD4C");	
			if(rs.getText()!=null){
				try {
					SnrErd4cState snrDev = gs.fromJson(rs.getText(), SnrErd4cState.class);
					String tmpstr = SnrErd4c.getContactState();
					SnrErd4cState snrDevnew = gs.fromJson(tmpstr, SnrErd4cState.class);
					if (snrDev.compareTo(snrDevnew)!=0) {
						Object[] params = new Object[3];
						params[0]=Instant.ofEpochMilli(snrDevnew.ts).atZone(ZoneOffset.ofHours(3));
						params[1]="SNRERD4C";
						params[2]=tmpstr;
						new QueryRunner().update(conn, JobConstants.INSERT_DEVICE_LOG, params);
						conn.commit();
					}
				}catch(JsonSyntaxException e) {
					//TODO
				}	
			}else {
				try {
					String tmpstr = SnrErd4c.getContactState();
					SnrErd4cState snrDevnew = gs.fromJson(tmpstr, SnrErd4cState.class);
					Object[] params = new Object[3];
					params[0]=Instant.ofEpochMilli(snrDevnew.ts).atZone(ZoneOffset.ofHours(3));
					params[1]="SNRERD4C";
					params[2]=tmpstr;
					new QueryRunner().update(conn, JobConstants.INSERT_DEVICE_LOG, params);
					conn.commit();
				}catch(JsonSyntaxException e) {
					//TODO
				}
			}	
			return null;
		} finally {
			DbUtils.close(conn);
		}
	}
    
    
}
