package org.zeromq;


import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

import static ZMQ.PULL;
import static ZMQ.PUSH;


public class ZSocketTest {
    @Test
    public void pushPullTest() throws IOException {
        int port = Utils.findOpenPort();
        try (final ZSocket pull = new ZSocket(PULL);final ZSocket push = new ZSocket(PUSH)) {
            pull.bind(("tcp://*:" + port));
            push.connect(("tcp://127.0.0.1:" + port));
            final String expected = "hello";
            push.sendStringUtf8(expected);
            final String actual = pull.receiveStringUtf8();
            Assert.assertEquals(expected, actual);
        }
    }
}

