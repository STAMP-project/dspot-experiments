/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.scheduling.concurrent;


import TestGroup.PERFORMANCE;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.task.NoOpRunnable;
import org.springframework.tests.Assume;


/**
 *
 *
 * @author Rick Evans
 * @author Juergen Hoeller
 */
public class ScheduledExecutorFactoryBeanTests {
    @Test
    public void testThrowsExceptionIfPoolSizeIsLessThanZero() throws Exception {
        try {
            ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean();
            factory.setPoolSize((-1));
            factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ new ScheduledExecutorFactoryBeanTests.NoOpScheduledExecutorTask() });
            factory.afterPropertiesSet();
            Assert.fail("Pool size less than zero");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    @SuppressWarnings("serial")
    public void testShutdownNowIsPropagatedToTheExecutorOnDestroy() throws Exception {
        final ScheduledExecutorService executor = Mockito.mock(ScheduledExecutorService.class);
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean() {
            @Override
            protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                return executor;
            }
        };
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ new ScheduledExecutorFactoryBeanTests.NoOpScheduledExecutorTask() });
        factory.afterPropertiesSet();
        factory.destroy();
        Mockito.verify(executor).shutdownNow();
    }

    @Test
    @SuppressWarnings("serial")
    public void testShutdownIsPropagatedToTheExecutorOnDestroy() throws Exception {
        final ScheduledExecutorService executor = Mockito.mock(ScheduledExecutorService.class);
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean() {
            @Override
            protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                return executor;
            }
        };
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ new ScheduledExecutorFactoryBeanTests.NoOpScheduledExecutorTask() });
        factory.setWaitForTasksToCompleteOnShutdown(true);
        factory.afterPropertiesSet();
        factory.destroy();
        Mockito.verify(executor).shutdown();
    }

    @Test
    public void testOneTimeExecutionIsSetUpAndFiresCorrectly() throws Exception {
        Assume.group(PERFORMANCE);
        Runnable runnable = Mockito.mock(Runnable.class);
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean();
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ new ScheduledExecutorTask(runnable) });
        factory.afterPropertiesSet();
        ScheduledExecutorFactoryBeanTests.pauseToLetTaskStart(1);
        factory.destroy();
        Mockito.verify(runnable).run();
    }

    @Test
    public void testFixedRepeatedExecutionIsSetUpAndFiresCorrectly() throws Exception {
        Assume.group(PERFORMANCE);
        Runnable runnable = Mockito.mock(Runnable.class);
        ScheduledExecutorTask task = new ScheduledExecutorTask(runnable);
        task.setPeriod(500);
        task.setFixedRate(true);
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean();
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ task });
        factory.afterPropertiesSet();
        ScheduledExecutorFactoryBeanTests.pauseToLetTaskStart(2);
        factory.destroy();
        Mockito.verify(runnable, Mockito.atLeast(2)).run();
    }

    @Test
    public void testFixedRepeatedExecutionIsSetUpAndFiresCorrectlyAfterException() throws Exception {
        Assume.group(PERFORMANCE);
        Runnable runnable = Mockito.mock(Runnable.class);
        BDDMockito.willThrow(new IllegalStateException()).given(runnable).run();
        ScheduledExecutorTask task = new ScheduledExecutorTask(runnable);
        task.setPeriod(500);
        task.setFixedRate(true);
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean();
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ task });
        factory.setContinueScheduledExecutionAfterException(true);
        factory.afterPropertiesSet();
        ScheduledExecutorFactoryBeanTests.pauseToLetTaskStart(2);
        factory.destroy();
        Mockito.verify(runnable, Mockito.atLeast(2)).run();
    }

    @Test
    @SuppressWarnings("serial")
    public void testSettingThreadFactoryToNullForcesUseOfDefaultButIsOtherwiseCool() throws Exception {
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean() {
            @Override
            protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                Assert.assertNotNull("Bah; the setThreadFactory(..) method must use a default ThreadFactory if a null arg is passed in.");
                return super.createExecutor(poolSize, threadFactory, rejectedExecutionHandler);
            }
        };
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ new ScheduledExecutorFactoryBeanTests.NoOpScheduledExecutorTask() });
        factory.setThreadFactory(null);// the null must not propagate

        factory.afterPropertiesSet();
        factory.destroy();
    }

    @Test
    @SuppressWarnings("serial")
    public void testSettingRejectedExecutionHandlerToNullForcesUseOfDefaultButIsOtherwiseCool() throws Exception {
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean() {
            @Override
            protected ScheduledExecutorService createExecutor(int poolSize, ThreadFactory threadFactory, RejectedExecutionHandler rejectedExecutionHandler) {
                Assert.assertNotNull("Bah; the setRejectedExecutionHandler(..) method must use a default RejectedExecutionHandler if a null arg is passed in.");
                return super.createExecutor(poolSize, threadFactory, rejectedExecutionHandler);
            }
        };
        factory.setScheduledExecutorTasks(new ScheduledExecutorTask[]{ new ScheduledExecutorFactoryBeanTests.NoOpScheduledExecutorTask() });
        factory.setRejectedExecutionHandler(null);// the null must not propagate

        factory.afterPropertiesSet();
        factory.destroy();
    }

    @Test
    public void testObjectTypeReportsCorrectType() throws Exception {
        ScheduledExecutorFactoryBean factory = new ScheduledExecutorFactoryBean();
        Assert.assertEquals(ScheduledExecutorService.class, factory.getObjectType());
    }

    private static class NoOpScheduledExecutorTask extends ScheduledExecutorTask {
        public NoOpScheduledExecutorTask() {
            super(new NoOpRunnable());
        }
    }
}

