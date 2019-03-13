/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.logging;


import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.IsolatedLoggingRule;
import com.hazelcast.test.annotation.NightlyTest;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category(NightlyTest.class)
public class CustomLoggerFactorySingularityTest extends HazelcastTestSupport {
    private static final int TEST_DURATION_SECONDS = 5;

    private static final int THREAD_COUNT = 4;

    @Rule
    public final IsolatedLoggingRule isolatedLoggingRule = new IsolatedLoggingRule();

    @Test
    public void testCustomLoggerFactorySingularity() throws Exception {
        isolatedLoggingRule.setLoggingClass(CustomLoggerFactorySingularityTest.CustomLoggerFactory.class);
        long deadLine = (System.currentTimeMillis()) + (TimeUnit.SECONDS.toMillis(CustomLoggerFactorySingularityTest.TEST_DURATION_SECONDS));
        CustomLoggerFactorySingularityTest.TestThread[] threads = CustomLoggerFactorySingularityTest.startThreads(deadLine);
        while ((System.currentTimeMillis()) < deadLine) {
            if (CustomLoggerFactorySingularityTest.CustomLoggerFactory.singularityCheckFailed) {
                Assert.fail("singularity check failed");
            }
            // begin the new round of a factory creation
            synchronized(isolatedLoggingRule.getLoggerFactoryLock()) {
                CustomLoggerFactorySingularityTest.CustomLoggerFactory.INSTANCE_COUNT.set(0);
                isolatedLoggingRule.setLoggerFactory(null);
            }
        } 
        CustomLoggerFactorySingularityTest.assertThreadsEventuallyFinishesWithoutException(threads);
    }

    private static class CustomLoggerFactory implements LoggerFactory {
        public static final AtomicLong INSTANCE_COUNT = new AtomicLong();

        public static volatile boolean singularityCheckFailed = false;

        public CustomLoggerFactory() {
            final long count = CustomLoggerFactorySingularityTest.CustomLoggerFactory.INSTANCE_COUNT.incrementAndGet();
            // XXX: All exceptions during factory construction are inhibited in Logger.tryCreateLoggerFactory(), so we use a flag
            // here to pass the status to the main thread instead of an assertion.
            if (count != 1) {
                CustomLoggerFactorySingularityTest.CustomLoggerFactory.singularityCheckFailed = true;
            }
        }

        @Override
        public ILogger getLogger(String name) {
            return Mockito.mock(ILogger.class, Mockito.withSettings().stubOnly());
        }
    }

    private static class TestThread extends Thread {
        private final long deadLine;

        private Exception e;

        TestThread(long deadLine) {
            this.deadLine = deadLine;
        }

        @Override
        public void run() {
            try {
                while ((System.currentTimeMillis()) < (deadLine)) {
                    Logger.getLogger(HazelcastTestSupport.randomName());
                    HazelcastTestSupport.assertInstanceOf(CustomLoggerFactorySingularityTest.CustomLoggerFactory.class, Logger.newLoggerFactory("irrelevant"));
                } 
            } catch (Exception e) {
                this.e = e;
            }
        }
    }
}

