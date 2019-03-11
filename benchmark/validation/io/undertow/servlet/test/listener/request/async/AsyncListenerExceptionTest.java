/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2017 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.listener.request.async;


import io.undertow.testutils.DefaultServer;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test that AsyncListener failures do not block execution of other listeners.
 *
 * @author ckozak
 */
@RunWith(DefaultServer.class)
public class AsyncListenerExceptionTest {
    @Test
    public void onCompleteThrowsRuntimeException() throws IOException, InterruptedException {
        doTest("runtime", false);
    }

    @Test
    public void onCompleteThrowsIOException() throws IOException, InterruptedException {
        doTest("io", false);
    }

    @Test
    public void onCompleteThrowsError() throws IOException, InterruptedException {
        doTest("error", false);
    }

    @Test
    public void onTimeoutThrowsRuntimeException() throws IOException, InterruptedException {
        doTest("runtime", true);
    }

    @Test
    public void onTimeoutThrowsIOException() throws IOException, InterruptedException {
        doTest("io", true);
    }

    @Test
    public void onTimeoutThrowsError() throws IOException, InterruptedException {
        doTest("error", true);
    }

    public abstract static class AbstractAsyncServlet extends HttpServlet {
        static final BlockingQueue<String> QUEUE = new LinkedBlockingDeque<>();

        @Override
        protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, ServletException {
            AsyncContext context = req.startAsync();
            context.setTimeout(1000);
            for (int i = 0; i < 2; i++) {
                context.addListener(new AsyncListener() {
                    @Override
                    public void onComplete(AsyncEvent asyncEvent) throws IOException {
                        AsyncListenerExceptionTest.AbstractAsyncServlet.QUEUE.add("onComplete");
                        throwException();
                    }

                    @Override
                    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
                        AsyncListenerExceptionTest.AbstractAsyncServlet.QUEUE.add("onTimeout");
                        throwException();
                    }

                    @Override
                    public void onError(AsyncEvent asyncEvent) throws IOException {
                        AsyncListenerExceptionTest.AbstractAsyncServlet.QUEUE.add("onError");
                        throwException();
                    }

                    @Override
                    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
                        AsyncListenerExceptionTest.AbstractAsyncServlet.QUEUE.add("onStartAsync");
                    }
                });
            }
            if ((req.getHeader("timeout")) == null) {
                context.complete();
            }
        }

        protected abstract void throwException() throws IOException;
    }

    public static final class RuntimeExceptionServlet extends AsyncListenerExceptionTest.AbstractAsyncServlet {
        @Override
        protected void throwException() throws IOException {
            throw new RuntimeException();
        }
    }

    public static final class IOExceptionServlet extends AsyncListenerExceptionTest.AbstractAsyncServlet {
        @Override
        protected void throwException() throws IOException {
            throw new IOException();
        }
    }

    public static final class ErrorServlet extends AsyncListenerExceptionTest.AbstractAsyncServlet {
        @Override
        protected void throwException() throws IOException {
            throw new Error();
        }
    }
}

