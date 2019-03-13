/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.jms.issues;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * Concurrent consumer with JMSReply test.
 */
public class JmsConcurrentConsumersTest extends CamelTestSupport {
    @Test
    public void testConcurrentConsumersWithReply() throws Exception {
        // latch for the 5 exchanges we expect
        final CountDownLatch latch = new CountDownLatch(5);
        // setup a task executor to be able send the messages in parallel
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.afterPropertiesSet();
        for (int i = 0; i < 5; i++) {
            final int count = i;
            executor.execute(new Runnable() {
                public void run() {
                    // request body is InOut pattern and thus we expect a reply (JMSReply)
                    Object response = template.requestBody("activemq:a", ("World #" + count));
                    assertEquals(("Bye World #" + count), response);
                    latch.countDown();
                }
            });
        }
        long start = System.currentTimeMillis();
        // wait for test completion, timeout after 30 sec to let other unit test run to not wait forever
        assertTrue(latch.await(30000L, TimeUnit.MILLISECONDS));
        assertEquals("Latch should be zero", 0, latch.getCount());
        long delta = (System.currentTimeMillis()) - start;
        assertTrue((("Should be faster than 20000 millis, took " + delta) + " millis"), (delta < 20000L));
        executor.shutdown();
    }
}

