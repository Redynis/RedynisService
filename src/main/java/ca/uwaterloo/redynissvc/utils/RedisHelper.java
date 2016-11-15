package ca.uwaterloo.redynissvc.utils;

import redis.clients.jedis.Jedis;

public class RedisHelper
{
    private Jedis jedis;

    public RedisHelper(String host, Integer port)
    {
        this.jedis = new Jedis(host, port);
    }

    public String getValue(String key)
    {
        return jedis.get(key);
    }

    public void setValue(String key, String value)
    {
        jedis.set(key, value);
    }
}
