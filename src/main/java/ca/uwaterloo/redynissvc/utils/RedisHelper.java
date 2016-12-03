package ca.uwaterloo.redynissvc.utils;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisHelper
{
    private static Jedis jedis;

    public static void init(String host, Integer port)
    {
        jedis = new Jedis(host, port);
    }

    public static String getValue(String key)
    {
        return jedis.get(key);
    }

    public static void setValue(String key, String value)
    {
        jedis.set(key, value);
    }

    public synchronized static void
    setValueAtMultipleHosts(String redisKey, String redisValue, Set<String> hosts, Integer dataLayerPort)
        throws InterruptedException
    {
        Jedis jedis;
        for (String host : hosts)
        {
            Thread.sleep(Constants.INDUCED_LATENCY_MILLISEC); // inducing artificial latency
            jedis = new Jedis(host, dataLayerPort);
            jedis.set(redisKey, redisValue);
        }
    }
}
