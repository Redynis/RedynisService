package ca.uwaterloo.redynissvc.threads;

import ca.uwaterloo.redynissvc.beans.ServiceConfig;
import ca.uwaterloo.redynissvc.beans.UsageMetric;
import ca.uwaterloo.redynissvc.utils.Constants;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.util.*;


@AllArgsConstructor
public class CaptureMetrics extends Thread
{
    private ServiceConfig serviceConfig;
    private String redisKey;
    private static Logger log = LogManager.getLogger(CaptureMetrics.class);

    @Override
    public void run()
    {
        try
        {
            log.debug("Capturing metrics");

            Jedis jedis = new Jedis(serviceConfig.getMetadataLayerHost(), serviceConfig.getMetadataLayerPort());
            String usageMetricsString = jedis.get(redisKey);

            String hostname = InetAddress.getLocalHost().getHostName();

            if (null == usageMetricsString)
            {
                Integer totalAccessCount = 1;

                Set<String> hosts = new HashSet<>();
                hosts.add(hostname);

                Map<String, Integer> hostAccesses = new HashMap<>();
                hostAccesses.put(hostname, 1);

                jedis.set(
                    redisKey,
                    Constants.MAPPER.writeValueAsString(
                        new UsageMetric(totalAccessCount, hosts, hostAccesses, new Date())
                    )
                );
            }
            else
            {
                UsageMetric oldUsageMetric = Constants.MAPPER.readValue(usageMetricsString, UsageMetric.class);

                Map<String, Integer> hostAccesses = oldUsageMetric.getHostAccesses();
                Integer hostAccessCount =
                    hostAccesses.containsKey(hostname) ? hostAccesses.get(hostname) + 1 : 1;
                hostAccesses.put(hostname, hostAccessCount);

                UsageMetric newUsageMetric =
                    new UsageMetric(
                        oldUsageMetric.getTotalAccessCount() + 1,
                        oldUsageMetric.getHosts(),
                        hostAccesses,
                        new Date()
                    );

                jedis.set(
                    redisKey,
                    Constants.MAPPER.writeValueAsString(newUsageMetric)
                );
            }
        }
        catch (Exception e)
        {
            log.error("CaptureMetrics failed. ", e);
        }
    }
}
