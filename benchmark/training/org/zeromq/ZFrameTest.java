package org.zeromq;


import SocketType.PAIR;
import ZMQ.DONTWAIT;
import ZMQ.SNDMORE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


public class ZFrameTest {
    @Test
    public void testZFrameCreation() {
        ZFrame f = new ZFrame("Hello".getBytes());
        Assert.assertThat(f, CoreMatchers.notNullValue());
        Assert.assertThat(f.hasData(), CoreMatchers.is(true));
        Assert.assertThat(f.size(), CoreMatchers.is(5));
        f = new ZFrame();
        Assert.assertThat(f.hasData(), CoreMatchers.is(false));
        Assert.assertThat(f.size(), CoreMatchers.is(0));
    }

    @Test
    public void testZFrameEquals() {
        ZFrame f = new ZFrame("Hello".getBytes());
        ZFrame clone = f.duplicate();
        Assert.assertThat(clone, CoreMatchers.is(f));
    }

    @Test
    public void testSending() {
        ZContext ctx = new ZContext();
        Socket output = ctx.createSocket(PAIR);
        output.bind("inproc://zframe.test");
        Socket input = ctx.createSocket(PAIR);
        input.connect("inproc://zframe.test");
        // Send five different frames, test ZFRAME_MORE
        for (int i = 0; i < 5; i++) {
            ZFrame f = new ZFrame("Hello".getBytes());
            boolean rt = f.send(output, SNDMORE);
            Assert.assertThat(rt, CoreMatchers.is(true));
        }
        // Send same frame five times
        ZFrame f = new ZFrame("Hello".getBytes());
        for (int i = 0; i < 5; i++) {
            f.send(output, SNDMORE);
        }
        Assert.assertThat(f.size(), CoreMatchers.is(5));
        ctx.close();
    }

    @Test
    public void testCopyingAndDuplicating() {
        ZFrame f = new ZFrame("Hello");
        ZFrame copy = f.duplicate();
        Assert.assertThat(copy, CoreMatchers.is(f));
        f.destroy();
        Assert.assertThat(copy, CoreMatchers.is(CoreMatchers.not(f)));
        Assert.assertThat(copy.size(), CoreMatchers.is(5));
    }

    @Test
    public void testReceiving() {
        ZContext ctx = new ZContext();
        Socket output = ctx.createSocket(PAIR);
        output.bind("inproc://zframe.test");
        Socket input = ctx.createSocket(PAIR);
        input.connect("inproc://zframe.test");
        // Send same frame five times
        ZFrame f = new ZFrame("Hello".getBytes());
        for (int i = 0; i < 5; i++) {
            f.send(output, SNDMORE);
        }
        // Send END frame
        f = new ZFrame("NOT".getBytes());
        f.reset("END".getBytes());
        Assert.assertThat(f.strhex(), CoreMatchers.is("454E44"));
        f.send(output, 0);
        // Read and count until we receive END
        int frameNbr = 0;
        while (true) {
            f = ZFrame.recvFrame(input);
            frameNbr++;
            if (f.streq("END")) {
                f.destroy();
                break;
            }
        } 
        Assert.assertThat(frameNbr, CoreMatchers.is(6));
        f = ZFrame.recvFrame(input, DONTWAIT);
        Assert.assertThat(f, CoreMatchers.nullValue());
        ctx.close();
    }

    @Test
    public void testStringFrames() {
        ZContext ctx = new ZContext();
        Socket output = ctx.createSocket(PAIR);
        output.bind("inproc://zframe.test");
        Socket input = ctx.createSocket(PAIR);
        input.connect("inproc://zframe.test");
        ZFrame f1 = new ZFrame("Hello");
        Assert.assertThat(f1.getData().length, CoreMatchers.is(5));
        f1.send(output, 0);
        ZFrame f2 = ZFrame.recvFrame(input);
        Assert.assertThat(f2.hasData(), CoreMatchers.is(true));
        Assert.assertThat(f2.getData().length, CoreMatchers.is(5));
        Assert.assertThat(f2.streq("Hello"), CoreMatchers.is(true));
        Assert.assertThat(f2.toString(), CoreMatchers.is("Hello"));
        Assert.assertThat(f2, CoreMatchers.is(f1));
        ctx.close();
    }
}

