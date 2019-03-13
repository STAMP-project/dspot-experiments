/**
 * Copyright 2014-2019 Real Logic Ltd.
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
package io.aeron;


import CountersReader.RECORD_ALLOCATED;
import io.aeron.driver.MediaDriver;
import io.aeron.status.ReadableCounter;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.concurrent.status.CountersReader;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CounterTest {
    private static final int COUNTER_TYPE_ID = 101;

    private static final String COUNTER_LABEL = "counter label";

    private final UnsafeBuffer labelBuffer = new UnsafeBuffer(new byte[CounterTest.COUNTER_LABEL.length()]);

    private Aeron clientA;

    private Aeron clientB;

    private MediaDriver driver;

    private final AvailableCounterHandler availableCounterHandlerClientA = Mockito.mock(AvailableCounterHandler.class);

    private final UnavailableCounterHandler unavailableCounterHandlerClientA = Mockito.mock(UnavailableCounterHandler.class);

    private AvailableCounterHandler availableCounterHandlerClientB = Mockito.mock(AvailableCounterHandler.class);

    private UnavailableCounterHandler unavailableCounterHandlerClientB = Mockito.mock(UnavailableCounterHandler.class);

    private volatile ReadableCounter readableCounter;

    @Test(timeout = 2000)
    public void shouldBeAbleToAddCounter() {
        launch();
        final Counter counter = clientA.addCounter(CounterTest.COUNTER_TYPE_ID, null, 0, 0, labelBuffer, 0, CounterTest.COUNTER_LABEL.length());
        Assert.assertFalse(counter.isClosed());
        Mockito.verify(availableCounterHandlerClientA, Mockito.timeout(1000)).onAvailableCounter(ArgumentMatchers.any(CountersReader.class), ArgumentMatchers.eq(counter.registrationId()), ArgumentMatchers.eq(counter.id()));
        Mockito.verify(availableCounterHandlerClientB, Mockito.timeout(1000)).onAvailableCounter(ArgumentMatchers.any(CountersReader.class), ArgumentMatchers.eq(counter.registrationId()), ArgumentMatchers.eq(counter.id()));
    }

    @Test(timeout = 2000)
    public void shouldBeAbleToAddReadableCounterWithinHandler() {
        availableCounterHandlerClientB = this::createReadableCounter;
        launch();
        final Counter counter = clientA.addCounter(CounterTest.COUNTER_TYPE_ID, null, 0, 0, labelBuffer, 0, CounterTest.COUNTER_LABEL.length());
        while (null == (readableCounter)) {
            SystemTest.checkInterruptedStatus();
            SystemTest.sleep(1);
        } 
        MatcherAssert.assertThat(readableCounter.state(), CoreMatchers.is(RECORD_ALLOCATED));
        MatcherAssert.assertThat(readableCounter.counterId(), CoreMatchers.is(counter.id()));
        MatcherAssert.assertThat(readableCounter.registrationId(), CoreMatchers.is(counter.registrationId()));
    }

    @Test(timeout = 2000)
    public void shouldCloseReadableCounterOnUnavailableCounter() {
        availableCounterHandlerClientB = this::createReadableCounter;
        unavailableCounterHandlerClientB = this::unavailableCounterHandler;
        launch();
        final Counter counter = clientA.addCounter(CounterTest.COUNTER_TYPE_ID, null, 0, 0, labelBuffer, 0, CounterTest.COUNTER_LABEL.length());
        while (null == (readableCounter)) {
            SystemTest.checkInterruptedStatus();
            SystemTest.sleep(1);
        } 
        Assert.assertTrue((!(readableCounter.isClosed())));
        MatcherAssert.assertThat(readableCounter.state(), CoreMatchers.is(RECORD_ALLOCATED));
        counter.close();
        while (!(readableCounter.isClosed())) {
            SystemTest.checkInterruptedStatus();
            SystemTest.sleep(1);
        } 
    }
}

