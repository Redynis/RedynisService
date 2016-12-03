package ca.uwaterloo.redynissvc.utils;

import ca.uwaterloo.redynissvc.beans.ServiceConfig;
import ca.uwaterloo.redynissvc.beans.UsageMetric;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Set;

public class DataLocator
{
    private static Logger log = LogManager.getLogger(DataLocator.class);

    public static String locateDataHost(String key, ServiceConfig serviceConfig)
        throws IOException, InterruptedException
    {
        String hostname = null;
        String localHostname = InetAddress.getLocalHost().getHostAddress();

        Set<String> hosts = locateDataHosts(key, serviceConfig);
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

    public static Set<String> locateDataHosts(String key, ServiceConfig serviceConfig)
        throws IOException
    {
        Set<String> hosts = null;
        Jedis jedis = new Jedis(serviceConfig.getMetadataLayerHost(), serviceConfig.getMetadataLayerPort());

        String metadata = jedis.get(key);
        if (null != metadata)
        {
            UsageMetric usageMetric = Constants.MAPPER.readValue(metadata, UsageMetric.class);
            hosts = usageMetric.getHosts();
        }

        jedis.close();

        return hosts;
    }
}
