package ca.uwaterloo.redynissvc;

import ca.uwaterloo.redynissvc.exceptions.InternalServerError;
import ca.uwaterloo.redynissvc.serviceobjects.KeyValue;
import ca.uwaterloo.redynissvc.serviceobjects.PostSuccess;
import ca.uwaterloo.redynissvc.threads.CaptureMetrics;
import ca.uwaterloo.redynissvc.utils.Constants;
import ca.uwaterloo.redynissvc.utils.RedisHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/redis")
@Produces("application/json")
public class RedynisService
{
    private static Logger log = LogManager.getLogger("RedynisServiceLogger");

    @GET()
	public Response GetRedisData(
        @QueryParam("key") String redisKey
    )
        throws JsonProcessingException
    {
        log.debug("Received GET request");

        InternalServerError error;
        if (null == redisKey)
        {
            error = new InternalServerError("Key cannot be null/empty", this.getClass().getName());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Constants.MAPPER.writeValueAsString(error)).build();
        }

        CaptureMetrics captureMetrics = new CaptureMetrics(redisKey);
        captureMetrics.start();

        RedisHelper redisHelper = RedisHelper.getInstance();
        String redisValue = redisHelper.getValue(redisKey);

        KeyValue keyValue = new KeyValue(redisKey, redisValue);
        return Response.ok(Constants.MAPPER.writeValueAsString(keyValue)).build();
    }

    @POST
    public Response SetRedisData(
        @QueryParam("key") String redisKey,
        @QueryParam("value") String redisValue
    )
        throws JsonProcessingException
    {
        log.debug("Received POST request");

        InternalServerError error;
        if (null == redisKey)
        {
            error = new InternalServerError("Key cannot be null/empty", this.getClass().getName());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Constants.MAPPER.writeValueAsString(error)).build();
        }
        if (null == redisValue)
        {
            error = new InternalServerError("Value cannot be null/empty", this.getClass().getName());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Constants.MAPPER.writeValueAsString(error)).build();
        }


        RedisHelper redisHelper = RedisHelper.getInstance();
        redisHelper.setValue(redisKey, redisValue);

        return Response.ok(Constants.MAPPER.writeValueAsString(new PostSuccess(true))).build();
    }
}
