package ca.uwaterloo.redynissvc;

import ca.uwaterloo.redynissvc.utils.RedisHelper;
import org.junit.Assert;
import org.junit.Test;

public class GetValue
{
    @Test
    public void testGetValue()
    {
        RedisHelper.init("localhost", 6379);
        RedisHelper.getValue("vineet");
    }
}
