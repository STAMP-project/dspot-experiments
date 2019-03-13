/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hystrix.contrib.sample.stream;


import com.netflix.hystrix.config.HystrixConfiguration;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;
import rx.schedulers.Schedulers;


public class HystrixConfigSseServletTest {
    @Mock
    HttpServletRequest mockReq;

    @Mock
    HttpServletResponse mockResp;

    @Mock
    HystrixConfiguration mockConfig;

    @Mock
    PrintWriter mockPrintWriter;

    HystrixConfigSseServlet servlet;

    private final Observable<HystrixConfiguration> streamOfOnNexts = Observable.interval(100, TimeUnit.MILLISECONDS).map(new rx.functions.Func1<Long, HystrixConfiguration>() {
        @Override
        public HystrixConfiguration call(Long timestamp) {
            return mockConfig;
        }
    });

    private final Observable<HystrixConfiguration> streamOfOnNextThenOnError = Observable.create(new Observable.OnSubscribe<HystrixConfiguration>() {
        @Override
        public void call(Subscriber<? extends HystrixConfiguration> subscriber) {
            try {
                Thread.sleep(100);
                subscriber.onNext(mockConfig);
                Thread.sleep(100);
                subscriber.onError(new RuntimeException("stream failure"));
            } catch ( ex) {
                ex.printStackTrace();
            }
        }
    }).subscribeOn(Schedulers.computation());

    private final Observable<HystrixConfiguration> streamOfOnNextThenOnCompleted = Observable.create(new Observable.OnSubscribe<HystrixConfiguration>() {
        @Override
        public void call(Subscriber<? extends HystrixConfiguration> subscriber) {
            try {
                Thread.sleep(100);
                subscriber.onNext(mockConfig);
                Thread.sleep(100);
                subscriber.onCompleted();
            } catch ( ex) {
                ex.printStackTrace();
            }
        }
    }).subscribeOn(Schedulers.computation());

    @Test
    public void shutdownServletShouldRejectRequests() throws IOException, ServletException {
        servlet = new HystrixConfigSseServlet(streamOfOnNexts, 10);
        try {
            servlet.init();
        } catch (ServletException ex) {
        }
        servlet.shutdown();
        servlet.doGet(mockReq, mockResp);
        Mockito.verify(mockResp).sendError(503, "Service has been shut down.");
    }

    @Test
    public void testConfigDataWithInfiniteOnNextStream() throws IOException, InterruptedException {
        servlet = new HystrixConfigSseServlet(streamOfOnNexts, 10);
        try {
            servlet.init();
        } catch (ServletException ex) {
        }
        final AtomicInteger writes = new AtomicInteger(0);
        Mockito.when(mockReq.getParameter("delay")).thenReturn("100");
        Mockito.when(mockResp.getWriter()).thenReturn(mockPrintWriter);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String written = ((String) (invocation.getArguments()[0]));
                System.out.println(("ARG : " + written));
                if (!(written.contains("ping"))) {
                    writes.incrementAndGet();
                }
                return null;
            }
        }).when(mockPrintWriter).print(Mockito.anyString());
        Runnable simulateClient = new Runnable() {
            @Override
            public void run() {
                try {
                    servlet.doGet(mockReq, mockResp);
                } catch (ServletException ex) {
                    Assert.fail(ex.getMessage());
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };
        Thread t = new Thread(simulateClient);
        System.out.println(("Starting thread : " + (t.getName())));
        t.start();
        System.out.println(("Started thread : " + (t.getName())));
        try {
            Thread.sleep(1000);
            System.out.println(("Woke up from sleep : " + (Thread.currentThread().getName())));
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        System.out.println("About to interrupt");
        t.interrupt();
        System.out.println("Done interrupting");
        Thread.sleep(100);
        System.out.println(("WRITES : " + (writes.get())));
        Assert.assertTrue(((writes.get()) >= 9));
        Assert.assertEquals(0, servlet.getNumberCurrentConnections());
    }

    @Test
    public void testConfigDataWithStreamOnError() throws IOException, InterruptedException {
        servlet = new HystrixConfigSseServlet(streamOfOnNextThenOnError, 10);
        try {
            servlet.init();
        } catch (ServletException ex) {
        }
        final AtomicInteger writes = new AtomicInteger(0);
        Mockito.when(mockReq.getParameter("delay")).thenReturn("100");
        Mockito.when(mockResp.getWriter()).thenReturn(mockPrintWriter);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String written = ((String) (invocation.getArguments()[0]));
                System.out.println(("ARG : " + written));
                if (!(written.contains("ping"))) {
                    writes.incrementAndGet();
                }
                return null;
            }
        }).when(mockPrintWriter).print(Mockito.anyString());
        Runnable simulateClient = new Runnable() {
            @Override
            public void run() {
                try {
                    servlet.doGet(mockReq, mockResp);
                } catch (ServletException ex) {
                    Assert.fail(ex.getMessage());
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };
        Thread t = new Thread(simulateClient);
        t.start();
        try {
            Thread.sleep(1000);
            System.out.println((((System.currentTimeMillis()) + " Woke up from sleep : ") + (Thread.currentThread().getName())));
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertEquals(1, writes.get());
        Assert.assertEquals(0, servlet.getNumberCurrentConnections());
    }

    @Test
    public void testConfigDataWithStreamOnCompleted() throws IOException, InterruptedException {
        servlet = new HystrixConfigSseServlet(streamOfOnNextThenOnCompleted, 10);
        try {
            servlet.init();
        } catch (ServletException ex) {
        }
        final AtomicInteger writes = new AtomicInteger(0);
        Mockito.when(mockReq.getParameter("delay")).thenReturn("100");
        Mockito.when(mockResp.getWriter()).thenReturn(mockPrintWriter);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String written = ((String) (invocation.getArguments()[0]));
                System.out.println(("ARG : " + written));
                if (!(written.contains("ping"))) {
                    writes.incrementAndGet();
                }
                return null;
            }
        }).when(mockPrintWriter).print(Mockito.anyString());
        Runnable simulateClient = new Runnable() {
            @Override
            public void run() {
                try {
                    servlet.doGet(mockReq, mockResp);
                } catch (ServletException ex) {
                    Assert.fail(ex.getMessage());
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };
        Thread t = new Thread(simulateClient);
        t.start();
        try {
            Thread.sleep(1000);
            System.out.println((((System.currentTimeMillis()) + " Woke up from sleep : ") + (Thread.currentThread().getName())));
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertEquals(1, writes.get());
        Assert.assertEquals(0, servlet.getNumberCurrentConnections());
    }

    @Test
    public void testConfigDataWithIoExceptionOnWrite() throws IOException, InterruptedException {
        servlet = new HystrixConfigSseServlet(streamOfOnNexts, 10);
        try {
            servlet.init();
        } catch (ServletException ex) {
        }
        final AtomicInteger writes = new AtomicInteger(0);
        Mockito.when(mockResp.getWriter()).thenReturn(mockPrintWriter);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String written = ((String) (invocation.getArguments()[0]));
                System.out.println(("ARG : " + written));
                if (!(written.contains("ping"))) {
                    writes.incrementAndGet();
                }
                throw new IOException("simulated IO Exception");
            }
        }).when(mockPrintWriter).print(Mockito.anyString());
        Runnable simulateClient = new Runnable() {
            @Override
            public void run() {
                try {
                    servlet.doGet(mockReq, mockResp);
                } catch (ServletException ex) {
                    Assert.fail(ex.getMessage());
                } catch (IOException ex) {
                    Assert.fail(ex.getMessage());
                }
            }
        };
        Thread t = new Thread(simulateClient);
        t.start();
        try {
            Thread.sleep(1000);
            System.out.println((((System.currentTimeMillis()) + " Woke up from sleep : ") + (Thread.currentThread().getName())));
        } catch (InterruptedException ex) {
            Assert.fail(ex.getMessage());
        }
        Assert.assertTrue(((writes.get()) <= 2));
        Assert.assertEquals(0, servlet.getNumberCurrentConnections());
    }
}

