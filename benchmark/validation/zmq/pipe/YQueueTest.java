package zmq.pipe;


import ZMQ.CHARSET;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.Msg;


public class YQueueTest {
    @Test
    public void testReuse() {
        // yqueue has a first empty entry
        YQueue<Msg> p = new YQueue<Msg>(3);
        Msg m1 = new Msg(1);
        Msg m2 = new Msg(2);
        Msg m3 = new Msg(3);
        Msg m4 = new Msg(4);
        Msg m5 = new Msg(5);
        Msg m6 = new Msg(6);
        Msg m7 = new Msg(7);
        m7.put("1234567".getBytes(CHARSET));
        p.push(m1);
        Assert.assertThat(p.backPos(), CoreMatchers.is(1));
        p.push(m2);// might allocated new chunk

        p.push(m3);
        Assert.assertThat(p.backPos(), CoreMatchers.is(3));
        Assert.assertThat(p.frontPos(), CoreMatchers.is(0));
        p.pop();
        p.pop();
        p.pop();// offer the old chunk

        Assert.assertThat(p.frontPos(), CoreMatchers.is(3));
        p.push(m4);
        p.push(m5);// might reuse the old chunk

        p.push(m6);
        Assert.assertThat(p.backPos(), CoreMatchers.is(0));
    }
}

