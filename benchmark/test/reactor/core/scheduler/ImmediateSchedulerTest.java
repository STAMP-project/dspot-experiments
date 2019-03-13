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
package reactor.core.scheduler;


import Scannable.Attr.CANCELLED;
import Scannable.Attr.NAME;
import Scannable.Attr.TERMINATED;
import Schedulers.IMMEDIATE;
import java.util.concurrent.RejectedExecutionException;
import org.junit.Test;
import reactor.core.Exceptions;
import reactor.core.Scannable;
import reactor.core.scheduler.Scheduler.Worker;

import static Schedulers.IMMEDIATE;


/**
 *
 *
 * @author Stephane Maldini
 */
public class ImmediateSchedulerTest extends AbstractSchedulerTest {
    @Test
    public void directAndWorkerTimeSchedulingRejected() {
        Scheduler scheduler = scheduler();
        Worker worker = scheduler.createWorker();
        try {
            assertThatExceptionOfType(RejectedExecutionException.class).isThrownBy(() -> scheduler.schedule(() -> {
            }, 100, TimeUnit.MILLISECONDS)).isSameAs(Exceptions.failWithRejectedNotTimeCapable());
            assertThatExceptionOfType(RejectedExecutionException.class).isThrownBy(() -> scheduler.schedulePeriodically(() -> {
            }, 100, 100, TimeUnit.MILLISECONDS)).isSameAs(Exceptions.failWithRejectedNotTimeCapable());
            assertThatExceptionOfType(RejectedExecutionException.class).isThrownBy(() -> worker.schedule(() -> {
            }, 100, TimeUnit.MILLISECONDS)).isSameAs(Exceptions.failWithRejectedNotTimeCapable());
            assertThatExceptionOfType(RejectedExecutionException.class).isThrownBy(() -> worker.schedulePeriodically(() -> {
            }, 100, 100, TimeUnit.MILLISECONDS)).isSameAs(Exceptions.failWithRejectedNotTimeCapable());
        } finally {
            worker.dispose();
        }
    }

    @Test
    public void scanScheduler() {
        ImmediateScheduler s = ((ImmediateScheduler) (Schedulers.immediate()));
        assertThat(s.scan(NAME)).isEqualTo(IMMEDIATE);
        // don't test TERMINATED as this would shutdown the only instance
    }

    @Test
    public void scanWorker() {
        Worker worker = Schedulers.immediate().createWorker();
        Scannable s = ((Scannable) (worker));
        assertThat(s.scan(TERMINATED)).isFalse();
        assertThat(s.scan(CANCELLED)).isFalse();
        assertThat(s.scan(NAME)).isEqualTo(((IMMEDIATE) + ".worker"));
        worker.dispose();
        assertThat(s.scan(TERMINATED)).isTrue();
        assertThat(s.scan(CANCELLED)).isTrue();
    }
}

