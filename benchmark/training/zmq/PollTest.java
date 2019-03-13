package zmq;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class PollTest {
    @FunctionalInterface
    static interface TxRx<R> {
        R apply(ByteBuffer bb) throws IOException;
    }

    @Test
    public void testPollTcp() throws IOException {
        ServerSocketChannel server = ServerSocketChannel.open();
        Assert.assertThat(server, CoreMatchers.notNullValue());
        server.configureBlocking(true);
        server.bind(null);
        InetSocketAddress addr = ((InetSocketAddress) (server.socket().getLocalSocketAddress()));
        SocketChannel in = SocketChannel.open();
        Assert.assertThat(in, CoreMatchers.notNullValue());
        in.configureBlocking(false);
        boolean rc = in.connect(addr);
        Assert.assertThat(rc, CoreMatchers.is(false));
        SocketChannel out = server.accept();
        out.configureBlocking(false);
        rc = in.finishConnect();
        Assert.assertThat(rc, CoreMatchers.is(true));
        try {
            assertPoller(in, out, out::write, in::read);
        } finally {
            in.close();
            out.close();
            server.close();
        }
    }

    @Test
    public void testPollPipe() throws IOException {
        Pipe pipe = Pipe.open();
        Assert.assertThat(pipe, CoreMatchers.notNullValue());
        Pipe.SinkChannel sink = pipe.sink();
        Assert.assertThat(sink, CoreMatchers.notNullValue());
        sink.configureBlocking(false);
        Pipe.SourceChannel source = pipe.source();
        Assert.assertThat(source, CoreMatchers.notNullValue());
        source.configureBlocking(false);
        try {
            assertPoller(source, sink, sink::write, source::read);
        } finally {
            sink.close();
            source.close();
        }
    }

    @Test
    public void testPollUdp() throws IOException {
        DatagramChannel in = DatagramChannel.open();
        Assert.assertThat(in, CoreMatchers.notNullValue());
        in.configureBlocking(false);
        in.socket().bind(null);
        InetSocketAddress addr = ((InetSocketAddress) (in.socket().getLocalSocketAddress()));
        DatagramChannel out = DatagramChannel.open();
        Assert.assertThat(out, CoreMatchers.notNullValue());
        out.configureBlocking(false);
        out.connect(addr);
        try {
            assertPoller(in, out, ( bb) -> out.send(bb, addr), in::receive);
        } finally {
            in.close();
            out.close();
        }
    }
}

