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


import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.hystrix.config.HystrixConfiguration;
import com.netflix.hystrix.config.HystrixConfigurationStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
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


public class HystrixSampleSseServletTest {
    private static final String INTERJECTED_CHARACTER = "a";

    @Mock
    HttpServletRequest mockReq;

    @Mock
    HttpServletResponse mockResp;

    @Mock
    HystrixConfiguration mockConfig;

    @Mock
    PrintWriter mockPrintWriter;

    HystrixSampleSseServletTest.TestHystrixConfigSseServlet servlet;

    @Test
    public void testNoConcurrentResponseWrites() throws IOException, InterruptedException {
        final Observable<HystrixConfiguration> limitedOnNexts = Observable.create(new Observable.OnSubscribe<HystrixConfiguration>() {
            @Override
            public void call(Subscriber<? extends HystrixConfiguration> subscriber) {
                try {
                    for (int i = 0; i < 500; i++) {
                        Thread.sleep(10);
                        subscriber.onNext(mockConfig);
                    }
                } catch ( ex) {
                    ex.printStackTrace();
                } catch ( e) {
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.computation());
        servlet = new HystrixSampleSseServletTest.TestHystrixConfigSseServlet(limitedOnNexts, 1);
        try {
            init();
        } catch (ServletException ex) {
        }
        final StringBuilder buffer = new StringBuilder();
        Mockito.when(mockReq.getParameter("delay")).thenReturn("100");
        Mockito.when(mockResp.getWriter()).thenReturn(mockPrintWriter);
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                String written = ((String) (invocation.getArguments()[0]));
                if (written.contains("ping")) {
                    buffer.append(HystrixSampleSseServletTest.INTERJECTED_CHARACTER);
                } else {
                    // slow down the append to increase chances to interleave
                    for (int i = 0; i < (written.length()); i++) {
                        Thread.sleep(5);
                        buffer.append(written.charAt(i));
                    }
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
        Pattern pattern = Pattern.compile((("\\{[" + (HystrixSampleSseServletTest.INTERJECTED_CHARACTER)) + "]+\\}"));
        boolean hasInterleaved = pattern.matcher(buffer).find();
        Assert.assertFalse(hasInterleaved);
    }

    private static class TestHystrixConfigSseServlet extends HystrixSampleSseServlet {
        private static AtomicInteger concurrentConnections = new AtomicInteger(0);

        private static DynamicIntProperty maxConcurrentConnections = DynamicPropertyFactory.getInstance().getIntProperty("hystrix.config.stream.maxConcurrentConnections", 5);

        public TestHystrixConfigSseServlet() {
            this(HystrixConfigurationStream.getInstance().observe(), DEFAULT_PAUSE_POLLER_THREAD_DELAY_IN_MS);
        }

        TestHystrixConfigSseServlet(Observable<HystrixConfiguration> sampleStream, int pausePollerThreadDelayInMs) {
            super(sampleStream.map(new rx.functions.Func1<HystrixConfiguration, String>() {
                @Override
                public String call(HystrixConfiguration hystrixConfiguration) {
                    return "{}";
                }
            }), pausePollerThreadDelayInMs);
        }

        @Override
        protected int getMaxNumberConcurrentConnectionsAllowed() {
            return HystrixSampleSseServletTest.TestHystrixConfigSseServlet.maxConcurrentConnections.get();
        }

        @Override
        protected int getNumberCurrentConnections() {
            return HystrixSampleSseServletTest.TestHystrixConfigSseServlet.concurrentConnections.get();
        }

        @Override
        protected int incrementAndGetCurrentConcurrentConnections() {
            return HystrixSampleSseServletTest.TestHystrixConfigSseServlet.concurrentConnections.incrementAndGet();
        }

        @Override
        protected void decrementCurrentConcurrentConnections() {
            HystrixSampleSseServletTest.TestHystrixConfigSseServlet.concurrentConnections.decrementAndGet();
        }
    }
}

