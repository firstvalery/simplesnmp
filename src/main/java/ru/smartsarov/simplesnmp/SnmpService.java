package ru.smartsarov.simplesnmp;





import java.io.InputStream;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ru.smartsarov.simplesnmp.job.DeviceTable;
import ru.smartsarov.simplesnmp.job.JobConstants;
import ru.smartsarov.simplesnmp.job.JobTable;
import ru.smartsarov.simplesnmp.job.JobsTableAgregator;
import ru.smartsarov.simplesnmp.job.UsersTable;




@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SnmpService
{
	@GET
	@Path("/")
	@Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
    public Response index()
    {
		InputStream is = this.getClass().getResourceAsStream("/static/index.html");
    	return Response.status(Response.Status.OK).entity(is).build();
    }
	
	
	@GET
	@Path("/on")
    public Response getMsgOn()
    {
		return Response.status(Response.Status.OK).entity(ERDSndRcv.erdSendOn()).build();
    }
	
	@GET
	@Path("/off")
    public Response getMsgOff()
    {
		return Response.status(Response.Status.OK).entity(ERDSndRcv.erdSendOff()).build();
    }
	
	@GET
	@Path("/state")
    public Response getMsgState()
    {
		return Response.status(Response.Status.OK).entity(ERDSndRcv.erdGetInfo()).build();
    }
	
	@GET
	@Path("/scheduler/create_table")
    public Response createTable()
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.createTables()).build();
		} catch (ClassNotFoundException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build();
		} catch (SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build();
		}
    }
	
	
	
	
	
	

	

	
	@GET
	@Path("/scheduler/users/add")
    public Response addUser(@QueryParam("username") String userName)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator
					.insertNewRecord(true, UsersTable.class, JobConstants.INSERT_USER , JobConstants.SELECT_BY_USER_NAME, userName, userName)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}	
    }
	@GET
	@Path("/scheduler/users/show")
    public Response showUsers()
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.getJsonSelect(UsersTable.class, JobConstants.SELECT_USERS)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}	
    }
	
	@GET
	@Path("/scheduler/users/remove")
    public Response removeUser(
    @QueryParam("user_name") String user_name)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					removeElement(JobConstants.MARK_FOR_REMOVING_USER_BY_NAME,user_name)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/device/add")
    public Response addDevice(
    		@QueryParam("community") String community,
    		@QueryParam("name") String name,
    		@QueryParam("ip") String ip)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator
					.insertNewDevice(ip, community, name)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}	
    }
	
	@GET
	@Path("/scheduler/device/remove")
    public Response removeDevice(
    @QueryParam("name") String name)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					removeElement(JobConstants.MARK_FOR_REMOVING_DEVICE_BY_NAME,name)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/device/show")
    public Response showDevices()
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.getJsonSelect(DeviceTable.class, JobConstants.SELECT_DEVICES)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}	
    }	
	
	
	
	
	/**
	 * Inserting new job in scheduler
	 * 
	 */
	@GET
	@Path("/scheduler/job/add")
    public Response createJob(
    @QueryParam("job_ts") long job_ts,
    @QueryParam("command") int command,
    @QueryParam("user") String user,
    @QueryParam("device_name") String device_name)
    {
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.insertNewJob(job_ts, command, user, device_name)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	/**
	 * Show jobs between two timestamps
	 * 
	 */
	@GET
	@Path("/scheduler/job/show")
    public Response showJobs(
    @QueryParam("min_ts") long min_ts,
    @QueryParam("max_ts") long max_ts)
    { 	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					getJsonSelect(JobTable.class, JobConstants.SELECT_JOBS_BETWEEN, min_ts, max_ts)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/job/remove")
    public Response removeJob(
    @QueryParam("id") int id)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					removeElement(JobConstants.MARK_FOR_REMOVING_JOBS_BY_ID,id)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/job/check")
    public Response check() throws ClassNotFoundException, SQLException
    {	
		JobsTableAgregator.getCurrentJob(JobConstants.DELTA_FOR_CHECK);
		return Response.status(Response.Status.OK).entity("{}").build();
    }
	
	@GET
	@Path("/scheduler/sunny/insert")
    public Response sunnyInsert() 
    {	try {
    	return Response.status(Response.Status.OK).entity(JobsTableAgregator.getSunnyFile(Props.get().getProperty("simplesnmp.sunny_file_path", "c:/conf/simplesnmp/scheduler.csv"))).build();
	} catch (Exception e) {
		return Response.status(Response.Status.OK).entity(e.toString()).build(); 
	}
    }
	
	@GET
	@Path("/scheduler/sunnyjob/generate")
    public Response generateSunny(
    	    @QueryParam("user") String user,
    	    @QueryParam("device") String device) throws ClassNotFoundException, SQLException
    {	
		
		try {
	    	return Response.status(Response.Status.OK).entity(JobsTableAgregator.setSunnyScheduleForDevice(user,device)).build();
		} catch (Exception e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/sunnyjob/remove")
    public Response removeSunny(
    @QueryParam("device") String device)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					removeElement(JobConstants.MARK_FOR_REMOVING_JOBS_SUNNY,device)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/device_rule/insert")
    public Response deviceRuleInsert(
    @QueryParam("device_name") String device_name,
    @QueryParam("work_t") String workT,
    @QueryParam("weekend_t") String weekendT,
    @QueryParam("rule_type") int ruleType)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					insertDeviceRule(device_name, workT, weekendT, ruleType)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/device_rule/remove")
    public Response deviceRuleRemove(
    @QueryParam("id") int id)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.
					removeElement(JobConstants.MARK_FOR_REMOVING_DEVICE_RULE_BY_ID,id)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/device_rule/show")
    public Response deviceRulesShow(
    @QueryParam("device_name") String  deviceName)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.showDeviceRules(deviceName)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	

}