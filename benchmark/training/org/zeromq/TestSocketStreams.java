package org.zeromq;


import SocketType.PULL;
import SocketType.PUSH;
import ZMQ.Context;
import ZMQ.Socket;
import java.io.IOException;
import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Isa Hekmatizadeh
 */
public class TestSocketStreams {
    @Test
    public void testRecvStream() throws IOException {
        int port = Utils.findOpenPort();
        try (final ZMQ.Context ctx = new ZMQ.Context(1);final ZMQ.Socket pull = ctx.socket(PULL);final ZMQ.Socket push = ctx.socket(PUSH)) {
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://127.0.0.1:" + port));
            final byte[] expected = new byte[]{ 17, 34, 51 };
            push.send(expected);
            Optional<byte[]> first = pull.recvStream().peek(System.out::print).findFirst();
            System.out.println();
            Assert.assertTrue(first.isPresent());
            Assert.assertArrayEquals(expected, first.get());
        }
    }

    @Test
    public void testRecvStrStream() throws IOException {
        int port = Utils.findOpenPort();
        try (final ZMQ.Context ctx = new ZMQ.Context(1);final ZMQ.Socket pull = ctx.socket(PULL);final ZMQ.Socket push = ctx.socket(PUSH)) {
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://127.0.0.1:" + port));
            final String expected = "Hello";
            push.send(expected);
            Optional<String> first = pull.recvStrStream().peek(System.out::print).findFirst();
            System.out.println();
            Assert.assertTrue(first.isPresent());
            Assert.assertEquals(expected, first.get());
        }
    }
}

