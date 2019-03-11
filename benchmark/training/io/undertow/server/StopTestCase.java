package io.undertow.server;


import Options.WORKER_IO_THREADS;
import io.undertow.Undertow;
import org.junit.Test;


public class StopTestCase {
    @Test
    public void testStopUndertowNotStarted() {
        Undertow.builder().build().stop();
    }

    @Test
    public void testStopUndertowAfterExceptionDuringStart() {
        // Making the NioXnioWorker constructor throw an exception, resulting in the Undertow.worker field not getting set.
        Undertow undertow = Undertow.builder().setWorkerOption(WORKER_IO_THREADS, (-1)).build();
        try {
            undertow.start();
        } catch (RuntimeException e) {
        }
        undertow.stop();
    }
}

