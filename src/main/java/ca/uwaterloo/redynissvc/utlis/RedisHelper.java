package ca.uwaterloo.redynissvc.utlis;

import redis.clients.jedis.Jedis;

public class RedisHelper
{
    private static RedisHelper instance;
    private Jedis jedis;

    public static RedisHelper getInstance()
    {
        if(null == instance)
        {
            instance = new RedisHelper("localhost", 6379);
        }
        return instance;
    }

    private RedisHelper(String host, Integer port)
    {
        jedis = new Jedis(host, port);
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
