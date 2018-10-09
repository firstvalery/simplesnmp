package ru.smartsarov.simplesnmp;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/")
@Produces(MediaType.TEXT_HTML + ";charset=UTF-8")
public class HelloService
{
/*	@GET
	@Path("/")
    public Response index()
    {
		InputStream is = this.getClass().getResourceAsStream("/static/index.html");
    	return Response.status(Response.Status.OK).entity(is).build();
    }*/
	
	@GET
	@Path("/{cmd}")
    public Response getMsg(@PathParam("cmd") String cmd)
    {   
		String rs = cmd;
		switch (cmd.toLowerCase()) {
		case "on": ERDSndRcv.erdSendOn();
			break;
		case "off": ERDSndRcv.erdSendOff();
		    break;
		case "state": rs = ERDSndRcv.erdGetInfo();
		    break;
		default:   
			rs+=": wrong request";
			
		}
		return Response.status(Response.Status.OK).entity(rs).build();
    }
}