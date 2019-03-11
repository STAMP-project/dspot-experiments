package org.wildfly.clustering.server.singleton.election;


import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.wildfly.clustering.group.Node;
import org.wildfly.clustering.singleton.election.Preference;
import org.wildfly.clustering.singleton.election.SocketAddressPreference;


public class SocketAddressPreferenceTestCase {
    @Test
    public void test() throws UnknownHostException {
        InetSocketAddress preferredAddress = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 1);
        InetSocketAddress otherAddress1 = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 2);
        InetSocketAddress otherAddress2 = new InetSocketAddress(InetAddress.getByName("127.0.0.2"), 1);
        Preference preference = new SocketAddressPreference(preferredAddress);
        Node preferredNode = Mockito.mock(Node.class);
        Node otherNode1 = Mockito.mock(Node.class);
        Node otherNode2 = Mockito.mock(Node.class);
        Mockito.when(preferredNode.getSocketAddress()).thenReturn(preferredAddress);
        Mockito.when(otherNode1.getSocketAddress()).thenReturn(otherAddress1);
        Mockito.when(otherNode2.getSocketAddress()).thenReturn(otherAddress2);
        Assert.assertTrue(preference.preferred(preferredNode));
        Assert.assertFalse(preference.preferred(otherNode1));
        Assert.assertFalse(preference.preferred(otherNode2));
    }
}

