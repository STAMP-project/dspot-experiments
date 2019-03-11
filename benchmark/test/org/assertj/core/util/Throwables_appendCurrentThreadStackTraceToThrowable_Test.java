/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Throwables#appendStackTraceInCurrentThreadToThrowable(Throwable, String)}.
 *
 * @author Alex Ruiz
 */
public class Throwables_appendCurrentThreadStackTraceToThrowable_Test {
    private AtomicReference<RuntimeException> exceptionReference;

    @Test
    public void should_add_stack_trace_of_current_thread() {
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            @Override
            public void run() {
                RuntimeException e = new RuntimeException("Thrown on purpose");
                exceptionReference.set(e);
                latch.countDown();
            }
        }.start();
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        RuntimeException thrown = exceptionReference.get();
        Throwables.appendStackTraceInCurrentThreadToThrowable(thrown, "should_add_stack_trace_of_current_thread");
        StackTraceElement[] stackTrace = thrown.getStackTrace();
        Assertions.assertThat(asString(stackTrace[0])).isEqualTo("org.assertj.core.util.Throwables_appendCurrentThreadStackTraceToThrowable_Test$1.run");
        Assertions.assertThat(asString(stackTrace[1])).isEqualTo("org.assertj.core.util.Throwables_appendCurrentThreadStackTraceToThrowable_Test.should_add_stack_trace_of_current_thread");
    }
}

