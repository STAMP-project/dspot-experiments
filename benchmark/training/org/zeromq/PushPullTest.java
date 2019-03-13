package org.zeromq;


import SocketType.PULL;
import SocketType.PUSH;
import ZMQ.Context;
import ZMQ.Socket;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import zmq.util.Utils;


public class PushPullTest {
    private static final int REPETITIONS = 10;

    class Sender implements Runnable {
        private final CountDownLatch latch;

        private final int port;

        public Sender(CountDownLatch latch, int port) {
            this.latch = latch;
            this.port = port;
        }

        @Override
        public void run() {
            String address = "tcp://*:" + (port);
            ZMQ.Context context = ZMQ.context(1);
            Assert.assertThat(context, CoreMatchers.notNullValue());
            ZMQ.Socket socket = context.socket(PUSH);
            Assert.assertThat(socket, CoreMatchers.notNullValue());
            // Socket options
            boolean rc = socket.setLinger(1000);
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = socket.bind(address);
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Ensure that receiver is "connected" before sending
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int idx = 0; idx < (PushPullTest.REPETITIONS); ++idx) {
                rc = socket.send(("data" + idx));
                Assert.assertThat(rc, CoreMatchers.is(true));
            }
            socket.close();
            context.close();
        }
    }

    class Receiver implements Runnable {
        private final CountDownLatch latch;

        private final int port;

        public Receiver(CountDownLatch latch, int port) {
            this.latch = latch;
            this.port = port;
        }

        @Override
        public void run() {
            String address = "tcp://localhost:" + (port);
            ZMQ.Context context = ZMQ.context(1);
            Assert.assertThat(context, CoreMatchers.notNullValue());
            ZMQ.Socket socket = context.socket(PULL);
            Assert.assertThat(socket, CoreMatchers.notNullValue());
            // Options Section
            boolean rc = socket.setRcvHWM(1);
            Assert.assertThat(rc, CoreMatchers.is(true));
            rc = socket.connect(address);
            Assert.assertThat(rc, CoreMatchers.is(true));
            latch.countDown();
            for (int idx = 0; idx < (PushPullTest.REPETITIONS); ++idx) {
                String recvd = socket.recvStr();
                Assert.assertThat(recvd, CoreMatchers.is(("data" + idx)));
                ZMQ.msleep(10);
            }
            socket.close();
            context.close();
        }
    }

    @Test
    public void testIssue131() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final int port = Utils.findOpenPort();
        Thread sender = new Thread(new PushPullTest.Sender(latch, port));
        Thread receiver = new Thread(new PushPullTest.Receiver(latch, port));
        // Start sender before receiver and use latch to ensure that receiver has connected
        // before sender is sending messages
        sender.start();
        receiver.start();
        sender.join();
        receiver.join();
    }
}

