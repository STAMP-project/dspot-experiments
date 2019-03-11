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
package org.axonframework.eventhandling.async;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.utils.EventTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Allard Buijze
 */
public class AsynchronousEventProcessingStrategyTest {
    private Executor executor;

    private AsynchronousEventProcessingStrategy testSubject;

    @Test
    public void testOrderingOfEvents() throws Exception {
        testSubject = new AsynchronousEventProcessingStrategy(Executors.newSingleThreadExecutor(), new SequentialPolicy());
        final List<EventMessage> ackedMessages = Collections.synchronizedList(new ArrayList<>());
        EventMessage<?> event1 = EventTestUtils.createEvent(1);
        EventMessage<?> event2 = EventTestUtils.createEvent(2);
        final Consumer<List<? extends EventMessage<?>>> processor = Mockito.mock(Consumer.class);
        CountDownLatch latch = new CountDownLatch(2);
        Mockito.doAnswer(( invocation) -> {
            List<? extends EventMessage<?>> events = ((List) (invocation.getArguments()[0]));
            events.forEach(( e) -> {
                ackedMessages.add(e);
                latch.countDown();
            });
            return null;
        }).when(processor).accept(ArgumentMatchers.anyList());
        new DefaultUnitOfWork(null).execute(() -> {
            testSubject.handle(Collections.singletonList(event1), processor);
            testSubject.handle(Collections.singletonList(event2), processor);
        });
        latch.await();
        InOrder inOrder = Mockito.inOrder(processor, processor);
        inOrder.verify(processor).accept(Arrays.asList(event1, event2));
        Assert.assertEquals(2, ackedMessages.size());
        Assert.assertEquals(event1, ackedMessages.get(0));
        Assert.assertEquals(event2, ackedMessages.get(1));
    }

    @Test
    public void testEventsScheduledForHandling() {
        EventMessage<?> message1 = EventTestUtils.createEvent("aggregate1", 1);
        EventMessage<?> message2 = EventTestUtils.createEvent("aggregate2", 1);
        testSubject.handle(Arrays.asList(message1, message2), Mockito.mock(Consumer.class));
        Mockito.verify(executor, Mockito.times(2)).execute(ArgumentMatchers.isA(Runnable.class));
    }

    @Test
    public void testEventsScheduledForHandlingWhenSurroundingUnitOfWorkCommits() {
        EventMessage<?> message1 = EventTestUtils.createEvent("aggregate1", 1);
        EventMessage<?> message2 = EventTestUtils.createEvent("aggregate2", 1);
        UnitOfWork<EventMessage<?>> uow = DefaultUnitOfWork.startAndGet(message1);
        uow.onPrepareCommit(( u) -> verify(executor, never()).execute(isA(.class)));
        testSubject.handle(Arrays.asList(message1, message2), Mockito.mock(Consumer.class));
        Mockito.verify(executor, Mockito.never()).execute(ArgumentMatchers.isA(Runnable.class));
        uow.commit();
        Mockito.verify(executor, Mockito.times(2)).execute(ArgumentMatchers.isA(Runnable.class));
    }
}

