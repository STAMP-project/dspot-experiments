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
package org.springframework.security.scheduling;


import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;


/**
 * Test An implementation of {@link TaskScheduler}  invoking it whenever the trigger
 * indicates a next execution time.
 *
 * @author Richard Valdivieso
 * @since 5.1
 */
public class DelegatingSecurityContextTaskSchedulerTests {
    @Mock
    private TaskScheduler scheduler;

    @Mock
    private Runnable runnable;

    @Mock
    private Trigger trigger;

    private DelegatingSecurityContextTaskScheduler delegatingSecurityContextTaskScheduler;

    @Test(expected = IllegalArgumentException.class)
    public void testSchedulerIsNotNull() {
        delegatingSecurityContextTaskScheduler = new DelegatingSecurityContextTaskScheduler(null);
    }

    @Test
    public void testSchedulerWithRunnableAndTrigger() {
        delegatingSecurityContextTaskScheduler.schedule(runnable, trigger);
        Mockito.verify(scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Trigger.class));
    }

    @Test
    public void testSchedulerWithRunnableAndInstant() {
        Instant date = Instant.now();
        delegatingSecurityContextTaskScheduler.schedule(runnable, date);
        Mockito.verify(scheduler).schedule(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.any(Date.class));
    }

    @Test
    public void testScheduleAtFixedRateWithRunnableAndDate() {
        Date date = new Date(1544751374L);
        Duration duration = Duration.ofSeconds(4L);
        delegatingSecurityContextTaskScheduler.scheduleAtFixedRate(runnable, date, 1000L);
        Mockito.verify(scheduler).scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.isA(Date.class), ArgumentMatchers.eq(1000L));
    }

    @Test
    public void testScheduleAtFixedRateWithRunnableAndLong() {
        delegatingSecurityContextTaskScheduler.scheduleAtFixedRate(runnable, 1000L);
        Mockito.verify(scheduler).scheduleAtFixedRate(ArgumentMatchers.isA(Runnable.class), ArgumentMatchers.eq(1000L));
    }
}

