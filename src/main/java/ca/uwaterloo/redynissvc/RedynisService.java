package ca.uwaterloo.redynissvc;

import ca.uwaterloo.redynissvc.beans.ServiceConfig;
import ca.uwaterloo.redynissvc.exceptions.InternalServerError;
import ca.uwaterloo.redynissvc.beans.KeyValue;
import ca.uwaterloo.redynissvc.beans.PostSuccess;
import ca.uwaterloo.redynissvc.threads.CaptureMetrics;
import ca.uwaterloo.redynissvc.utils.ConfigHelper;
import ca.uwaterloo.redynissvc.utils.Constants;
import ca.uwaterloo.redynissvc.utils.DataLocator;
import ca.uwaterloo.redynissvc.utils.RedisHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.servlet.ServletContext;
import java.io.IOException;

@Path("/redis")
@Produces("application/json")
public class RedynisService extends Application
{
    private static Logger log = LogManager.getLogger(RedynisService.class);
    private ServiceConfig serviceConfig;

    public RedynisService(@Context ServletContext context)
        throws IOException
    {
        String configFilePath = context.getInitParameter(Constants.CONFIGFILE_PARAM);
        serviceConfig = ConfigHelper.getInstance(configFilePath).getServiceConfig();
    }

    @GET
	public Response GetRedisData(
        @QueryParam("key") String redisKey
    )
        throws IOException
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

        CaptureMetrics captureMetrics = new CaptureMetrics(serviceConfig, redisKey);
        captureMetrics.start();

        DataLocator dataLocator = DataLocator.getInstance(serviceConfig);
        String host = dataLocator.locateDataHost(redisKey);

        String redisValue = null;
        if(null != host)
        {
            RedisHelper redisHelper = new RedisHelper(host, serviceConfig.getDataLayerPort());
            redisValue = redisHelper.getValue(redisKey);
        }

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

        RedisHelper redisHelper = new RedisHelper(serviceConfig.getDataLayerHost(), serviceConfig.getDataLayerPort());
        redisHelper.setValue(redisKey, redisValue);

        return Response.ok(Constants.MAPPER.writeValueAsString(new PostSuccess(true))).build();
    }
}
