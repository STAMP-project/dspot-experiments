/**
 * Copyright 2011 LMAX Ltd.
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
package com.lmax.disruptor.dsl;


import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.dsl.stubs.SleepingEventHandler;
import com.lmax.disruptor.support.TestEvent;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerRepositoryTest {
    private ConsumerRepository<TestEvent> consumerRepository;

    private EventProcessor eventProcessor1;

    private EventProcessor eventProcessor2;

    private SleepingEventHandler handler1;

    private SleepingEventHandler handler2;

    private SequenceBarrier barrier1;

    private SequenceBarrier barrier2;

    @Test
    public void shouldGetBarrierByHandler() throws Exception {
        consumerRepository.add(eventProcessor1, handler1, barrier1);
        Assert.assertThat(consumerRepository.getBarrierFor(handler1), CoreMatchers.sameInstance(barrier1));
    }

    @Test
    public void shouldReturnNullForBarrierWhenHandlerIsNotRegistered() throws Exception {
        Assert.assertThat(consumerRepository.getBarrierFor(handler1), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldGetLastEventProcessorsInChain() throws Exception {
        consumerRepository.add(eventProcessor1, handler1, barrier1);
        consumerRepository.add(eventProcessor2, handler2, barrier2);
        consumerRepository.unMarkEventProcessorsAsEndOfChain(eventProcessor2.getSequence());
        final Sequence[] lastEventProcessorsInChain = consumerRepository.getLastSequenceInChain(true);
        Assert.assertThat(lastEventProcessorsInChain.length, CoreMatchers.equalTo(1));
        Assert.assertThat(lastEventProcessorsInChain[0], CoreMatchers.sameInstance(eventProcessor1.getSequence()));
    }

    @Test
    public void shouldRetrieveEventProcessorForHandler() throws Exception {
        consumerRepository.add(eventProcessor1, handler1, barrier1);
        Assert.assertThat(consumerRepository.getEventProcessorFor(handler1), CoreMatchers.sameInstance(eventProcessor1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenHandlerIsNotRegistered() throws Exception {
        consumerRepository.getEventProcessorFor(new SleepingEventHandler());
    }

    @Test
    public void shouldIterateAllEventProcessors() throws Exception {
        consumerRepository.add(eventProcessor1, handler1, barrier1);
        consumerRepository.add(eventProcessor2, handler2, barrier2);
        boolean seen1 = false;
        boolean seen2 = false;
        for (ConsumerInfo testEntryEventProcessorInfo : consumerRepository) {
            final EventProcessorInfo<?> eventProcessorInfo = ((EventProcessorInfo<?>) (testEntryEventProcessorInfo));
            if (((!seen1) && ((eventProcessorInfo.getEventProcessor()) == (eventProcessor1))) && ((eventProcessorInfo.getHandler()) == (handler1))) {
                seen1 = true;
            } else
                if (((!seen2) && ((eventProcessorInfo.getEventProcessor()) == (eventProcessor2))) && ((eventProcessorInfo.getHandler()) == (handler2))) {
                    seen2 = true;
                } else {
                    Assert.fail(("Unexpected eventProcessor info: " + testEntryEventProcessorInfo));
                }

        }
        Assert.assertTrue("Included eventProcessor 1", seen1);
        Assert.assertTrue("Included eventProcessor 2", seen2);
    }
}

