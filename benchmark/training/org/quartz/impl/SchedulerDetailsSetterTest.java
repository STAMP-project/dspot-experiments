/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz.impl;


import StdSchedulerFactory.PROP_JOB_STORE_CLASS;
import StdSchedulerFactory.PROP_THREAD_POOL_CLASS;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import junit.framework.TestCase;
import org.quartz.SchedulerException;
import org.quartz.simpl.RAMJobStore;
import org.quartz.simpl.SimpleThreadPool;
import org.quartz.spi.ThreadPool;


public class SchedulerDetailsSetterTest extends TestCase {
    public void testSetter() throws IOException, SchedulerException {
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/org/quartz/quartz.properties"));
        props.setProperty(PROP_THREAD_POOL_CLASS, SchedulerDetailsSetterTest.MyThreadPool.class.getName());
        props.setProperty(PROP_JOB_STORE_CLASS, SchedulerDetailsSetterTest.MyJobStore.class.getName());
        StdSchedulerFactory factory = new StdSchedulerFactory(props);
        factory.getScheduler();// this will initialize all the test fixtures.

        TestCase.assertEquals(3, SchedulerDetailsSetterTest.instanceIdCalls.get());
        TestCase.assertEquals(3, SchedulerDetailsSetterTest.instanceNameCalls.get());
        DirectSchedulerFactory directFactory = DirectSchedulerFactory.getInstance();
        directFactory.createScheduler(new SchedulerDetailsSetterTest.MyThreadPool(), new SchedulerDetailsSetterTest.MyJobStore());
        TestCase.assertEquals(5, SchedulerDetailsSetterTest.instanceIdCalls.get());
        TestCase.assertEquals(6, SchedulerDetailsSetterTest.instanceNameCalls.get());
    }

    public void testMissingSetterMethods() throws SchedulerException {
        SchedulerDetailsSetter.setDetails(new Object(), "name", "id");
    }

    public void testUnimplementedMethods() throws Exception {
        ThreadPool tp = makeIncompleteThreadPool();
        try {
            tp.setInstanceName("name");
            TestCase.fail();
        } catch (AbstractMethodError ame) {
            // expected
        }
        SchedulerDetailsSetter.setDetails(tp, "name", "id");
    }

    private static final AtomicInteger instanceIdCalls = new AtomicInteger();

    private static final AtomicInteger instanceNameCalls = new AtomicInteger();

    public static class MyThreadPool extends SimpleThreadPool {
        @Override
        public void initialize() {
        }

        @Override
        public void setInstanceId(String schedInstId) {
            super.setInstanceId(schedInstId);
            SchedulerDetailsSetterTest.instanceIdCalls.incrementAndGet();
        }

        @Override
        public void setInstanceName(String schedName) {
            super.setInstanceName(schedName);
            SchedulerDetailsSetterTest.instanceNameCalls.incrementAndGet();
        }
    }

    public static class MyJobStore extends RAMJobStore {
        @Override
        public void setInstanceId(String schedInstId) {
            super.setInstanceId(schedInstId);
            SchedulerDetailsSetterTest.instanceIdCalls.incrementAndGet();
        }

        @Override
        public void setInstanceName(String schedName) {
            super.setInstanceName(schedName);
            SchedulerDetailsSetterTest.instanceNameCalls.incrementAndGet();
        }
    }
}

