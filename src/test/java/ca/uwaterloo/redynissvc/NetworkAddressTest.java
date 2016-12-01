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
        System.out.println(InetAddress.getLocalHost().getHostName());
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }
}
