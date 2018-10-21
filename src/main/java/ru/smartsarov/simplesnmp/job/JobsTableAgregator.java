package ru.smartsarov.simplesnmp.job;

import java.sql.Connection;
import java.sql.DriverManager;


import java.sql.SQLException;
import java.time.Instant;

import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ru.smartsarov.simplesnmp.ERDSndRcv;
import ru.smartsarov.simplesnmp.Props;




public class JobsTableAgregator {
	
	/** Get Connection to DB
	 * Returns Connection object
	 */
	public static Connection getConnect() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName("org.sqlite.JDBC");		
		   conn = DriverManager.getConnection("jdbc:sqlite:"+Props.get().getProperty("simplesnmp.db_path"));
		   conn.setAutoCommit(false);
		   return conn;
	} 
	
	
	
	/*
	 *	Selects jobs from the table with a timestamp greater current time and lower current time plus delta. 
	 *  Performs a task for each JobDbBean object.
	 *  Mark a task in the "done" field
	 */
	/*select jobs_table.command, device.community, device.desc, device_interface.ip 
	from jobs_table
	join device on jobs_table.device_id = device.id and device.removed = 0
	join device_interface on device_interface.device_id = device.id and device_interface.removed = 0 
	where jobs_table.done = 0 and jobs_table.remove = 0 
	--jobs_table.job_ts between */
	
	
	
	
	
	/*
	 * Do the job and set "done" to true
	 * Uses a connection as parameter. Passes an exception SQLException further
	 */
	
	private static String doJob(JobTable job) {
		switch (job.getCommand()){
		case 1:	
				ERDSndRcv.erdSendOn(job.getIp(),job.getCommunity());	
				//System.out.println(Instant.ofEpochSecond(job.getJob_ts(), 0) +  "     to device "+ job.getName()+ " was send command On");
			break;
		case 2:	
				ERDSndRcv.erdSendOff(job.getIp(),job.getCommunity());	
				//System.out.println(Instant.ofEpochSecond(job.getJob_ts(), 0) +  "     to device "+ job.getName()+ " was send command Off");
			break;
		default:
			break;
		}
		job.setDone(true);//Set "done" for job
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
			    conn.commit();     
			    return getJsonMessage("Tables was created");
	    	}finally {
	    		DbUtils.close(conn);
	    	}  
	}
	
	
	/**
	 * Generalized method for adding a new record
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * 
	 */
	public static <V> String insertNewRecord(boolean checkFlag, Class<V> classTable, String insertConst,
			String selectConst, Object selectParam, Object... insertParams) 
					throws ClassNotFoundException, SQLException 
					  {
		Connection conn = getConnect();
		try {
			if( !checkFlag || isRecordExist(classTable, selectConst, conn, selectParam )==null){//checking for existence
				new QueryRunner().update(conn, insertConst, insertParams);
				conn.commit();     
			    return getJsonMessage("new record was added");
			}else return getJsonMessage("record already exists!");
		}finally {
			DbUtils.close(conn);
		}  	
	}
	
	private static String getJsonMessage(String str) {
		return "{\"message\":\""+ str +"\"}";
	}
	
	
	
	
/**
 * Generalized method for checking the existence of an entry in a table
 * 
 * @throws SQLException 
 */
	private static <V> V isRecordExist(Class<V> classTable, String selectConst, Connection conn, Object obj) throws SQLException {	
		ResultSetHandler<V> h = new BeanHandler<V>(classTable);
		return new QueryRunner().query(conn, selectConst, h, obj );
		//return rs;
	}
	
	/**
	 * Generalized method for getting an entries List from a table
	 * 
	 * @throws SQLException 
	 */	
	private static <V> List<V> getRecordList(Class<V> classTable, String selectConst, Connection conn, Object... obj) throws SQLException {	
		ResultSetHandler<List<V>> h = new BeanListHandler<V>(classTable);
		return new QueryRunner().query(conn, selectConst, h, obj );
		//return rs;
	}
	
	
	/**
	 * Generalized method for adding a new device
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 * 
	 */
	public static String insertNewDevice(String ip,  String community,  String name) 
					throws ClassNotFoundException, SQLException 
					  {
		Connection conn = getConnect();
		try {
			if(getRecordList(DeviceTable.class, JobConstants.SELECT_BY_IP_NAME, conn, ip, name ).size()==0){//checking for existence
				new QueryRunner().update(conn, JobConstants.INSERT_DEVICE,  community, name, ip);
				conn.commit();     
			    return getJsonMessage("new record was added");
			}else return getJsonMessage("IP address or name of device already exists!");
		}finally {
			DbUtils.close(conn);
		}  	
	}
	
	 
	/**
	 * Specialized method for inserting new job
	 * with checking user and device existence
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	
	public static String insertNewJob(long job_ts, int command, String user, String device_name) throws ClassNotFoundException, SQLException {
		Connection conn = getConnect();
		try {
			String str=null;
			//checking for user existence
			UsersTable rs_user = isRecordExist(UsersTable.class, JobConstants.SELECT_BY_USER_NAME, conn, user);
			if (rs_user==null) { 
				str = "User "+ user + " is not registered in system";
				return getJsonMessage(str);
			}
			//checking for device_id existence
			DeviceTable rs_device = isRecordExist(DeviceTable.class, JobConstants.SELECT_BY_NAME, conn, device_name);
			if (rs_device ==null) {
				str = "Device "+ device_name + " is not registered in system";
				return getJsonMessage(str);
			}
			//insert a job
			new QueryRunner().update(conn, JobConstants.INSERT_JOB, job_ts, command, rs_user.getId(), rs_device.getId(), Instant.now().getEpochSecond());
			conn.commit();
		    return getJsonMessage("new job was added");
		}finally {
			DbUtils.close(conn);
		}	  
	}
	
	
	

	/**
	 * Shows scheduled jobs  between two timestampes
	 * returns a json of JobJson elements
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static String getJobsFrom(long minTimestamp, long maxTimestamp) throws SQLException, ClassNotFoundException {	
		Connection conn = getConnect();	
		try {
			List<JobTable>jobList = getRecordList(JobTable.class, JobConstants.SELECT_JOBS_BETWEEN, conn, minTimestamp, maxTimestamp);
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
			return gson.toJson(jobList);
		}finally {
					DbUtils.close(conn);
			 }
	}
	
	
	/**
	 * Marks to remove job from task by it's id
	 */
	public static String removeJob(int id) throws ClassNotFoundException, SQLException  {	
			 Connection conn = getConnect();	
			 try {
				 new QueryRunner().update(conn, JobConstants.MARK_FOR_REMOVING, id);
			     conn.commit();
			     return getJsonMessage("job was deleted");
			 }
			 finally {
					DbUtils.close(conn);
			 } 
	}
	
	
	/**
	 * Gets jobs from interval between current time and current time plus delta.
	 * Do this jobs immediately
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
		
	
	public static void getCurrentJob(int delta) throws SQLException, ClassNotFoundException {
			Connection conn = getConnect();
			try {
				long curTimestamp = Instant.now().getEpochSecond();//take a current timestamp		
				List<JobTable> jobList = getRecordList(JobTable.class, JobConstants.SELECT_JOBS_BETWEEN,
												conn, curTimestamp, curTimestamp + delta);
				if (jobList!=null && !jobList.isEmpty()) {
					StringBuilder jobsId = new StringBuilder(10);			
					jobList.stream().forEach(j-> {//do job for each element
							
							jobsId.append(doJob(j)).append(",");
					});
	/*				
					StringBuilder sb = new StringBuilder(10);//make string like this: 1,2,3,4,5,....n,
					jobList.stream().filter(j -> j.isDone())//where "1,2,3,4,5,....n,"` is the id of completed jobs
									.forEach(j -> sb.append(String.valueOf(j.getId()).concat(",")));*/
					
					String st = jobsId.toString();
					String sql = JobConstants.MARK_FOR_DONE+ "(".concat(st.substring(0,st.lastIndexOf(","))).concat(")");
					new QueryRunner().update(conn, sql);
	
					conn.commit();
				}
			}finally {
				DbUtils.rollbackAndClose(conn);
			}	
	} 
	

}
