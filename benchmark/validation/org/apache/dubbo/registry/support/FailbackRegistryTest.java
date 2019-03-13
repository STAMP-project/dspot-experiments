/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.registry.support;


import Constants.CONSUMER_PROTOCOL;
import FailbackRegistry.Holder;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.registry.NotifyListener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class FailbackRegistryTest {
    static String service;

    static URL serviceUrl;

    static URL registryUrl;

    FailbackRegistryTest.MockRegistry registry;

    private int FAILED_PERIOD = 200;

    private int sleeptime = 100;

    private int trytimes = 5;

    /**
     * Test method for retry
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDoRetry() throws Exception {
        final AtomicReference<Boolean> notified = new AtomicReference<Boolean>(false);
        // the latest latch just for 3. Because retry method has been removed.
        final CountDownLatch latch = new CountDownLatch(2);
        NotifyListener listner = new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                notified.set(Boolean.TRUE);
            }
        };
        registry = new FailbackRegistryTest.MockRegistry(FailbackRegistryTest.registryUrl, latch);
        registry.setBad(true);
        registry.register(FailbackRegistryTest.serviceUrl);
        registry.unregister(FailbackRegistryTest.serviceUrl);
        registry.subscribe(FailbackRegistryTest.serviceUrl.setProtocol(CONSUMER_PROTOCOL).addParameters(CollectionUtils.toStringMap("check", "false")), listner);
        registry.unsubscribe(FailbackRegistryTest.serviceUrl.setProtocol(CONSUMER_PROTOCOL).addParameters(CollectionUtils.toStringMap("check", "false")), listner);
        // Failure can not be called to listener.
        Assertions.assertEquals(false, notified.get());
        Assertions.assertEquals(2, latch.getCount());
        registry.setBad(false);
        for (int i = 0; i < (trytimes); i++) {
            System.out.println(("failback registry retry ,times:" + i));
            // System.out.println(latch.getCount());
            if ((latch.getCount()) == 0)
                break;

            Thread.sleep(sleeptime);
        }
        // Thread.sleep(100000);//for debug
        Assertions.assertEquals(0, latch.getCount());
        // The failedsubcribe corresponding key will be cleared when unsubscribing
        Assertions.assertEquals(false, notified.get());
    }

    @Test
    public void testDoRetry_subscribe() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);// All of them are called 4 times. A successful attempt to lose 1. subscribe will not be done

        registry = new FailbackRegistryTest.MockRegistry(FailbackRegistryTest.registryUrl, latch);
        registry.setBad(true);
        registry.register(FailbackRegistryTest.serviceUrl);
        registry.setBad(false);
        for (int i = 0; i < (trytimes); i++) {
            System.out.println(("failback registry retry ,times:" + i));
            if ((latch.getCount()) == 0)
                break;

            Thread.sleep(sleeptime);
        }
        Assertions.assertEquals(0, latch.getCount());
    }

    @Test
    public void testDoRetry_register() throws Exception {
        final AtomicReference<Boolean> notified = new AtomicReference<Boolean>(false);
        final CountDownLatch latch = new CountDownLatch(1);// All of them are called 4 times. A successful attempt to lose 1. subscribe will not be done

        NotifyListener listner = new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                notified.set(Boolean.TRUE);
            }
        };
        registry = new FailbackRegistryTest.MockRegistry(FailbackRegistryTest.registryUrl, latch);
        registry.setBad(true);
        registry.subscribe(FailbackRegistryTest.serviceUrl.setProtocol(CONSUMER_PROTOCOL).addParameters(CollectionUtils.toStringMap("check", "false")), listner);
        // Failure can not be called to listener.
        Assertions.assertEquals(false, notified.get());
        Assertions.assertEquals(1, latch.getCount());
        registry.setBad(false);
        for (int i = 0; i < (trytimes); i++) {
            System.out.println(("failback registry retry ,times:" + i));
            // System.out.println(latch.getCount());
            if ((latch.getCount()) == 0)
                break;

            Thread.sleep(sleeptime);
        }
        // Thread.sleep(100000);
        Assertions.assertEquals(0, latch.getCount());
        // The failedsubcribe corresponding key will be cleared when unsubscribing
        Assertions.assertEquals(true, notified.get());
    }

    @Test
    public void testDoRetry_nofify() throws Exception {
        // Initial value 0
        final AtomicInteger count = new AtomicInteger(0);
        NotifyListener listner = new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                count.incrementAndGet();
                // The exception is thrown for the first time to see if the back will be called again to incrementAndGet
                if ((count.get()) == 1L) {
                    throw new RuntimeException("test exception please ignore");
                }
            }
        };
        registry = new FailbackRegistryTest.MockRegistry(FailbackRegistryTest.registryUrl, new CountDownLatch(0));
        registry.subscribe(FailbackRegistryTest.serviceUrl.setProtocol(CONSUMER_PROTOCOL).addParameters(CollectionUtils.toStringMap("check", "false")), listner);
        Assertions.assertEquals(1, count.get());// Make sure that the subscribe call has just been called once count.incrementAndGet after the call is completed

        // Wait for the timer.
        for (int i = 0; i < (trytimes); i++) {
            System.out.println(("failback notify retry ,times:" + i));
            if ((count.get()) == 2)
                break;

            Thread.sleep(sleeptime);
        }
        Assertions.assertEquals(2, count.get());
    }

    @Test
    public void testRecover() throws Exception {
        CountDownLatch countDownLatch = new CountDownLatch(4);
        final AtomicReference<Boolean> notified = new AtomicReference<Boolean>(false);
        NotifyListener listener = new NotifyListener() {
            @Override
            public void notify(List<URL> urls) {
                notified.set(Boolean.TRUE);
            }
        };
        FailbackRegistryTest.MockRegistry mockRegistry = new FailbackRegistryTest.MockRegistry(FailbackRegistryTest.registryUrl, countDownLatch);
        mockRegistry.register(FailbackRegistryTest.serviceUrl);
        mockRegistry.subscribe(FailbackRegistryTest.serviceUrl, listener);
        Assertions.assertEquals(1, getRegistered().size());
        Assertions.assertEquals(1, getSubscribed().size());
        recover();
        countDownLatch.await();
        Assertions.assertEquals(0, getFailedRegistered().size());
        FailbackRegistry.Holder h = new FailbackRegistry.Holder(FailbackRegistryTest.registryUrl, listener);
        Assertions.assertEquals(null, getFailedSubscribed().get(h));
        Assertions.assertEquals(countDownLatch.getCount(), 0);
    }

    private static class MockRegistry extends FailbackRegistry {
        CountDownLatch latch;

        private boolean bad = false;

        /**
         *
         *
         * @param url
         * 		
         */
        public MockRegistry(URL url, CountDownLatch latch) {
            super(url);
            this.latch = latch;
        }

        /**
         *
         *
         * @param bad
         * 		the bad to set
         */
        public void setBad(boolean bad) {
            this.bad = bad;
        }

        @Override
        public void doRegister(URL url) {
            if (bad) {
                throw new RuntimeException("can not invoke!");
            }
            // System.out.println("do doRegister");
            latch.countDown();
        }

        @Override
        public void doUnregister(URL url) {
            if (bad) {
                throw new RuntimeException("can not invoke!");
            }
            // System.out.println("do doUnregister");
            latch.countDown();
        }

        @Override
        public void doSubscribe(URL url, NotifyListener listener) {
            if (bad) {
                throw new RuntimeException("can not invoke!");
            }
            // System.out.println("do doSubscribe");
            super.notify(url, listener, Arrays.asList(new URL[]{ FailbackRegistryTest.serviceUrl }));
            latch.countDown();
        }

        @Override
        public void doUnsubscribe(URL url, NotifyListener listener) {
            if (bad) {
                throw new RuntimeException("can not invoke!");
            }
            // System.out.println("do doUnsubscribe");
            latch.countDown();
        }

        @Override
        public boolean isAvailable() {
            return true;
        }
    }
}

