package zmq.io.net;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQException;


public class TestAddress {
    @Test
    public void testToNotResolvedToString() {
        Address addr = new Address("tcp", "google.com:90");
        String saddr = addr.toString();
        Assert.assertThat(saddr, CoreMatchers.is("tcp://google.com:90"));
    }

    @Test
    public void testResolvedToString() {
        Address addr = new Address("tcp", "google.com:90");
        addr.resolve(false);
        String resolved = addr.toString();
        Assert.assertTrue(resolved.matches("tcp://\\d+\\.\\d+\\.\\d+\\.\\d+:90"));
    }

    @Test(expected = ZMQException.class)
    public void testInvalid() {
        new Address("tcp", "ggglocalhostxxx:90").resolve(false);
    }
}

