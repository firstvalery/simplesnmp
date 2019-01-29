package ru.smartsarov.simplesnmp.job;
public class JobConstants {
	/**
	 * CREATE TABLE `jobs` constant
	 */
	public final static String CREATE_JOBS_TABLE = "CREATE TABLE IF NOT EXISTS jobs (id INTEGER, job_ts INTEGER, command INTEGER," +
			"user	INTEGER, device_id	INTEGER, set_ts	INTEGER, done	NUMERIC NOT NULL DEFAULT 0,"+
			"removed	NUMERIC NOT NULL DEFAULT 0,sunny NUMERIC NOT NULL DEFAULT 0, PRIMARY KEY(id),FOREIGN KEY(user) REFERENCES users(id) ON UPDATE CASCADE,"+
			"FOREIGN KEY(device_id) REFERENCES device(id) ON UPDATE CASCADE)";
	
	/**
	 * CREATE TABLE `device` constant
	 */
	public final static String CREATE_DEVICE_TABLE = "CREATE TABLE IF NOT EXISTS device (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,"+
			"community TEXT, name TEXT NOT NULL, type TEXT, ip TEXT NOT NULL, removed NUMERIC NOT NULL DEFAULT 0)";
	
	/**
	 * CREATE TABLE `users` constant
	 */
	public final static String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT,"+
			" user_name TEXT NOT NULL, removed NUMERIC NOT NULL DEFAULT 0)";
	
	/**
	 * CREATE TABLE `sunny_day` constant
	 */
	public final static String CREATE_SUNNY_DAY_TABLE = "CREATE TABLE IF NOT EXISTS sunny_day (day INTEGER PRIMARY KEY AUTOINCREMENT,"+
			" off_ts TEXT NOT NULL, on_ts TEXT NOT NULL)";
	/**
	 * CREATE TABLE `device_rules` constant
	 */
	public final static String CREATE_DEVICE_RULES_TABLE = "CREATE TABLE IF NOT EXISTS device_rules (" 
			+"	id	INTEGER," + 
			"	device_id	INTEGER," + 
			"	work_t	TEXT," + 
			"	weekend_t	TEXT," + 
			"	removed	NUMERIC DEFAULT 0," + 
			"	rule_type	INTEGER," + 
			"	FOREIGN KEY(device_id) REFERENCES device(id) ON UPDATE CASCADE," + 
			"	PRIMARY KEY(id));";

	
	public final static String CREATE_LOG_TABLE =" CREATE TABLE IF NOT EXISTS log"+ 
			"( id INTEGER PRIMARY KEY AUTOINCREMENT, ts TEXT, device TEXT, text TEXT, removed NUMERIC NOT NULL DEFAULT 0 )";
	
	
	/**
	 * Insert new user in table "users" statement 
	 */
	public final static String INSERT_USER = "INSERT INTO users (user_name) values (?)";

	/**
	 * Select by user_name from table "users" statement
	 */
	public final static String SELECT_BY_USER_NAME = "SELECT id, user_name FROM users WHERE user_name = ? AND removed = 0";
	
	/**
	 * Insert new device in table "device" statement
	 */
	public final static String INSERT_DEVICE = "INSERT INTO device (community, name, ip) values (?,?,?)";
	
	/**
	 * Insert new device rule in table "device_rules" statement
	 */
	public final static String INSERT_DEVICE_RULE = "INSERT INTO device_rules (device_id, work_t, weekend_t,rule_type) values (?,?,?,?)";
	/**
	 * Select device rule by device_id and rule_type from table "device_rules" statement
	 */
	public final static String SELECT_DEVICE_RULE_BY_DEVICE_ID_AND_TYPE = "SELECT device_rules.device_id, device_rules.work_t, device_rules.weekend_t  "
			+ "FROM device_rules WHERE device_rules.device_id = ? AND device_rules.removed = 0 AND device_rules.rule_type = ?";
	public final static String SELECT_DEVICE_RULE_BY_DEVICE_ID = "SELECT device_rules.id, device_rules.device_id, device_rules.work_t, device_rules.weekend_t, device_rules.rule_type  "
			+ "FROM device_rules WHERE device_rules.device_id = ? AND device_rules.removed = 0 ";

	/**
	 * Select by IP-address or name from table "device" statement
	 */
	public final static String SELECT_BY_IP_NAME = "SELECT device.id, device.community, device.name, device.type, device.removed, device.ip  FROM device WHERE (ip = ?  OR name =?) AND removed = 0";
	/**
	 * Select by id from table "device" statement
	 */
	public final static String SELECT_BY_NAME = "SELECT device.id, device.community, device.name, device.type, device.removed, device.ip  FROM device WHERE name = ? AND removed = 0";

	/**
	 * Insert new job in table "jobs" statement
	 */
	public final static String INSERT_JOB = "INSERT INTO jobs (job_ts, command, user, device_id, set_ts, sunny) values (?,?,?,?,?,?)";

	/**
	 * Select list of jobs by min and mix timestamps statement
	 */	
	public final static String SELECT_JOBS_BETWEEN = "select jobs.id, jobs.job_ts, users.user_name, jobs.set_ts,"+
			" jobs.command, device.name, device.type, device.ip, jobs.done, jobs.removed, jobs.sunny, device.community"+
			" from jobs"+
			" join users on jobs.user = users.id and users.removed = 0"+
			" join device on jobs.device_id = device.id and device.removed = 0"+
			" where jobs.removed = 0"+ 
			" and jobs.job_ts between ? and ? "+
			" ORDER BY jobs.job_ts";
	
	/**
	 * update jobs field removed statement
	 */
	public final static String MARK_FOR_REMOVING_JOBS_BY_ID = "UPDATE jobs SET removed = 1 WHERE id = ?";
	
	/**
	 * update field removed statement
	 */
	public final static String MARK_FOR_REMOVING_USER_BY_NAME = "UPDATE users SET removed = 1 WHERE user_name = ?";
	
	/**
	 * update field removed statement
	 */
	public final static String MARK_FOR_REMOVING_DEVICE_BY_NAME = "UPDATE device SET removed = 1 WHERE name = ?";
	
	/**
	 * update field removed statement
	 */
	public final static String MARK_FOR_REMOVING_DEVICE_RULE_BY_ID = "UPDATE device_rules SET removed = 1 WHERE id = ?";
	
	/**
	 * update field removed for jobs statement
	 */
	public final static String MARK_FOR_REMOVING_JOBS_SUNNY = "UPDATE jobs SET removed = 1 WHERE jobs.device_id =" + 
	 " (SELECT device.id FROM device WHERE device.name = ? AND device.removed = 0) AND jobs.sunny = 1";
	
	/**
	 * a part of update field done statement
	 */
	public final static String MARK_FOR_DONE =  "UPDATE jobs SET done = 1 WHERE id in";
	
	/**
	 * The period of checking for new jobs
	 */
	public final static int DELTA_FOR_CHECK =  60;
	
	/**
	 * Select all users statement
	 */
	public final static String SELECT_USERS = "SELECT users.id, users.user_name FROM users WHERE removed = 0";

	/**
	 * Select all devices statement
	 */
	public final static String SELECT_DEVICES = "SELECT device.id, device.community, device.ip, device.name, device.type FROM device WHERE removed = 0";
	
	/**
	 * Select jobs statement
	 */
	public final static String SELECT_BY_TIMESTAMP = "SELECT jobs.id FROM jobs WHERE jobs.job_ts =? AND jobs.removed = 0";
	
	/**
	 * Insert sunny_day table
	 */
	public final static String INSERT_SUNNY_DAY = "INSERT INTO sunny_day (off_ts, on_ts) VALUES (?,?)";
	
	/**
	 * Select jobs table for checking sunny mode existence
	 */
	public final static String SELECT_SUNNY_DAY_CHECK = "SELECT jobs.id "
			+ "FROM jobs "
			+ "WHERE jobs.removed = 0 AND jobs.sunny = 1 AND jobs.device_id = (SELECT device.id FROM device WHERE device.name = ? AND device.removed = 0)";
	
	/**
	 * Select Sunny Day Length 
	 */
	public final static String SELECT_SUNNY_DAY_LENGTH = "SELECT sunny_day.day, sunny_day.off_ts, sunny_day.on_ts"
			+ " FROM sunny_day ";
	
	public final static int DEVICE_RULE_TYPE_OFF = 1; 
	
	
	public final static int DEVICE_RULE_TYPE_ON = 2; 
	
	/**
	 * Select previous state of device from log
	 */
	public final static String SELECT_LOGGED_DEVICE = "SELECT max(log.ts), log.text FROM log WHERE removed = 0 AND log.device = ?";

	/**
	 * Insert new state of device
	 */
	public final static String INSERT_DEVICE_LOG = "INSERT INTO log (ts, device, text) VALUES (?,?,?)";


}






  