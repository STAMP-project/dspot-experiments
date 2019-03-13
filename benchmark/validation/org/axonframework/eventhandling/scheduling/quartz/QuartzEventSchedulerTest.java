/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.eventhandling.scheduling.quartz;


import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.axonframework.common.transaction.Transaction;
import org.axonframework.common.transaction.TransactionManager;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.utils.AssertUtils;
import org.axonframework.utils.MockException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;


/**
 *
 *
 * @author Allard Buijze
 */
public class QuartzEventSchedulerTest {
    private static final String GROUP_ID = "TestGroup";

    private QuartzEventScheduler testSubject;

    private EventBus eventBus;

    private Scheduler scheduler;

    @Test
    public void testScheduleJob() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            latch.countDown();
            return null;
        }).when(eventBus).publish(ArgumentMatchers.isA(EventMessage.class));
        ScheduleToken token = testSubject.schedule(Duration.ofMillis(30), buildTestEvent());
        Assert.assertTrue(token.toString().contains("Quartz"));
        Assert.assertTrue(token.toString().contains(QuartzEventSchedulerTest.GROUP_ID));
        latch.await(1, TimeUnit.SECONDS);
        Mockito.verify(eventBus).publish(ArgumentMatchers.isA(EventMessage.class));
    }

    @Test
    public void testScheduleJobTransactionalUnitOfWork() throws InterruptedException {
        Transaction mockTransaction = Mockito.mock(Transaction.class);
        final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
        Mockito.when(transactionManager.startTransaction()).thenReturn(mockTransaction);
        testSubject = QuartzEventScheduler.builder().scheduler(scheduler).eventBus(eventBus).transactionManager(transactionManager).build();
        testSubject.setGroupIdentifier(QuartzEventSchedulerTest.GROUP_ID);
        final CountDownLatch latch = new CountDownLatch(1);
        Mockito.doAnswer(( invocation) -> {
            latch.countDown();
            return null;
        }).when(mockTransaction).commit();
        ScheduleToken token = testSubject.schedule(Duration.ofMillis(30), buildTestEvent());
        Assert.assertTrue(token.toString().contains("Quartz"));
        Assert.assertTrue(token.toString().contains(QuartzEventSchedulerTest.GROUP_ID));
        latch.await(1, TimeUnit.SECONDS);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Mockito.verify(mockTransaction).commit());
        InOrder inOrder = Mockito.inOrder(transactionManager, eventBus, mockTransaction);
        inOrder.verify(transactionManager).startTransaction();
        inOrder.verify(eventBus).publish(ArgumentMatchers.isA(EventMessage.class));
        inOrder.verify(mockTransaction).commit();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testScheduleJobTransactionalUnitOfWorkFailingTransaction() throws InterruptedException {
        final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);
        final CountDownLatch latch = new CountDownLatch(1);
        Mockito.when(transactionManager.startTransaction()).thenAnswer(( i) -> {
            latch.countDown();
            throw new MockException();
        });
        testSubject = QuartzEventScheduler.builder().scheduler(scheduler).eventBus(eventBus).transactionManager(transactionManager).build();
        testSubject.setGroupIdentifier(QuartzEventSchedulerTest.GROUP_ID);
        ScheduleToken token = testSubject.schedule(Duration.ofMillis(30), buildTestEvent());
        Assert.assertTrue(token.toString().contains("Quartz"));
        Assert.assertTrue(token.toString().contains(QuartzEventSchedulerTest.GROUP_ID));
        latch.await(1, TimeUnit.SECONDS);
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Mockito.verify(transactionManager).startTransaction());
        InOrder inOrder = Mockito.inOrder(transactionManager, eventBus);
        inOrder.verify(transactionManager).startTransaction();
        inOrder.verifyNoMoreInteractions();
        Assert.assertFalse(CurrentUnitOfWork.isStarted());
    }

    @Test
    public void testCancelJob() throws SchedulerException {
        ScheduleToken token = testSubject.schedule(Duration.ofMillis(1000), buildTestEvent());
        Assert.assertEquals(1, scheduler.getJobKeys(GroupMatcher.groupEquals(QuartzEventSchedulerTest.GROUP_ID)).size());
        testSubject.cancelSchedule(token);
        Assert.assertEquals(0, scheduler.getJobKeys(GroupMatcher.groupEquals(QuartzEventSchedulerTest.GROUP_ID)).size());
        scheduler.shutdown(true);
        Mockito.verify(eventBus, Mockito.never()).publish(ArgumentMatchers.isA(EventMessage.class));
    }
}

