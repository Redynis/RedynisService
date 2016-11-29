package ca.uwaterloo.redynissvc.utils;

import ca.uwaterloo.redynissvc.beans.ServiceConfig;
import ca.uwaterloo.redynissvc.beans.UsageMetric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

public class DataLocator
{
    private Jedis jedis;
    private static DataLocator instance;

    private static Logger log = LogManager.getLogger(DataLocator.class);

    public static DataLocator getInstance(ServiceConfig serviceConfig)
        throws UnknownHostException
    {
        if (null == instance)
        {
            Jedis jedisInstance =
                new Jedis(
                    InetAddress.getLocalHost().getCanonicalHostName(),
                    serviceConfig.getMetadataLayerPort()
                );
            instance = new DataLocator(jedisInstance);
        }

        return instance;
    }

    private DataLocator(Jedis jedis)
    {
        this.jedis = jedis;
    }

    public String locateDataHost(String key)
        throws IOException, InterruptedException
    {
        String hostname = null;
        String localHostname = InetAddress.getLocalHost().getCanonicalHostName();

        Set<String> hosts = locateDataHosts(key);
        if (null != hosts)
        {
            if (hosts.contains(localHostname))
            {
                hostname = localHostname;
            }
            else
            {
                Thread.sleep(Constants.INDUCED_LATENCY_MILLISEC);  // inducing false latency to simulate remote node
                hostname = hosts.iterator().next();
            }
        }

        log.debug("Host where " + key + " is located: " + hostname);
        return hostname;
    }

    public Set<String> locateDataHosts(String key)
        throws IOException
    {
        Set<String> hosts = null;

        String metadata = jedis.get(key);
        if (null != metadata)
        {
            UsageMetric usageMetric = Constants.MAPPER.readValue(metadata, UsageMetric.class);
            hosts = usageMetric.getHosts();
        }

        return hosts;
    }
}
