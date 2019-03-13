package org.csploit.android.helpers;


import java.net.InetAddress;
import junit.framework.Assert;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;

import static org.junit.Assert.assertThat;


/**
 * Test NetworkHelper class
 */
public class NetworkHelperTest extends TestCase {
    public void testGetOUICode() throws Exception {
        byte[] address = new byte[]{ 1, 2, 3 };
        int fromString = NetworkHelper.getOUICode("010203");
        int fromMAC = NetworkHelper.getOUICode(address);
        Assert.assertEquals(((fromString + " differs from ") + fromMAC), fromString, fromMAC);
    }

    public void testComapreInetAddress() throws Exception {
        InetAddress a;
        InetAddress b;
        a = InetAddress.getLocalHost();
        b = InetAddress.getByAddress("127.0.0.1", new byte[]{ 127, 0, 0, 1 });
        org.junit.Assert.assertThat(a, CoreMatchers.is(b));
        int res = NetworkHelper.compareInetAddresses(a, b);
        org.junit.Assert.assertThat(res, CoreMatchers.is(0));
        b = InetAddress.getByAddress(new byte[]{ ((byte) (192)), ((byte) (168)), 1, 1 });
        org.junit.Assert.assertThat(a, CoreMatchers.not(b));
        res = NetworkHelper.compareInetAddresses(a, b);
        assertThat(((a + " should be less than ") + b), (res < 0), CoreMatchers.is(true));
    }
}

