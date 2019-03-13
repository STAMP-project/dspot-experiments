/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.jms.listener;


import BackOffExecution.STOP;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.BackOffExecution;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class DefaultMessageListenerContainerTests {
    @Test
    public void applyBackOff() {
        BackOff backOff = Mockito.mock(BackOff.class);
        BackOffExecution execution = Mockito.mock(BackOffExecution.class);
        BDDMockito.given(execution.nextBackOff()).willReturn(STOP);
        BDDMockito.given(backOff.start()).willReturn(execution);
        DefaultMessageListenerContainer container = createContainer(createFailingContainerFactory());
        container.setBackOff(backOff);
        container.start();
        Assert.assertEquals(true, container.isRunning());
        container.refreshConnectionUntilSuccessful();
        Assert.assertEquals(false, container.isRunning());
        Mockito.verify(backOff).start();
        Mockito.verify(execution).nextBackOff();
    }

    @Test
    public void applyBackOffRetry() {
        BackOff backOff = Mockito.mock(BackOff.class);
        BackOffExecution execution = Mockito.mock(BackOffExecution.class);
        BDDMockito.given(execution.nextBackOff()).willReturn(50L, STOP);
        BDDMockito.given(backOff.start()).willReturn(execution);
        DefaultMessageListenerContainer container = createContainer(createFailingContainerFactory());
        container.setBackOff(backOff);
        container.start();
        container.refreshConnectionUntilSuccessful();
        Assert.assertEquals(false, container.isRunning());
        Mockito.verify(backOff).start();
        Mockito.verify(execution, Mockito.times(2)).nextBackOff();
    }

    @Test
    public void recoverResetBackOff() {
        BackOff backOff = Mockito.mock(BackOff.class);
        BackOffExecution execution = Mockito.mock(BackOffExecution.class);
        BDDMockito.given(execution.nextBackOff()).willReturn(50L, 50L, 50L);// 3 attempts max

        BDDMockito.given(backOff.start()).willReturn(execution);
        DefaultMessageListenerContainer container = createContainer(createRecoverableContainerFactory(1));
        container.setBackOff(backOff);
        container.start();
        container.refreshConnectionUntilSuccessful();
        Assert.assertEquals(true, container.isRunning());
        Mockito.verify(backOff).start();
        Mockito.verify(execution, Mockito.times(1)).nextBackOff();// only on attempt as the second one lead to a recovery

    }

    @Test
    public void runnableIsInvokedEvenIfContainerIsNotRunning() throws InterruptedException {
        DefaultMessageListenerContainer container = createRunningContainer();
        container.stop();
        // container is stopped but should nevertheless invoke the runnable argument
        DefaultMessageListenerContainerTests.TestRunnable runnable2 = new DefaultMessageListenerContainerTests.TestRunnable();
        container.stop(runnable2);
        runnable2.waitForCompletion();
    }

    private static class TestRunnable implements Runnable {
        private final CountDownLatch countDownLatch = new CountDownLatch(1);

        @Override
        public void run() {
            this.countDownLatch.countDown();
        }

        public void waitForCompletion() throws InterruptedException {
            this.countDownLatch.await(2, TimeUnit.SECONDS);
            Assert.assertEquals("callback was not invoked", 0, this.countDownLatch.getCount());
        }
    }
}

