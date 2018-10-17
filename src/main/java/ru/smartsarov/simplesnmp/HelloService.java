package ru.smartsarov.simplesnmp;

import java.time.Instant;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import ru.smartsarov.simplesnmp.job.JobDbBean;
import ru.smartsarov.simplesnmp.job.JobsTableAgregator;



@Path("/")
@Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
public class HelloService
{
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
		return Response.status(Response.Status.OK).entity(JobsTableAgregator.createTableJob()).build();
    }
	
	
	/*
	 * Inserting new job in scheduler
	 * 
	 * */
	@GET
	@Path("/scheduler/setjob")
    public Response create(
    @QueryParam("job_ts") long job_ts,
    @QueryParam("command") String command,
    @QueryParam("user") String user,
    @QueryParam("device_id") int device_id)
    {
        JobsTableAgregator.setJobToDb(new JobDbBean(0,job_ts, command, user, device_id, Instant.now().getEpochSecond(),false,false));
		return Response.status(Response.Status.OK).entity("insert job success").build();
    }
	
	/*
	 * Show jobs between two timestamps
	 * 
	 * */
	@GET
	@Path("/scheduler/showjobs")
    public Response create(
    @QueryParam("min_ts") long min_ts,
    @QueryParam("max_ts") long max_ts)
    { 
		return Response.status(Response.Status.OK).entity( JobsTableAgregator.getJobsFrom(min_ts, max_ts)).build();
    }
	
	@GET
	@Path("/scheduler/removejob")
    public Response removejob(
    @QueryParam("id") int id)
    {	
		return Response.status(Response.Status.OK).entity("removed: "+JobsTableAgregator.removeJob(id)+ " job(s)").build();
    }
	
	@GET
	@Path("/scheduler/check")
    public Response check()
    {	
		JobsTableAgregator.getCurrentJob(60);
		return Response.status(Response.Status.OK).entity("Ok").build();
    }
	
	
	
}