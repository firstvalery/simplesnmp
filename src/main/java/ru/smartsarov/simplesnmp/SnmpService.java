package ru.smartsarov.simplesnmp;




import java.io.InputStream;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import ru.smartsarov.simplesnmp.job.JobConstants;
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
	
	/**
	 * Inserting new job in scheduler
	 * 
	 */
	@GET
	@Path("/scheduler/job/add")
    public Response create(
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
    public Response create(
    @QueryParam("min_ts") long min_ts,
    @QueryParam("max_ts") long max_ts)
    { 	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.getJobsFrom(min_ts, max_ts)).build(); 
		} catch (ClassNotFoundException | SQLException e) {
			return Response.status(Response.Status.OK).entity(e.toString()).build(); 
		}
    }
	
	@GET
	@Path("/scheduler/job/remove")
    public Response removejob(
    @QueryParam("id") int id)
    {	
		try {
			return Response.status(Response.Status.OK).entity(JobsTableAgregator.removeJob(id)).build(); 
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
	
	
	
}