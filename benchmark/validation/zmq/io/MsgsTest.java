package zmq.io;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ;
import zmq.Msg;


public class MsgsTest {
    @Test
    public void testPutMax() {
        assertPutStartsWith(255);
    }

    @Test
    public void testPutMiddle() {
        assertPutStartsWith(127);
    }

    @Test
    public void testPutMiddlePlusOne() {
        assertPutStartsWith(128);
    }

    @Test
    public void testPutZero() {
        assertPutStartsWith(0);
    }

    @Test
    public void testPutEncodesWithLength() {
        String test = "test1";
        Msg msg = new Msg(((test.length()) + 1));
        Msgs.put(msg, test);
        String read = new String(msg.data(), ZMQ.CHARSET);
        Assert.assertThat(read, CoreMatchers.is("\u0005test1"));
    }
}

