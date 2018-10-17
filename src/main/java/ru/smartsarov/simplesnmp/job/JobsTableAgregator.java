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




public class JobsTableAgregator {
	
	/* Get Connection to DB
	 * Returns Connection object
	 */
	public static Connection getConnect() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		Class.forName("org.sqlite.JDBC");		
		   conn = DriverManager.getConnection("jdbc:sqlite:D:/WORKSPACE/simplesnmp/src/main/resources/db/scheduler.db");
		   conn.setAutoCommit(false);
		   return conn;
	} 
	
	
	
	/*
	 *	Selects jobs from the table with a timestamp greater current time and lower current time plus delta. 
	 *  Performs a task for each JobDbBean object.
	 *  Mark a task in the "done" field
	 */
	
	public static void getCurrentJob(int delta) {
		try {
			Connection conn = getConnect();
			String sql = "SELECT * FROM jobs_table WHERE job_ts >= ? AND job_ts < ? AND done = 0 AND remove = 0";
			try {
				ResultSetHandler<List<JobDbBean>> h = new BeanListHandler<JobDbBean>(JobDbBean.class);
				
				long curTimestamp = Instant.now().getEpochSecond();//take a current timestamp		
				List<JobDbBean>jobList = new QueryRunner().query(conn, sql, h, curTimestamp, curTimestamp + delta);
				
				for(JobDbBean tmp : jobList) {
					doJob(tmp, conn);
				}
				conn.commit();
			}finally {
				DbUtils.close(conn);
			}	
		}catch(Exception e) {
			e.printStackTrace();
		}
	} 
	
	
	
	/*
	 * Do the job and set "done" to true
	 * Uses a connection as parameter. Passes an exception SQLException further
	 */
	
	private static void doJob(JobDbBean job, Connection conn) throws SQLException{
		DeviceDbBean dev = getDeviceDbBean(job.getDevice_id(), conn);
		switch (job.getCommand().toLowerCase()){
		case "on":	
				//ERDSndRcv.erdSendOn(dev.getIp(),dev.getCommunity());	
				System.out.println(Instant.ofEpochSecond(job.getJob_ts(), 0) +  "     to device "+ dev.getDesc()+ " was send command On");
			break;
		case "off":	
				//ERDSndRcv.erdSendOff(dev.getIp(),dev.getCommunity());	
				System.out.println(Instant.ofEpochSecond(job.getJob_ts(), 0) +  "     to device "+ dev.getDesc()+ " was send command Off");
			break;
		default:
			break;
		}
		String sql = "UPDATE jobs_table SET done = 1 WHERE id = ?";//Mark a done 
		
		new QueryRunner().update(conn, sql, job.getId());
		
	}
	
	/*
	 * Create the job table. It's just an instrument for creating table
	 */
	public static String createTableJob() {	
	    try {
	    	
	    	Connection conn = getConnect();
	    	try {
	    		QueryRunner qr = new QueryRunner();
	    		
	    		String sql = "CREATE TABLE IF NOT EXISTS jobs_table (id INTEGER PRIMARY KEY, job_ts INTEGER, "+
	    		"command TEXT, user TEXT, device_id INTEGER, set_ts INTEGER, done NUMERIC, remove NUMERIC)";
	    		qr.update(conn, sql);
	    		
	    		sql = "CREATE TABLE IF NOT EXISTS device_table (id INTEGER PRIMARY KEY, device_id INTEGER, ip TEXT, community TEXT, desc TEXT)";
	    		qr.update(conn, sql);
	    		
			     conn.commit();
			     
			     return "Success!";
	    	}finally {
	    		DbUtils.close(conn);
	    	}
	    }catch(Exception e) {
	    	return e.getMessage();
	    }   
	}
	
	
	
	
	/*
	 * Insert the job to DB from JobDbBean object
	 */
	public static int setJobToDb(JobDbBean job){	
	    try {
	    	String sql = "insert into jobs_table"+
	    						"(job_ts, command, user, device_id, set_ts, done, remove)"+
	    																		"values(?,?,?,?,?,?,?)";
	    	Connection conn = getConnect();
	    	try {
	    		int res = new QueryRunner().update(conn, sql, job.getJob_ts(), job.getCommand(), job.getUser(),
	    											job.getDevice_id(), job.getSet_ts(), job.isDone(), job.isRemove());
	    		conn.commit();
	    		return res;
	    	}finally {
	    		DbUtils.close(conn);
	    	}
	    }catch(Exception e) {
	    	return 0;
	    }   
	}
	
	
	/*
	 * Create a JobDbBean object from json
	 * TODO
	 */
//	public static JobDbBean createJobDbBeanFromJson(String str) {
//		Gson gson = new GsonBuilder().create();
//		JobDbBean job =  gson.fromJson(str, JobDbBean.class);
//				job.setSet_ts(Instant.now().getEpochSecond());
//				job.setDone(false);
//				return job;
//	}
	
	/*
	 * Shows scheduled jobs  between two timestampes
	 * returns a json of JobDbBean elements
	 */
	public static String getJobsFrom(long minTimestamp, long maxTimestamp)  {	
		try {
			 Connection conn = getConnect();	
			 String sql = "SELECT * FROM jobs_table WHERE job_ts >= ? AND job_ts < ?";
			 ResultSetHandler<List<JobDbBean>> h = new BeanListHandler<JobDbBean>(JobDbBean.class);
			 try {
				 List<JobDbBean>jobList = new QueryRunner().query(conn, sql, h, minTimestamp, maxTimestamp);
				 Gson gson = new GsonBuilder().create();
				 return gson.toJson(jobList);
			 }
			 finally {
					DbUtils.close(conn);
			 } 
		}catch(Exception e) {
			return "[]";
		}	
	}
	
	
	/*
	 * Mark to remove job from task by it's id
	 * 
	 */
	public static int removeJob(int id)  {	
		try {
			 Connection conn = getConnect();	
			 String sql = "UPDATE jobs_table SET remove = 1 WHERE id = ?";
			 try {
				 int res = new QueryRunner().update(conn, sql, id);
			     conn.commit();
			     return res;
			 }
			 finally {
					DbUtils.close(conn);
			 } 
		}catch(Exception e) {
			return 0;
		}	
	}
		
	
	/*
	 * Selects device bean by device_id
	 * Uses a connection as parameter. Passes an exception SQLException further
	 * 
	 */
	
	private static DeviceDbBean getDeviceDbBean(int device_id, Connection conn) throws SQLException {
	
			 String sql = "SELECT * FROM device_table WHERE device_id = ?";
			 ResultSetHandler<DeviceDbBean> h = new BeanHandler<DeviceDbBean>(DeviceDbBean.class);  
			 DeviceDbBean rs = new QueryRunner().query(conn, sql, h, device_id );
			 return rs;
	}
}
