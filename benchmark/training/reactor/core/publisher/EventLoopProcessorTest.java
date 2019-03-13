/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.CAPACITY;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class EventLoopProcessorTest {
    EventLoopProcessor<String> test;

    @Test
    public void scanMain() {
        assertThat(test.scan(Attr.PARENT)).isNull();
        assertThat(test.scan(Attr.TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        assertThat(test.scan(Attr.TERMINATED)).isTrue();
        assertThat(test.scan(Attr.ERROR)).hasMessage("boom");
        assertThat(test.scan(CAPACITY)).isEqualTo(128);
    }

    Condition<ExecutorService> shutdown = new Condition(ExecutorService::isShutdown, "is shutdown");

    Condition<ExecutorService> terminated = new Condition(ExecutorService::isTerminated, "is terminated");

    @Test
    public void awaitTerminationImmediate() {
        assertThat(test.executor).isNotNull();
        test.executor.submit(() -> {
            try {
                Thread.sleep(100);
            } catch ( e) {
                e.printStackTrace();
            }
        });
        boolean shutResult = test.awaitAndShutdown(Duration.ofMillis((-1)));
        assertThat(test.executor).isNotNull().is(shutdown).isNot(terminated);
        assertThat(shutResult).isFalse();
    }

    @Test
    public void awaitTerminationNanosDuration() {
        assertThat(test.executor).isNotNull();
        test.executor.submit(() -> {
            try {
                Thread.sleep(100);
            } catch ( e) {
                e.printStackTrace();
            }
        });
        boolean shutResult = test.awaitAndShutdown(Duration.ofNanos(1000));
        assertThat(test.executor).isNotNull().is(shutdown).isNot(terminated);
        assertThat(shutResult).isFalse();
    }

    @Test
    public void awaitTerminationNanosLong() {
        assertThat(test.executor).isNotNull();
        test.executor.submit(() -> {
            try {
                Thread.sleep(100);
            } catch ( e) {
                e.printStackTrace();
            }
        });
        @SuppressWarnings("deprecation")
        boolean shutResult = test.awaitAndShutdown(1000, TimeUnit.NANOSECONDS);
        assertThat(test.executor).isNotNull().is(shutdown).isNot(terminated);
        assertThat(shutResult).isFalse();
    }

    @Test
    public void awaitAndShutdownInterrupt() throws InterruptedException {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        Mockito.doThrow(new InterruptedException("boom")).when(executor).awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        EventLoopProcessor<String> interruptingProcessor = EventLoopProcessorTest.initProcessor(executor);
        boolean result = interruptingProcessor.awaitAndShutdown(Duration.ofMillis(100));
        assertThat(Thread.currentThread().isInterrupted()).as("interrupted").isTrue();
        assertThat(result).as("await failed").isFalse();
    }

    @Test
    public void awaitAndShutdownLongInterrupt() throws InterruptedException {
        ExecutorService executor = Mockito.mock(ExecutorService.class);
        Mockito.doThrow(new InterruptedException("boom")).when(executor).awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        EventLoopProcessor<String> interruptingProcessor = EventLoopProcessorTest.initProcessor(executor);
        @SuppressWarnings("deprecation")
        boolean result = interruptingProcessor.awaitAndShutdown(100, TimeUnit.MILLISECONDS);
        assertThat(Thread.currentThread().isInterrupted()).as("interrupted").isTrue();
        assertThat(result).as("await failed").isFalse();
    }
}

