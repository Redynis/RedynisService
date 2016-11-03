package ca.uwaterloo.redynissvc;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/redisdata")
public class RedynisService
{
    @GET()
	public String GetRedisData()
	{
        return "Hello";
    }
}