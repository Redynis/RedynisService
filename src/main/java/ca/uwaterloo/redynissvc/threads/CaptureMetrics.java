package ca.uwaterloo.redynissvc.threads;

import ca.uwaterloo.redynissvc.serviceobjects.UsageMetric;
import ca.uwaterloo.redynissvc.utils.Constants;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AllArgsConstructor
public class CaptureMetrics extends Thread
{
    private String redisKey;
    private static Logger log = LogManager.getLogger("RedynisServiceLogger");

    @Override
    public void run()
    {
        try
        {
            log.debug("Capturing metrics");

            Jedis jedis = new Jedis("localhost", 6380);
            String usageMetricsString = jedis.get(redisKey);

            String hostname = InetAddress.getLocalHost().getHostName();

            if (null == usageMetricsString)
            {
                Integer totalAccessCount = 1;

                List<String> hosts = new ArrayList<>();
                hosts.add(hostname);

                Map<String, Integer> hostAccesses = new HashMap<>();
                hostAccesses.put(hostname, 1);

                jedis.set(
                    redisKey,
                    Constants.MAPPER.writeValueAsString(new UsageMetric(totalAccessCount, hosts, hostAccesses))
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
                        hostAccesses
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
