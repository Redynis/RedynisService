package ca.uwaterloo.redynissvc;

import org.junit.Assert;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetworkAddressTest
{
    @Test
    public void testGetHostName()
        throws UnknownHostException
    {
        String hostname = InetAddress.getLocalHost().getHostName();
        Assert.assertEquals(hostname, "scs555pc");
    }
}
