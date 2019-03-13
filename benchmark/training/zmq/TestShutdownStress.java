package zmq;


import ZMQ.ZMQ_PUB;
import ZMQ.ZMQ_SUB;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestShutdownStress {
    private static final int THREAD_COUNT = 100;

    class Worker implements Runnable {
        SocketBase s;

        Worker(SocketBase s) throws IOException {
            this.s = s;
        }

        @Override
        public void run() {
            boolean rc = ZMQ.connect(s, "tcp://127.0.0.1:*");
            Assert.assertThat(rc, CoreMatchers.is(true));
            // Start closing the socket while the connecting process is underway.
            ZMQ.close(s);
        }
    }

    @Test
    public void testShutdownStress() throws Exception {
        Thread[] threads = new Thread[TestShutdownStress.THREAD_COUNT];
        for (int j = 0; j != 10; j++) {
            Ctx ctx = ZMQ.init(7);
            Assert.assertThat(ctx, CoreMatchers.notNullValue());
            SocketBase s1 = ZMQ.socket(ctx, ZMQ_PUB);
            Assert.assertThat(s1, CoreMatchers.notNullValue());
            boolean rc = ZMQ.bind(s1, "tcp://127.0.0.1:*");
            Assert.assertThat(rc, CoreMatchers.is(true));
            for (int i = 0; i != (TestShutdownStress.THREAD_COUNT); i++) {
                SocketBase s2 = ZMQ.socket(ctx, ZMQ_SUB);
                assert s2 != null;
                threads[i] = new Thread(new TestShutdownStress.Worker(s2));
                threads[i].start();
            }
            for (int i = 0; i != (TestShutdownStress.THREAD_COUNT); i++) {
                threads[i].join();
            }
            ZMQ.close(s1);
            ZMQ.term(ctx);
        }
    }
}

