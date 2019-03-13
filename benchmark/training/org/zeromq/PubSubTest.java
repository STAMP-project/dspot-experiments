package org.zeromq;


import SocketType.PUB;
import SocketType.SUB;
import ZMQ.Context;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.zeromq.ZMQ.Socket;


public class PubSubTest {
    @Test
    public void testUnsubscribeIssue554() throws Exception {
        final int port = Utils.findOpenPort();
        final ExecutorService service = Executors.newFixedThreadPool(2);
        final Runnable pub = new Runnable() {
            @Override
            public void run() {
                final ZMQ.Context ctx = ZMQ.context(1);
                Assert.assertThat(ctx, CoreMatchers.notNullValue());
                final ZMQ.Socket pub = ctx.socket(PUB);
                Assert.assertThat(pub, CoreMatchers.notNullValue());
                boolean rc = pub.bind(("tcp://*:" + port));
                Assert.assertThat(rc, CoreMatchers.is(true));
                for (int idx = 1; idx <= 15; ++idx) {
                    rc = pub.sendMore("test/");
                    Assert.assertThat(rc, CoreMatchers.is(true));
                    rc = pub.send(("data" + idx));
                    Assert.assertThat(rc, CoreMatchers.is(true));
                    System.out.printf("Send-%d/", idx);
                    ZMQ.sleep(1);
                }
                pub.close();
                ctx.close();
            }
        };
        final Callable<Integer> sub = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                final ZMQ.Context ctx = ZMQ.context(1);
                Assert.assertThat(ctx, CoreMatchers.notNullValue());
                final ZMQ.Socket sub = ctx.socket(SUB);
                Assert.assertThat(sub, CoreMatchers.notNullValue());
                boolean rc = sub.setReceiveTimeOut(3000);
                Assert.assertThat(rc, CoreMatchers.is(true));
                rc = sub.subscribe("test/");
                Assert.assertThat(rc, CoreMatchers.is(true));
                rc = sub.connect(("tcp://localhost:" + port));
                Assert.assertThat(rc, CoreMatchers.is(true));
                System.out.println("[SUB]");
                int received = receive(sub, 5);
                Assert.assertThat((received > 1), CoreMatchers.is(true));
                // unsubscribe from the topic and verify that we don't receive messages anymore
                rc = sub.unsubscribe("test/");
                Assert.assertThat(rc, CoreMatchers.is(true));
                System.out.printf("%n[UNSUB]%n");
                received = receive(sub, 10);
                sub.close();
                ctx.close();
                return received;
            }

            private int receive(ZMQ.Socket socket, int maxSeconds) {
                int received = 0;
                long current = System.currentTimeMillis();
                long end = current + (maxSeconds * 1000);
                while (current < end) {
                    ZMsg msg = ZMsg.recvMsg(socket);
                    current = System.currentTimeMillis();
                    if (msg == null) {
                        continue;
                    }
                    ++received;
                } 
                return received;
            }
        };
        final Future<Integer> rc = service.submit(sub);
        service.submit(pub);
        service.shutdown();
        service.awaitTermination(60, TimeUnit.SECONDS);
        final int receivedAfterUnsubscription = rc.get();
        Assert.assertThat(receivedAfterUnsubscription, CoreMatchers.is(0));
    }
}

