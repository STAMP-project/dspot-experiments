package io.searchbox.client.http;


import HttpResponseStatus.FORBIDDEN;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResultHandler;
import io.searchbox.indices.Stats;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import org.junit.Test;


/**
 * Test the situation where there's a misconfigured proxy between the Jest
 * client and the server.  If the proxy speaks text/html instead of
 * application/json, we should not throw a generic JsonSyntaxException.
 */
public class FailingProxyTest {
    JestClientFactory factory = new JestClientFactory();

    private FailingProxy proxy;

    private JestHttpClient client;

    private Stats status;

    @Test
    public void testWithFailingProxy() throws IOException, InterruptedException {
        Exception exception = runSynchronously();
        validateFailingProxyException(exception);
    }

    @Test
    public void testAsyncWithFailingProxy() throws IOException, InterruptedException {
        Exception exception = runAsynchronously();
        validateFailingProxyException(exception);
    }

    @Test
    public void testWithBrokenResponse() throws IOException, InterruptedException {
        proxy.setErrorStatus(FORBIDDEN);
        proxy.setErrorContentType("application/json");
        proxy.setErrorMessage("banana");// <-- this is not json at all!

        Exception exception = runSynchronously();
        validateBrokenResponseException(exception);
    }

    @Test
    public void testAsyncWithBrokenResponse() throws IOException, InterruptedException {
        proxy.setErrorStatus(FORBIDDEN);
        proxy.setErrorContentType("application/json");
        proxy.setErrorMessage("banana");// <-- this is not json at all!

        Exception exception = runAsynchronously();
        validateBrokenResponseException(exception);
    }

    private class ResultHandler implements JestResultHandler {
        private final Semaphore sema = new Semaphore(0);

        private Exception exception = null;

        @Override
        public void completed(final Object result) {
            sema.release();
        }

        @Override
        public void failed(final Exception ex) {
            exception = ex;
            sema.release();
        }

        public Exception get() throws InterruptedException {
            sema.acquire();
            return exception;
        }
    }
}

