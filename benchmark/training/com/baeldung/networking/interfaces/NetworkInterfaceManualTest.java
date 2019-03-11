package com.baeldung.networking.interfaces;


import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class NetworkInterfaceManualTest {
    @Test
    public void givenName_whenReturnsNetworkInterface_thenCorrect() throws SocketException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Assert.assertNotNull(nif);
    }

    @Test
    public void givenInExistentName_whenReturnsNull_thenCorrect() throws SocketException {
        NetworkInterface nif = NetworkInterface.getByName("inexistent_name");
        Assert.assertNull(nif);
    }

    @Test
    public void givenIP_whenReturnsNetworkInterface_thenCorrect() throws SocketException, UnknownHostException {
        byte[] ip = new byte[]{ 127, 0, 0, 1 };
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByAddress(ip));
        Assert.assertNotNull(nif);
    }

    @Test
    public void givenHostName_whenReturnsNetworkInterface_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getByName("localhost"));
        Assert.assertNotNull(nif);
    }

    @Test
    public void givenLocalHost_whenReturnsNetworkInterface_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
        Assert.assertNotNull(nif);
    }

    @Test
    public void givenLoopBack_whenReturnsNetworkInterface_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByInetAddress(InetAddress.getLoopbackAddress());
        Assert.assertNotNull(nif);
    }

    @Test
    public void givenIndex_whenReturnsNetworkInterface_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByIndex(0);
        Assert.assertNotNull(nif);
    }

    @Test
    public void givenInterface_whenReturnsInetAddresses_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Enumeration<InetAddress> addressEnum = nif.getInetAddresses();
        InetAddress address = addressEnum.nextElement();
        Assert.assertEquals("127.0.0.1", address.getHostAddress());
    }

    @Test
    public void givenInterface_whenReturnsInterfaceAddresses_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        List<InterfaceAddress> addressEnum = nif.getInterfaceAddresses();
        InterfaceAddress address = addressEnum.get(0);
        InetAddress localAddress = address.getAddress();
        InetAddress broadCastAddress = address.getBroadcast();
        Assert.assertEquals("127.0.0.1", localAddress.getHostAddress());
        Assert.assertEquals("127.255.255.255", broadCastAddress.getHostAddress());
    }

    @Test
    public void givenInterface_whenChecksIfLoopback_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Assert.assertTrue(nif.isLoopback());
    }

    @Test
    public void givenInterface_whenChecksIfUp_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Assert.assertTrue(nif.isUp());
    }

    @Test
    public void givenInterface_whenChecksIfPointToPoint_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Assert.assertFalse(nif.isPointToPoint());
    }

    @Test
    public void givenInterface_whenChecksIfVirtual_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Assert.assertFalse(nif.isVirtual());
    }

    @Test
    public void givenInterface_whenChecksMulticastSupport_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        Assert.assertTrue(nif.supportsMulticast());
    }

    @Test
    public void givenInterface_whenGetsMacAddress_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("lo");
        byte[] bytes = nif.getHardwareAddress();
        Assert.assertNotNull(bytes);
    }

    @Test
    public void givenInterface_whenGetsMTU_thenCorrect() throws SocketException, UnknownHostException {
        NetworkInterface nif = NetworkInterface.getByName("net0");
        int mtu = nif.getMTU();
        Assert.assertEquals(1500, mtu);
    }
}

