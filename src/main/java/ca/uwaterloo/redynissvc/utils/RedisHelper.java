package ca.uwaterloo.redynissvc.utils;

import redis.clients.jedis.Jedis;

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
}
