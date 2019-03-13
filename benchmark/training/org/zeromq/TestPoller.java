package org.zeromq;


import SocketType.PAIR;
import SocketType.PULL;
import SocketType.PUSH;
import ZMQ.Context;
import ZMQ.Poller;
import ZMQ.Poller.POLLIN;
import ZMQ.Poller.POLLOUT;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


public class TestPoller {
    static class Client implements Runnable {
        private final AtomicBoolean received = new AtomicBoolean();

        private final String address;

        public Client(String addr) {
            this.address = addr;
        }

        @Override
        public void run() {
            ZMQ.Context context = ZMQ.context(1);
            Socket pullConnect = context.socket(PULL);
            pullConnect.connect(address);
            System.out.println("Receiver Started");
            pullConnect.recv(0);
            received.set(true);
            pullConnect.close();
            context.close();
            System.out.println("Receiver Stopped");
        }
    }

    @Test
    public void testPollerPollout() throws Exception {
        int port = Utils.findOpenPort();
        String addr = "tcp://127.0.0.1:" + port;
        TestPoller.Client client = new TestPoller.Client(addr);
        ZMQ.Context context = ZMQ.context(1);
        // Socket to send messages to
        ZMQ.Socket sender = context.socket(PUSH);
        sender.setImmediate(false);
        sender.bind(addr);
        ZMQ.Poller outItems = context.poller();
        outItems.register(sender, POLLOUT);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        while (!(Thread.currentThread().isInterrupted())) {
            outItems.poll(1000);
            if (outItems.pollout(0)) {
                boolean rc = sender.send("OK", 0);
                Assert.assertThat(rc, CoreMatchers.is(true));
                System.out.println("Sender: wrote message");
                break;
            } else {
                System.out.println("Sender: not writable");
                executor.submit(client);
            }
        } 
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        outItems.close();
        sender.close();
        System.out.println("Poller test done");
        Assert.assertThat(client.received.get(), CoreMatchers.is(true));
        context.term();
    }

    @Test
    public void testExitPollerIssue580() throws InterruptedException, ExecutionException {
        Future<Integer> future = null;
        ExecutorService service = Executors.newSingleThreadExecutor();
        try (ZContext context = new ZContext(1);ZMQ.Poller poller = context.createPoller(1)) {
            ZMQ.Socket socket = context.createSocket(PAIR);
            Assert.assertThat(socket, CoreMatchers.notNullValue());
            int rc = poller.register(socket, POLLIN);
            Assert.assertThat(rc, CoreMatchers.is(0));
            future = service.submit(() -> poller.poll((-1)));
            Assert.assertThat(future, CoreMatchers.notNullValue());
            ZMQ.msleep(100);
        }
        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);
        Assert.assertThat(future.get(), CoreMatchers.is((-1)));
    }
}

