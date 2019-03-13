/**
 * Copyright (C) 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.scheduler;


import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import ninja.lifecycle.FailedStartException;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class SchedulerSupportTest {
    private Injector injector;

    @Test
    public void schedulableShouldNotBeScheduledBeforeStart() throws Exception {
        injector = createInjector();
        injector.getInstance(SchedulerSupportTest.MockScheduled.class);
        Thread.sleep(100);
        Assert.assertThat(SchedulerSupportTest.MockScheduled.countDownLatch.getCount(), IsEqual.equalTo(1L));
    }

    @Test(timeout = 5000)
    public void schedulableShouldBeScheduledAfterStart() throws Exception {
        injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SchedulerSupportTest.MockScheduled.class);
            }
        });
        start(injector);
        SchedulerSupportTest.MockScheduled.countDownLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 5000)
    public void schedulableAddedAfterStartShouldBeScheduledImmediately() throws Exception {
        injector = createInjector();
        start(injector);
        injector.getInstance(SchedulerSupportTest.MockScheduled.class);
        SchedulerSupportTest.MockScheduled.countDownLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 5000)
    public void schedulableShouldUsePropertyConfig() throws Exception {
        injector = createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Key.get(String.class, Names.named("delay.property"))).toInstance("10");
            }
        });
        injector.getInstance(SchedulerSupportTest.MockPropertyScheduled.class);
        start(injector);
        SchedulerSupportTest.MockPropertyScheduled.countDownLatch.await(5000, TimeUnit.MILLISECONDS);
    }

    @Test(expected = FailedStartException.class)
    public void schedulableShouldThrowExceptionWhenNoPropertyFound() throws Exception {
        injector = createInjector();
        injector.getInstance(SchedulerSupportTest.MockPropertyScheduled.class);
        start(injector);
    }

    @Singleton
    public static class MockScheduled {
        static CountDownLatch countDownLatch;

        @Schedule(initialDelay = 10, delay = 1000000000)
        public void doSomething() {
            SchedulerSupportTest.MockScheduled.countDownLatch.countDown();
        }
    }

    @Singleton
    public static class MockPropertyScheduled {
        static CountDownLatch countDownLatch;

        @Schedule(delayProperty = "delay.property")
        public void doSomething() {
            SchedulerSupportTest.MockPropertyScheduled.countDownLatch.countDown();
        }
    }
}

