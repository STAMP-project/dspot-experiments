/**
 * Copyright 2014-2019 the original author or authors.
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
package de.codecentric.boot.admin.server.services;


import de.codecentric.boot.admin.server.domain.values.InstanceId;
import java.time.Duration;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import reactor.core.publisher.Mono;


public class IntervalCheckTest {
    private static final InstanceId INSTANCE_ID = InstanceId.of("Test");

    @SuppressWarnings("unchecked")
    private final Function<InstanceId, Mono<Void>> checkFn = Mockito.mock(Function.class, ( i) -> Mono.empty());

    private final IntervalCheck intervalCheck = new IntervalCheck("test", this.checkFn, Duration.ofMillis(10), Duration.ofMillis(10));

    @Test
    public void should_check_after_being_started() throws InterruptedException {
        this.intervalCheck.markAsChecked(IntervalCheckTest.INSTANCE_ID);
        this.intervalCheck.start();
        Thread.sleep(100);
        Mockito.verify(this.checkFn, Mockito.atLeastOnce()).apply(IntervalCheckTest.INSTANCE_ID);
    }

    @Test
    public void should_not_check_when_stopped() throws InterruptedException {
        this.intervalCheck.markAsChecked(IntervalCheckTest.INSTANCE_ID);
        this.intervalCheck.stop();
        Thread.sleep(100);
        Mockito.verify(this.checkFn, Mockito.never()).apply(ArgumentMatchers.any());
    }

    @Test
    public void should_not_check_in_retention_period() throws InterruptedException {
        this.intervalCheck.setMinRetention(Duration.ofSeconds(100));
        this.intervalCheck.markAsChecked(IntervalCheckTest.INSTANCE_ID);
        this.intervalCheck.start();
        Thread.sleep(100);
        Mockito.verify(this.checkFn, Mockito.never()).apply(ArgumentMatchers.any());
    }

    @Test
    public void should_recheck_after_retention_period() throws InterruptedException {
        this.intervalCheck.setMinRetention(Duration.ofMillis(10));
        this.intervalCheck.markAsChecked(IntervalCheckTest.INSTANCE_ID);
        this.intervalCheck.start();
        Thread.sleep(100);
        Mockito.verify(this.checkFn, Mockito.atLeast(2)).apply(IntervalCheckTest.INSTANCE_ID);
    }

    @Test
    public void should_check_after_error() throws InterruptedException {
        this.intervalCheck.markAsChecked(IntervalCheckTest.INSTANCE_ID);
        Mockito.when(this.checkFn.apply(ArgumentMatchers.any())).thenReturn(Mono.error(new RuntimeException("Test"))).thenReturn(Mono.empty());
        this.intervalCheck.start();
        Thread.sleep(100);
        Mockito.verify(this.checkFn, Mockito.atLeast(2)).apply(InstanceId.of("Test"));
    }
}

