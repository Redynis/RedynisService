package ca.uwaterloo.redynissvc;

import ca.uwaterloo.redynissvc.beans.KeyValue;
import ca.uwaterloo.redynissvc.beans.PostSuccess;
import ca.uwaterloo.redynissvc.beans.ServiceConfig;
import ca.uwaterloo.redynissvc.beans.UsageMetric;
import ca.uwaterloo.redynissvc.exceptions.InternalServerError;
import ca.uwaterloo.redynissvc.threads.CaptureMetrics;
import ca.uwaterloo.redynissvc.utils.ConfigHelper;
import ca.uwaterloo.redynissvc.utils.Constants;
import ca.uwaterloo.redynissvc.utils.DataLocator;
import ca.uwaterloo.redynissvc.utils.RedisHelper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

@Path("/redis")
@Produces("application/json")
public class RedynisService extends Application
{
    private static Logger log = LogManager.getLogger(RedynisService.class);
    private ServiceConfig serviceConfig;
    private HttpClient httpClient = HttpClientBuilder.create().build();

    public RedynisService(@Context ServletContext context)
        throws IOException
    {
        String configFilePath = context.getInitParameter(Constants.CONFIGFILE_PARAM);
        serviceConfig = ConfigHelper.getInstance(configFilePath).getServiceConfig();
        RedisHelper.init(InetAddress.getLocalHost().getHostAddress(), serviceConfig.getDataLayerPort());
    }

    @GET
	public Response GetRedisData(
        @QueryParam(Constants.KEY_PARAM) String redisKey
    )
        throws IOException, InterruptedException
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

        String host = DataLocator.locateDataHost(redisKey, serviceConfig);

        String redisValue = null;
        if(null != host)
        {
            Jedis jedis = new Jedis(host, serviceConfig.getDataLayerPort());
            redisValue = jedis.get(redisKey);
        }

        KeyValue keyValue = new KeyValue(redisKey, redisValue);
        return Response.ok(Constants.MAPPER.writeValueAsString(keyValue)).build();
    }

    @POST
    public Response SetRedisData(
        @QueryParam(Constants.KEY_PARAM) String redisKey,
        @QueryParam(Constants.VALUE_PARAM) String redisValue
    )
        throws IOException, InterruptedException
    {
        log.debug("Received POST request");

        InternalServerError error;
        if (null == redisKey)
        {
            String msg = "Key cannot be null/empty";
            log.error(msg);
            error = new InternalServerError(msg, this.getClass().getName());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Constants.MAPPER.writeValueAsString(error)).build();
        }
        if (null == redisValue)
        {
            String msg = "Value cannot be null/empty";
            log.error(msg);
            error = new InternalServerError(msg, this.getClass().getName());
            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Constants.MAPPER.writeValueAsString(error)).build();
        }

        Set<String> hosts = DataLocator.locateDataHosts(redisKey, serviceConfig);

        log.debug("Hosts are " + hosts);

        if (null == hosts)
        {
            log.debug("New key being posted");
            Integer totalAccessCount = 0;
            hosts = new HashSet<>();
            hosts.add(InetAddress.getLocalHost().getHostAddress());


            UsageMetric usageMetric =
                new UsageMetric(totalAccessCount, hosts, new HashMap<String, Integer>(), new Date());
            RedisHelper.setValue(redisKey, Constants.MAPPER.writeValueAsString(usageMetric));
        }
        else
        {
            log.debug("Hosts with key: " + hosts);
            if (hosts.size() == 1 && hosts.iterator().next().equals(InetAddress.getLocalHost().getHostAddress()))
            {
                log.debug("Setting value locally, (1 owner) " + hosts);
                RedisHelper.setValue(redisKey, redisValue);
            }
            else if (serviceConfig.getMasterPropagator().equals(InetAddress.getLocalHost().getHostAddress()))
            {
                log.debug("Setting value locally, (master) " + hosts);
                RedisHelper.setValueAtMultipleHosts(redisKey, redisValue, hosts, serviceConfig.getDataLayerPort());
            }
            else
            {
                log.debug("Setting value at multiple hosts " + hosts);
                Thread.sleep(Constants.INDUCED_LATENCY_MILLISEC); // inducing artificial latency
                HttpPost post =
                    new HttpPost(
                        Constants.SERVICE_ENDPOINT
                            .replaceAll(Constants.KEY_PLACEHOLDER, redisKey)
                            .replaceAll(Constants.VALUE_PLACEHOLDER, redisValue)
                    );

                HttpResponse response = httpClient.execute(post);
                log.debug(EntityUtils.toString(response.getEntity()));
                assert response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
            }
        }

        return Response.ok(Constants.MAPPER.writeValueAsString(new PostSuccess(true))).build();
    }
}
