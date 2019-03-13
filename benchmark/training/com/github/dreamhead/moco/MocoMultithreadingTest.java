package com.github.dreamhead.moco;


import com.github.dreamhead.moco.helper.MocoTestHelper;
import com.github.dreamhead.moco.helper.RemoteTestUtils;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MocoMultithreadingTest {
    private MocoTestHelper helper;

    @Test
    public void should_work_well_for_request_hit() throws Exception {
        RequestHit hit = MocoRequestHit.requestHit();
        final HttpServer server = Moco.httpServer(RemoteTestUtils.port(), hit);
        server.get(Moco.by(Moco.uri("/foo"))).response("bar");
        final ExecutorService executorService = Executors.newFixedThreadPool(50);
        final int count = 100;
        final CountDownLatch latch = new CountDownLatch(count);
        Runner.running(server, new Runnable() {
            @Override
            public void run() throws Exception {
                for (int i = 0; i < count; i++) {
                    executorService.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Assert.assertThat(helper.get(RemoteTestUtils.remoteUrl("/foo")), CoreMatchers.is("bar"));
                                latch.countDown();
                            } catch (IOException ignored) {
                            }
                        }
                    });
                }
                latch.await();
            }
        });
        hit.verify(Moco.by(Moco.uri("/foo")), MocoRequestHit.times(count));
    }
}

